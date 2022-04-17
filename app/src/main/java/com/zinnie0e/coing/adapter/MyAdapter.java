package com.zinnie0e.coing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zinnie0e.coing.R;
import com.zinnie0e.coing.data.ConvData;

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

        TextView txtSentence = (TextView)view.findViewById(R.id.txtSentence);
        TextView txtStatus = (TextView)view.findViewById(R.id.txtStatus);
        TextView txtIsLook = (TextView)view.findViewById(R.id.txtIsLook);

        txtSentence.setText(conv_data.get(position).getConv_en());

        //btnLook 클릭했을때 기능 추가
        txtIsLook.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (isEnglish){
                    txtIsLook.setText(R.string.is_look_ko); //선글라스
                    txtSentence.setText("한글로바꿔!!" + conv_data.get(position).getConv_en() + "(ko)");
                }
                else {
                    txtIsLook.setText(R.string.is_look_en);
                    txtSentence.setText(conv_data.get(position).getConv_en());
                }
                isEnglish = !isEnglish;
            }
        });
        return view;
    }
}
