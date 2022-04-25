package com.zinnie0e.coing.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
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

public class MyAdapter extends BaseAdapter implements View.OnClickListener {
    View view;

    Context mContext = null;
    Resources res;
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
        view = mLayoutInflater.inflate(R.layout.view_contents_list, null);
        res = mContext.getResources();
        //findId();

        String myStatus = conv_data.get(position).getStatus();
        String myConvEn = conv_data.get(position).getConv_en();
        String myConvKo = conv_data.get(position).getConv_ko();

        TextView txtSentence = (TextView)view.findViewById(R.id.txtSentence);
        TextView txtStatus = (TextView)view.findViewById(R.id.txtStatus);
        TextView txtIsLook = (TextView)view.findViewById(R.id.txtIsLook);
        TextView txt_icon = (TextView)view.findViewById(R.id.txt_icon);
        ImageView img_icon = (ImageView) view.findViewById(R.id.img_icon);
        LinearLayout ly_status = (LinearLayout) view.findViewById(R.id.ly_status);

        txtSentence.setText(myConvEn);
        txtStatus.setText(myStatus);

        if(myStatus == res.getString(R.string.status_random)){
            img_icon.setVisibility(View.GONE);
        }else if(myStatus == res.getString(R.string.status_nothing)){
            ly_status.setVisibility(View.GONE);
        }else{
            txt_icon.setVisibility(View.GONE);
            if(myStatus == res.getString(R.string.status_new)){
                img_icon.setImageResource(R.drawable.ic_new);
            }else if(myStatus == res.getString(R.string.status_learning)){
                img_icon.setImageResource(R.drawable.ic_learning);
            }else if(myStatus == res.getString(R.string.status_done)){
                img_icon.setImageResource(R.drawable.ic_done);
            }
        }

        //btnLook 클릭했을때 기능 추가
        txtIsLook.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (isEnglish){
                    txtIsLook.setText(R.string.is_look_ko); //선글라스
                    txtSentence.setText(myConvKo);
                }else {
                    txtIsLook.setText(R.string.is_look_en);
                    txtSentence.setText(myConvEn);
                }
                isEnglish = !isEnglish;
            }
        });
        return view;
    }

    private void findId() {
        //이거하면 해당 아이템을 가져올 수 없음.. 이유는 모르겠음
    }

    @Override
    public void onClick(View v) {
    }
}
