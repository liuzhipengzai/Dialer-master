package com.ctao.dialer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;


import com.ctao.dialer.http.CallRecordBean;
import com.ctao.dialer.http.LoginHttp;
import com.ctao.dialer.util.CallRecordAdapter;

import butterknife.BindView;
import cn.dlc.commonlibrary.okgo.callback.Bean01Callback;

public class CallRecordActivity extends BaseActivity {

  @BindView(R.id.recycler_view)
  RecyclerView mRecyclerView;
  @BindView(R.id.iv_finish)
  ImageView iv_finish;
  private int page = 1, size = 10;
  CallRecordAdapter adapter;
  @Override
  protected int getLayoutId() {
    return R.layout.activity_call_record;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initRecycler();

    iv_finish.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void initRecycler() {

    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    adapter = new CallRecordAdapter();
    mRecyclerView.setAdapter(adapter);

    LoginHttp.get().callRecord(new Bean01Callback<CallRecordBean>() {
      @Override
      public void onSuccess(CallRecordBean bean) {
        adapter.setNewData(bean.getData());
      }

      @Override
      public void onFailure(String message, Throwable tr) {
        showOneToast(message);
      }
    });

  }


}
