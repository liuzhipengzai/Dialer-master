package com.ctao.dialer.util;

import android.support.annotation.NonNull;
import android.widget.TextView;


import com.ctao.dialer.R;
import com.ctao.dialer.http.CallRecordBean;

import cn.dlc.commonlibrary.ui.adapter.BaseRecyclerAdapter;

public class CallRecordAdapter extends BaseRecyclerAdapter<CallRecordBean.DataBean> {


  @Override
  public int getItemLayoutId(int viewType) {
    return R.layout.item_call_record;
  }

  @Override
  public void onBindViewHolder(@NonNull CommonHolder holder, int position) {
    CallRecordBean.DataBean dataBean = getItem(position);
    TextView item_tv_phone = holder.getText(R.id.item_tv_phone);
    TextView item_tv_time = holder.getText(R.id.item_tv_time);

    item_tv_phone.setText(dataBean.getCalled_number());
    item_tv_time.setText(dataBean.getStarttime());
  }
}
