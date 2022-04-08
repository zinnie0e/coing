package com.zinnie0e.coing;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.zinnie0e.coing.Data.WordData;

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
                Log.i("TAg", txtWordEn.getText().toString() + "    save");
            }
        });
        return view;
    }
}
