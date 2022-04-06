package com.zinnie0e.coing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ConvData> conv_data;
    Boolean isEnglish = true;

    public MyAdapter(Context context, ArrayList<ConvData> data) {
        mContext = context;
        conv_data = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return conv_data.size();
    }

    @Override
    public ConvData getItem(int position) {
        return conv_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_contents_list, null);

        TextView txtConvEn = (TextView)view.findViewById(R.id.txtConvEn);
        TextView txtStatus = (TextView)view.findViewById(R.id.txtStatus);
        TextView txtIsLook = (TextView)view.findViewById(R.id.txtIsLook);

        txtConvEn.setText(conv_data.get(position).getConv_en());

        //btnLook 클릭했을때 기능 추가
        txtIsLook.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                isEnglish = !isEnglish;
                if(isEnglish) txtIsLook.setText("🕶");
                else txtIsLook.setText("\uD83D\uDC40");
            }
        });
        return view;
    }
}
