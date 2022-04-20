package com.zinnie0e.coing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zinnie0e.coing.R;
import com.zinnie0e.coing.data.WordBasicData;

import java.util.ArrayList;

public class AdapterWordBasic extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WordBasicData> word_data;


    public AdapterWordBasic(Context context, ArrayList<WordBasicData> data) {
        mContext = context;
        word_data = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return word_data.size();
    }

    @Override
    public WordBasicData getItem(int position) {
        return word_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.view_word_list_basic, null);

        TextView txtWordEn = (TextView)view.findViewById(R.id.txtWordEn);
        TextView txtWordKo = (TextView)view.findViewById(R.id.txtWordKo);

        txtWordEn.setText(word_data.get(position).getWord_en());
        txtWordKo.setText(word_data.get(position).getWord_ko());
        return view;
    }
}
