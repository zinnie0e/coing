package com.zinnie0e.coing.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zinnie0e.coing.R;
import com.zinnie0e.coing.data.ConvData;

import java.util.ArrayList;

public class AdapterEdit extends BaseAdapter {
    View view;

    Context mContext = null;
    Resources res;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ConvData> conv_data;

    boolean isChkEdit = false;

    public AdapterEdit(Context context, ArrayList<ConvData> data) {
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
        view = mLayoutInflater.inflate(R.layout.view_contents_edit_list, null);
        res = mContext.getResources();
        //findId();

        String myConvEn = conv_data.get(position).getConv_en();

        TextView txt_sentence = (TextView)view.findViewById(R.id.txt_sentence);
        ImageView btn_itemSel = (ImageView) view.findViewById(R.id.btn_itemSel);
        LinearLayout ly_item = (LinearLayout) view.findViewById(R.id.ly_item);

        txt_sentence.setText(myConvEn);

        ly_item.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (isChkEdit){
                    btn_itemSel.setImageResource(R.drawable.ic_radio_off);
                }else {
                    btn_itemSel.setImageResource(R.drawable.ic_radio_on);
                }
                isChkEdit = !isChkEdit;
            }
        });
        return view;
    }

    private void findId() {
        //이거하면 해당 아이템을 가져올 수 없음.. 이유는 모르겠음
    }
}
