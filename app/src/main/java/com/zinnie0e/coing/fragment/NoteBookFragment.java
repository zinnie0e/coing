package com.zinnie0e.coing.fragment;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.R;

import java.sql.Array;
import java.util.Objects;

public class NoteBookFragment extends Fragment implements View.OnClickListener {
    View view;

    LinearLayout ly_status, ly_status_list, ly_status_new, ly_status_learning, ly_status_done, ly_status_nothing;
    ImageView img_status;
    TextView txt_status;
    boolean isCheckStatus = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public NoteBookFragment() {
    }

    public static NoteBookFragment newInstance(String param1, String param2) {
        NoteBookFragment fragment = new NoteBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note_book, container, false);

        findId();

        ly_status_list.setVisibility(View.GONE);
        return view;
    }

    private void findId() {
        ly_status = (LinearLayout) view.findViewById(R.id.ly_status);
        ly_status_list = (LinearLayout) view.findViewById(R.id.ly_status_list);
        ly_status_new = (LinearLayout) view.findViewById(R.id.ly_status_new);
        ly_status_learning = (LinearLayout) view.findViewById(R.id.ly_status_learning);
        ly_status_done = (LinearLayout) view.findViewById(R.id.ly_status_done);
        ly_status_nothing = (LinearLayout) view.findViewById(R.id.ly_status_nothing);
        img_status = (ImageView) view.findViewById(R.id.img_status);
        txt_status = (TextView) view.findViewById(R.id.txt_status);

        ly_status.setOnClickListener(this);
        ly_status_new.setOnClickListener(this);
        ly_status_learning.setOnClickListener(this);
        ly_status_done.setOnClickListener(this);
        ly_status_nothing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == ly_status){
            if(!isCheckStatus) ly_status_list.setVisibility(View.VISIBLE);
            else ly_status_list.setVisibility(View.GONE);
            isCheckStatus = !isCheckStatus;
        }else if(v == ly_status_new){
            initStatusList(0);
        }else if(v == ly_status_learning){
            initStatusList(1);
        }else if(v == ly_status_done){
            initStatusList(2);
        }else if(v == ly_status_nothing){
            initStatusList(3);
        }
    }

    public void initStatusList(int val){
        /** 0: new / 1: learning / 2: done / 3: nothing*/
        int[] img_arr = {R.drawable.ic_new, R.drawable.ic_learning, R.drawable.ic_done, R.color.claen};
        int[] string_arr = {R.string.status_new, R.string.status_learning, R.string.status_done, R.string.status_nothing};
        img_status.setImageResource(img_arr[val]);
        txt_status.setText(string_arr[val]);
        ly_status_list.setVisibility(View.GONE);
        isCheckStatus = !isCheckStatus;
    }
}