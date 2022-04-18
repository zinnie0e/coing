package com.zinnie0e.coing.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.R;

public class RecommendFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RecommendFragment.class.getSimpleName();
    private String mParam1;
    private String mParam2;

    /** VIEW */
    View view;
    TextView txten, txtko;

    public RecommendFragment() {
        // Required empty public constructor
    }

    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
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
        view = inflater.inflate(R.layout.fragment_recommend, container, false);

        findId();

        Bundle bundle = getArguments();
        if(bundle != null){
            Log.i("여긴 받는데", bundle.getString("conv_en"));
            Log.i("여긴 받는데", bundle.getString("conv_ko"));

            txten.setText(bundle.getString("conv_en"));
            txtko.setText(bundle.getString("conv_ko"));
        }

        return view;
    }

    private void findId() {
        txten = (TextView) view.findViewById(R.id.txten);
        txtko = (TextView) view.findViewById(R.id.txtko);
    }

    @Override
    public void onClick(View v) {
    }

}