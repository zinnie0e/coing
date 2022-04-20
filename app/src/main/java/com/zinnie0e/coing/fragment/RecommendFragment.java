package com.zinnie0e.coing.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.MediaUtil;
import com.zinnie0e.coing.R;
import com.zinnie0e.coing.adapter.AdapterWord;
import com.zinnie0e.coing.adapter.AdapterWordBasic;
import com.zinnie0e.coing.data.WordBasicData;
import com.zinnie0e.coing.data.WordData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class RecommendFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = RecommendFragment.class.getSimpleName();
    private String mParam1;
    private String mParam2;

    /** VIEW */
    View view;
    static TextView txt_convEn;
    TextView txt_convKo;
    static TextView txt_sttResult;
    TextView txt_stt;
    LinearLayout btn_tts, btn_stt;
    ListView lst_words;
    ImageButton btn_prev, btn_close;

    static Bundle bundle;
    boolean isChkStt = false;
    ArrayList<WordBasicData> wordDataList;
    AdapterWordBasic adapterWord;

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

    public static void setSttResult(String resultText) {
        String resultStr = resultText.toLowerCase();
        String oriStr = txt_convEn.getText().toString().toLowerCase();

        SpannableString spannableString = new SpannableString(resultStr);
        String[] resultStr_arr = resultStr.split(" ");
        String[] oriStr_arr = oriStr.split(" ");
        for(int i = 0; i < resultStr_arr.length; i++){
            int start;
            if(i != resultStr_arr.length-1) start = resultStr.indexOf(resultStr_arr[i] + " ");
            else start = resultStr.indexOf(resultStr_arr[i]);
            int end = start + resultStr_arr[i].length();

            if(Arrays.asList(oriStr_arr).contains(resultStr_arr[i])){
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#202020")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }else{
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F06D6D")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        txt_sttResult.setText(spannableString);
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

        String getConvEn = null, getConvKo = null;
        bundle = getArguments();
        if(bundle != null){
            getConvEn = bundle.getString("conv_en");
            getConvKo = bundle.getString("conv_ko");
        }

        txt_sttResult.setVisibility(View.GONE);
        txt_convEn.setText(getConvEn);
        txt_convKo.setText(getConvKo);

        initWord(getConvEn.split(" "));

        return view;
    }



    private void findId() {
        txt_convEn = (TextView) view.findViewById(R.id.txt_convEn);
        txt_convKo = (TextView) view.findViewById(R.id.txt_convKo);
        txt_sttResult = (TextView) view.findViewById(R.id.txt_sttResult);
        txt_stt = (TextView) view.findViewById(R.id.txt_stt);

        btn_tts = (LinearLayout) view.findViewById(R.id.btn_tts);
        btn_stt = (LinearLayout) view.findViewById(R.id.btn_stt);

        lst_words = (ListView) view.findViewById(R.id.lst_words);

        btn_prev = (ImageButton) view.findViewById(R.id.btn_prev);
        btn_close = (ImageButton) view.findViewById(R.id.btn_close);

        btn_tts.setOnClickListener(this);
        btn_stt.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_tts){
            MediaUtil.speakOut(txt_convEn.getText().toString());
        }else if(v == btn_stt){
            Drawable drawable;
            int string;
            if(isChkStt){
                txt_sttResult.setVisibility(View.GONE);
                drawable = getActivity().getResources().getDrawable(R.drawable.layout_background_base_0_border);
                string = R.string.status_stt_off;
            }else{

                txt_sttResult.setVisibility(View.VISIBLE);
                txt_sttResult.setText(R.string.txt_stt_result);
                drawable = getActivity().getResources().getDrawable(R.drawable.layout_background_positive_30_border);
                string = R.string.status_stt_on;

                MediaUtil.speechOut(); //듣기
            }
            txt_stt.setText(string);
            btn_stt.setBackground(drawable);
            isChkStt = !isChkStt;
        }else if(v == btn_prev || v == btn_close){
            findNavController(this).navigate(R.id.action_recommendFragment_to_homeFragment);
        }
    }

    private void initWord(String[] words) {
        wordDataList = new ArrayList<WordBasicData>();
        for(int i = 0; i < words.length; i++){
            wordDataList.add(new WordBasicData(words[i], "ko//"+words[i]));
        }
        adapterWord = new AdapterWordBasic(getContext(), wordDataList);

        // 리스트뷰의 높이를 계산에서 layout 크기를 설정
        int totalHeight = 0;
        for (int i = 0; i < adapterWord.getCount(); i++){
            View listItem = adapterWord.getView(i, null, lst_words);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lst_words.getLayoutParams();
        params.height = totalHeight + (lst_words.getDividerHeight() * (adapterWord.getCount() - 1));
        lst_words.setLayoutParams(params);

        lst_words.setAdapter(adapterWord);
    }
}