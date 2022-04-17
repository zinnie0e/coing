package com.zinnie0e.coing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zinnie0e.coing.R;
import com.zinnie0e.coing.data.WordData;

import java.util.ArrayList;

public class AdapterWord extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WordData> word_data;


    public AdapterWord(Context context, ArrayList<WordData> data) {
        mContext = context;
        word_data = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return word_data.size();
    }

    @Override
    public WordData getItem(int position) {
        return word_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_word_list, null);

        TextView txtWordEn = (TextView)view.findViewById(R.id.txtWordEn);
        TextView txtWordKo = (TextView)view.findViewById(R.id.txtWordKo);
        Button btnWordSave = (Button) view.findViewById(R.id.btnWordSave);

        txtWordEn.setText(word_data.get(position).getWord_en());
        txtWordKo.setText(word_data.get(position).getWord_ko());

        btnWordSave.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Drawable drawable;
                int color;
                boolean isCheckSave = word_data.get(position).getIs_save();

                if(isCheckSave){
                    drawable = mContext.getResources().getDrawable(R.drawable.layout_background_base_10);
                    color = mContext.getResources().getColor(R.color.base_60);
                }else{
                    drawable = mContext.getResources().getDrawable(R.drawable.layout_background_primary_100_4);
                    color = mContext.getResources().getColor(R.color.base_0);
                }
                word_data.get(position).setIs_save(!isCheckSave);
                btnWordSave.setBackground(drawable);
                btnWordSave.setTextColor(color);
            }
        });
        return view;
    }
}
