package com.ctao.dialer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Spanned;
import android.text.method.DialerKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ctao.dialer.http.CallBean;
import com.ctao.dialer.http.CallRecordBean;
import com.ctao.dialer.http.LoginHttp;
import com.ctao.dialer.update.AppInfo;
import com.ctao.dialer.update.AppUpdater;
import com.ctao.dialer.update.AppUtil;
import com.ctao.dialer.update.INetCallBack;
import com.ctao.dialer.update.ShowAppInfoDialog;
import com.ctao.dialer.util.CallRecordAdapter;
import com.ctao.dialer.util.CommonDialog;
import com.ctao.dialer.util.PermissionUtil;
import com.ctao.dialer.util.PhoneFormatCheckUtils;
import com.ctao.dialer.widght.DigitsEditText;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashSet;

import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;
import cn.dlc.commonlibrary.ui.dialog.WaitingDialog;
import cn.dlc.commonlibrary.utils.ToastUtil;

/**
 * Created by A Miracle on 2016/4/27.
 */
public class MainActivity extends FragmentActivity implements OnClickListener, OnLongClickListener, OnTouchListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private static final char PAUSE = ',';//延长暂停时间2秒
  private static final char WAIT = ';';//延长等待时间

  /**
   * Stream type used to play the DTMF tones off call, and mapped to the volume control keys (流类型用于播放DTMF音调打电话,和映射到音量控制键)
   */
  private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

  /**
   * The DTMF tone volume relative to other sounds in the stream (DTMF语气体积相对于其他的声音流)
   */
  private static final int TONE_RELATIVE_VOLUME = 80;

  /**
   * The length of DTMF tones in milliseconds(DTMF音调以毫秒为单位的长度)
   */
  private static final int TONE_LENGTH_INFINITE = 300;

  // determines if we want to playback local DTMF tones.(确定如果我们想播放本地DTMF音调。)
  private boolean mDTMFToneEnabled;

  /**
   * Set of dialpad keys that are currently being pressed
   */
  private final HashSet<View> mPressedDialpadKeys = new HashSet<View>(12);

  private ToneGenerator mToneGenerator; //音频发生器
  private final Object mToneGeneratorLock = new Object(); //Lock

  private ImageButton deleteButton;
  private DigitsEditText mDigits;

  private ImageView iv_call_record;
  private ImageView iv_check;
  private ImageView iv_exit;
  private RecyclerView mRecyclerView;
  private SmartRefreshLayout mRefreshlayout;
  private CallRecordAdapter adapter ;;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    initEven();
    initListener();
    checkPermission();
    initRecycler();
    initRefreshLayout();
  }

  @Override
  protected void onStart() {
    super.onStart();
    synchronized (mToneGeneratorLock) {
      if (mToneGenerator == null) {
        try {
          mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE, TONE_RELATIVE_VOLUME);
        } catch (RuntimeException e) {
          Log.w(TAG, "Exception caught while creating local tone generator: " + e);
          mToneGenerator = null;
        }
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    final ContentResolver contentResolver = getContentResolver();
    mDTMFToneEnabled = Settings.System.getInt(contentResolver,
      Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;

    mPressedDialpadKeys.clear();
  }

  @Override
  protected void onPause() {
    super.onPause();
    mPressedDialpadKeys.clear();
  }

  @Override
  protected void onStop() {
    super.onStop();
    synchronized (mToneGeneratorLock) {
      if (mToneGenerator != null) {
        mToneGenerator.release();
        mToneGenerator = null;
      }
    }
  }

  private void initView() {
    deleteButton = (ImageButton) findViewById(R.id.deleteButton);
    mDigits = (DigitsEditText) findViewById(R.id.digits);
    iv_call_record = (ImageView) findViewById(R.id.iv_call_record);
    iv_check = (ImageView) findViewById(R.id.iv_check);
    iv_exit = (ImageView) findViewById(R.id.iv_exit);
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    mRefreshlayout = (SmartRefreshLayout) findViewById(R.id.refreshlayout);
  }
  private void initRefreshLayout(){
    mRefreshlayout.setOnRefreshListener(refreshLayout -> {
      initRecycler();
    });
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    adapter = new CallRecordAdapter();
    mRecyclerView.setAdapter(adapter);
  }

  private void initRecycler(){

    LoginHttp.get().callRecord(new Bean01Callback<CallRecordBean>() {
      @Override
      public void onSuccess(CallRecordBean bean) {
        adapter.setNewData(bean.getData());
        mRefreshlayout.finishRefresh();
      }

      @Override
      public void onFailure(String message, Throwable tr) {
        ToastUtil.showOne(MainActivity.this, message);
      }
    });
  }

  private void checkPermission() {
    PermissionUtil.getInstance().checkPermission(this, true, new PermissionUtil.PermissionCallBack() {
      @Override
      public void onSuccess() {

      }

      @Override
      public void onFail() {
        finish();
      }
    }, Constants.Permissions);
  }

  private void initListener() {
    mDigits.setKeyListener(new DialerKeyListener() {
      @Override
      public CharSequence filter(CharSequence source, int start, int end,
                                 Spanned dest, int dstart, int dend) {
        final String converted = PhoneNumberUtils.convertKeypadLettersToDigits(
          replaceUnicodeDigits(source.toString()));
        // PhoneNumberUtils.replaceUnicodeDigits performs a character for character replacement,
        // so we can assume that start and end positions should remain unchanged.
        CharSequence result = super.filter(converted, start, end, dest, dstart, dend);
        if (result == null) {
          if (source.equals(converted)) {
            // There was no conversion or filtering performed. Just return null according to
            // the behavior of DialerKeyListener.
            return null;
          } else {
            // filter returns null if the charsequence is to be returned unchanged/unfiltered.
            // But in this case we do want to return a modified character string (even if
            // none of the characters in the modified string are filtered). So if
            // result == null we return the unfiltered but converted numeric string instead.
            return converted.subSequence(start, end);
          }
        }
        return result;
      }

      /**
       * Replaces all unicode(e.g. Arabic, Persian) digits with their decimal digit equivalents.
       *
       * @param number the number to perform the replacement on.
       * @return the replaced number.
       */
      public String replaceUnicodeDigits(String number) {
        StringBuilder normalizedDigits = new StringBuilder(number.length());
        for (char c : number.toCharArray()) {
          int digit = Character.digit(c, 10);
          if (digit != -1) {
            normalizedDigits.append(digit);
          } else {
            normalizedDigits.append(c);
          }
        }
        return normalizedDigits.toString();
      }
    });
    mDigits.setOnClickListener(this);
    mDigits.setOnLongClickListener(this);
    mDigits.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

    findViewById(R.id.dv_1).setOnTouchListener(this);
    findViewById(R.id.dv_2).setOnTouchListener(this);
    findViewById(R.id.dv_3).setOnTouchListener(this);
    findViewById(R.id.dv_4).setOnTouchListener(this);
    findViewById(R.id.dv_5).setOnTouchListener(this);
    findViewById(R.id.dv_6).setOnTouchListener(this);
    findViewById(R.id.dv_7).setOnTouchListener(this);
    findViewById(R.id.dv_8).setOnTouchListener(this);
    findViewById(R.id.dv_9).setOnTouchListener(this);
    findViewById(R.id.dv_xing).setOnTouchListener(this);
    findViewById(R.id.dv_0).setOnTouchListener(this);
    findViewById(R.id.dv_jing).setOnTouchListener(this);

    findViewById(R.id.dv_xing).setOnLongClickListener(this);
    findViewById(R.id.dv_0).setOnLongClickListener(this);
    findViewById(R.id.dv_jing).setOnLongClickListener(this);

    findViewById(R.id.ib_call).setOnClickListener(this);

    iv_call_record.setOnClickListener(this);
    iv_check.setOnClickListener(this);
    iv_exit.setOnClickListener(this);

    deleteButton.setOnClickListener(this);
    deleteButton.setOnLongClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.digits:
        if (mDigits.length() != 0) {
          mDigits.setCursorVisible(true);
        }
        break;
      case R.id.deleteButton:
        keyPressed(KeyEvent.KEYCODE_DEL);
        break;
      case R.id.ib_call:
        //13450031402
        String mobile = mDigits.getText().toString().replaceAll(" ", "");
        if (mobile.length() != 11 || !PhoneFormatCheckUtils.isChinaPhoneLegal(mobile)) {
          ToastUtil.showOne(this, "请输入正确的手机号");
        } else {
          Call(mobile);
        }
        break;
      case R.id.iv_call_record:
        Intent intent = new Intent(this, CallRecordActivity.class);
        startActivity(intent);
        break;
      case R.id.iv_check:
        Update();
        break;
      case R.id.iv_exit:
        CommonDialog dialog = new CommonDialog(this, 1, "确定退出吗?", new CommonDialog.ComitCallBack() {
          @Override
          public void comit() {
            LoginHttp.get().exit(new Bean01Callback<CallRecordBean>() {
              @Override
              public void onSuccess(CallRecordBean callRecordBean) {
                LoginActivity.startAct(MainActivity.this, 2);
              }

              @Override
              public void onFailure(String message, Throwable tr) {
                ToastUtil.show(MainActivity.this, message);
              }
            });

          }
        });
        dialog.show();
        break;
      default:
        break;
    }
  }

  private void Call(String mobile) {
    showWaitingDialog("请稍等", false);

    LoginHttp.get().makeCall(mobile, new Bean01Callback<CallBean>() {
      @SuppressLint("MissingPermission")
      @Override
      public void onSuccess(CallBean callBean) {
        dismissWaitingDialog();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + callBean.getData().getAxb_number()));
        startActivity(intent);
      }

      @Override
      public void onFailure(String message, Throwable tr) {
        dismissWaitingDialog();
        ToastUtil.showOne(MainActivity.this, message);
      }
    });
    Editable digits = mDigits.getText();
    if (digits != null) {
      digits.clear();
    }
  }

  private void keyPressed(int keyCode) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_1:
        playTone(ToneGenerator.TONE_DTMF_1, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_2:
        playTone(ToneGenerator.TONE_DTMF_2, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_3:
        playTone(ToneGenerator.TONE_DTMF_3, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_4:
        playTone(ToneGenerator.TONE_DTMF_4, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_5:
        playTone(ToneGenerator.TONE_DTMF_5, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_6:
        playTone(ToneGenerator.TONE_DTMF_6, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_7:
        playTone(ToneGenerator.TONE_DTMF_7, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_8:
        playTone(ToneGenerator.TONE_DTMF_8, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_9:
        playTone(ToneGenerator.TONE_DTMF_9, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_0:
        playTone(ToneGenerator.TONE_DTMF_0, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_POUND:
        playTone(ToneGenerator.TONE_DTMF_P, TONE_LENGTH_INFINITE);
        break;
      case KeyEvent.KEYCODE_STAR:
        playTone(ToneGenerator.TONE_DTMF_S, TONE_LENGTH_INFINITE);
        break;
      default:
        break;
    }

    // 振动 mHaptic.vibrate();
    KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
    mDigits.onKeyDown(keyCode, event);

    // 如果光标在文本的最后我们隐藏它。
    final int length = mDigits.length();
    if (length == mDigits.getSelectionStart() && length == mDigits.getSelectionEnd()) {
      mDigits.setCursorVisible(false);
    }
  }

  /**
   * Play the specified tone for the specified milliseconds
   * <p>
   * The tone is played locally, using the audio stream for phone calls.
   * Tones are played only if the "Audible touch tones" user preference
   * is checked, and are NOT played if the device is in silent mode.
   * <p>
   * The tone length can be -1, meaning "keep playing the tone." If the caller does so, it should
   * call stopTone() afterward.
   *
   * @param tone       a tone code from {@link ToneGenerator}
   * @param durationMs tone length.
   */
  private void playTone(int tone, int durationMs) {
    // if local tone playback is disabled, just return.
    if (!mDTMFToneEnabled) {
      return;
    }

    // Also do nothing if the phone is in silent mode.
    // We need to re-check the ringer mode for *every* playTone()
    // call, rather than keeping a local flag that's updated in
    // onResume(), since it's possible to toggle silent mode without
    // leaving the current activity (via the ENDCALL-longpress menu.)
    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int ringerMode = audioManager.getRingerMode();
    if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
      || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
      return;
    }

    synchronized (mToneGeneratorLock) {
      if (mToneGenerator == null) {
        Log.w(TAG, "playTone: mToneGenerator == null, tone: " + tone);
        return;
      }

      // Start the new tone (will stop any playing tone)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
        mToneGenerator.startTone(tone, durationMs);
      }
    }
  }

  /**
   * Stop the tone if it is played.
   */
  private void stopTone() {
    // if local tone playback is disabled, just return.
    if (!mDTMFToneEnabled) {
      return;
    }
    synchronized (mToneGeneratorLock) {
      if (mToneGenerator == null) {
        Log.w(TAG, "stopTone: mToneGenerator == null");
        return;
      }
      mToneGenerator.stopTone();
    }
  }

  @Override
  public boolean onLongClick(View v) {
    switch (v.getId()) {
      case R.id.digits:
        if (mDigits.length() != 0) {
          mDigits.setCursorVisible(true);
        }
        break;
      case R.id.deleteButton:
        Editable digits = mDigits.getText();
        if (digits != null) {
          digits.clear();
        }
        return true;
      case R.id.dv_0:
        // Remove tentative input ('0') done by onTouch().
        removePreviousDigitIfPossible();

        keyPressed(KeyEvent.KEYCODE_PLUS);

        // Stop tone immediately
        stopTone();
        mPressedDialpadKeys.remove(v);
        return true;
      case R.id.dv_xing:
        // Remove tentative input ('*') done by onTouch().
        removePreviousDigitIfPossible();

        updateDialString(PAUSE);

        // Stop tone immediately
        stopTone();
        mPressedDialpadKeys.remove(v);
        return true;
      case R.id.dv_jing:
        // Remove tentative input ('#') done by onTouch().
        removePreviousDigitIfPossible();

        updateDialString(WAIT);

        // Stop tone immediately
        stopTone();
        mPressedDialpadKeys.remove(v);
        return true;
    }
    return false;
  }

  /**
   * Remove the digit just before the current position. This can be used if we want to replace
   * the previous digit or cancel previously entered character.
   */
  private void removePreviousDigitIfPossible() {
    final int currentPosition = mDigits.getSelectionStart();
    if (currentPosition > 0) {
      mDigits.setSelection(currentPosition);
      mDigits.getText().delete(currentPosition - 1, currentPosition);
    }
  }

  /**
   * Updates the dial string (mDigits) after inserting a Pause character (,)
   * or Wait character (;).
   */
  private void updateDialString(char newDigit) {
    if (newDigit != WAIT && newDigit != PAUSE) {
      throw new IllegalArgumentException(
        "Not expected for anything other than PAUSE & WAIT");
    }

    int selectionStart;
    int selectionEnd;

    // SpannableStringBuilder editable_text = new SpannableStringBuilder(mDigits.getText());
    int anchor = mDigits.getSelectionStart();
    int point = mDigits.getSelectionEnd();

    selectionStart = Math.min(anchor, point);
    selectionEnd = Math.max(anchor, point);

    if (selectionStart == -1) {
      selectionStart = selectionEnd = mDigits.length();
    }

    Editable digits = mDigits.getText();

    if (canAddDigit(digits, selectionStart, selectionEnd, newDigit)) {
      digits.replace(selectionStart, selectionEnd, Character.toString(newDigit));

      if (selectionStart != selectionEnd) {
        // Unselect: back to a regular cursor, just pass the character inserted.
        mDigits.setSelection(selectionStart + 1);
      }
    }
  }

  /**
   * Returns true of the newDigit parameter can be added at the current selection
   * point, otherwise returns false.
   * Only prevents input of WAIT and PAUSE digits at an unsupported position.
   * Fails early if start == -1 or start is larger than end.
   */
  @VisibleForTesting
  /* package */ static boolean canAddDigit(CharSequence digits, int start, int end,
                                           char newDigit) {
    if (newDigit != WAIT && newDigit != PAUSE) {
      throw new IllegalArgumentException(
        "Should not be called for anything other than PAUSE & WAIT");
    }

    // False if no selection, or selection is reversed (end < start)
    if (start == -1 || end < start) {
      return false;
    }

    // unsupported selection-out-of-bounds state
    if (start > digits.length() || end > digits.length()) return false;

    // Special digit cannot be the first digit
    if (start == 0) return false;

    if (newDigit == WAIT) {
      // preceding char is ';' (WAIT)
      if (digits.charAt(start - 1) == WAIT) return false;

      // next char is ';' (WAIT)
      if ((digits.length() > end) && (digits.charAt(end) == WAIT)) return false;
    }

    return true;
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        switch (v.getId()) {
          case R.id.dv_1:
            keyPressed(KeyEvent.KEYCODE_1);
            break;
          case R.id.dv_2:
            keyPressed(KeyEvent.KEYCODE_2);
            break;
          case R.id.dv_3:
            keyPressed(KeyEvent.KEYCODE_3);
            break;
          case R.id.dv_4:
            keyPressed(KeyEvent.KEYCODE_4);
            break;
          case R.id.dv_5:
            keyPressed(KeyEvent.KEYCODE_5);
            break;
          case R.id.dv_6:
            keyPressed(KeyEvent.KEYCODE_6);
            break;
          case R.id.dv_7:
            keyPressed(KeyEvent.KEYCODE_7);
            break;
          case R.id.dv_8:
            keyPressed(KeyEvent.KEYCODE_8);
            break;
          case R.id.dv_9:
            keyPressed(KeyEvent.KEYCODE_9);
            break;
          case R.id.dv_0:
            keyPressed(KeyEvent.KEYCODE_0);
            break;
          case R.id.dv_xing:
            keyPressed(KeyEvent.KEYCODE_STAR);
            break;
          case R.id.dv_jing:
            keyPressed(KeyEvent.KEYCODE_POUND);
            break;
          default:
            break;
        }
        mPressedDialpadKeys.add(v);

      }
      break;
      case MotionEvent.ACTION_MOVE:
        break;
      case MotionEvent.ACTION_UP:
        mPressedDialpadKeys.remove(v);
        if (mPressedDialpadKeys.isEmpty()) {
          stopTone();
        }
        break;
      default:
        break;
    }
    return false;
  }

  private void Update() {

    AppUpdater.getInstance().getNetManager().get(Constants.APP_VERSION_URL, new INetCallBack() {
      @Override
      public void success(String response) {

        //1.解析json;http://59.110.162.30/app_updater_version.json
        final AppInfo appInfo = AppInfo.parse1(response);
        if (appInfo == null) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(MainActivity.this, "服务器返回版本信息错误", Toast.LENGTH_SHORT).show();
            }
          });
        }
        //2.做版本匹配
        long version = Integer.valueOf(appInfo.newversion);
        if (version <= AppUtil.getVersionCode(MainActivity.this)) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              Toast.makeText(MainActivity.this, "已经是最新版本了!!!", Toast.LENGTH_SHORT).show();
            }
          });
        } else {
          //弹框显示版本信息
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              ShowAppInfoDialog.show(MainActivity.this, appInfo);
            }
          });
        }
      }

      @Override
      public void failed(Throwable throwable) {
        mHandler.post(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(MainActivity.this, "网络错误，更新版本失败", Toast.LENGTH_SHORT).show();
          }
        });
      }
    }, MainActivity.this);
  }

  private Handler mHandler = new Handler(Looper.getMainLooper());
  /*****************************************************************************************/
  /**
   * 显示等待对话框
   *
   * @param text
   * @param cancelable
   */
  public void showWaitingDialog(String text, boolean cancelable) {
    if (mWaitingDialog == null) {
      mWaitingDialog = WaitingDialog.newDialog(this).setMessage(text);
    }
    if (mWaitingDialog.isShowing()) {
      mWaitingDialog.dismiss();
    }
    mWaitingDialog.setCancelable(cancelable);
    mWaitingDialog.show();
  }

  public void dismissWaitingDialog() {
    if (mWaitingDialog != null) {
      mWaitingDialog.dismiss();
      mWaitingDialog = null;
    }
  }

  WaitingDialog mWaitingDialog;

  //处理每个Activity都执行的逻辑
  private void initEven() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//隐藏状态栏但不隐藏状态栏字体
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//实现状态栏文字颜色为暗色

    }
  }
}
