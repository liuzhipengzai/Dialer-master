package com.ctao.dialer.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


import com.ctao.dialer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.dlc.commonlibrary.utils.DialogUtil;

public class CommonDialog extends Dialog {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_comit)
    TextView tvComit;
    @BindView(R.id.tv_data)
    TextView tvData;

    public CommonDialog(Context context, int type, String data , ComitCallBack comitCallBack) {
        super(context, R.style.CommonDialogStyle);
        setContentView(R.layout.dialog_delete);
        this.callBack = comitCallBack;
        DialogUtil.setGravity(this, Gravity.CENTER);
        ButterKnife.bind(this);
        //type=1确定和删除 2确定和电话呼叫
        if (type == 1) {

        }else if(type == 2){
            tvComit.setText("呼叫");
            tvComit.setTextColor(context.getResources().getColor(R.color.color_1977FD));
        }
        tvData.setText(data);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvComit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.comit();
                dismiss();
            }
        });
    }

    public ComitCallBack callBack;

    public interface ComitCallBack {
        void comit();
    }
}
