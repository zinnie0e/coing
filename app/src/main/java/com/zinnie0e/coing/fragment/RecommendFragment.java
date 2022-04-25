package com.zinnie0e.coing.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import static com.zinnie0e.coing.fragment.HomeFragment.myAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.zinnie0e.coing.adapter.MyAdapter;
import com.zinnie0e.coing.data.ConvData;
import com.zinnie0e.coing.data.WordBasicData;
import com.zinnie0e.coing.data.WordData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class RecommendFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = RecommendFragment.class.getSimpleName();
    static Context mContext;
    Resources res;

    /** VIEW */
    View view;
    static TextView txt_convEn;
    TextView txt_convKo;
    static TextView txt_sttResult;
    TextView txt_stt;
    LinearLayout btn_tts, btn_stt, ly_confirm;
    ListView lst_words;
    ImageButton btn_prev, btn_close, btn_bookmark;

    ConvData MyData;

    static Bundle bundle;
    boolean isChkStt = false;
    boolean isChkBookmark = false;
    ArrayList<WordBasicData> wordDataList;
    AdapterWordBasic adapterWord;

    public RecommendFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend, container, false);
        mContext = getActivity();
        res = mContext.getResources();

        findId();

        String getConvEn = null, getConvKo = null;
        bundle = getArguments();
        if(bundle != null){
            getConvEn = bundle.getString("conv_en");
            getConvKo = bundle.getString("conv_ko");
            MyData = myAdapter.getItem(bundle.getInt("position"));
        }
        txt_convEn.setText(getConvEn);
        txt_convKo.setText(getConvKo);

        if(MyData.getIs_bookmark()){
            btn_bookmark.setImageResource(R.drawable.ic_bookmark_on);
            isChkBookmark = true;
        }

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
        ly_confirm = (LinearLayout) view.findViewById(R.id.ly_confirm);

        lst_words = (ListView) view.findViewById(R.id.lst_words);

        btn_prev = (ImageButton) view.findViewById(R.id.btn_prev);
        btn_close = (ImageButton) view.findViewById(R.id.btn_close);
        btn_bookmark = (ImageButton) view.findViewById(R.id.btn_bookmark);

        btn_tts.setOnClickListener(this);
        btn_stt.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_bookmark.setOnClickListener(this);
        ly_confirm.setOnClickListener(this);

        txt_sttResult.setVisibility(View.GONE);
        ly_confirm.setVisibility(View.GONE);
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
        }else if(v == btn_bookmark){
            if(isChkBookmark){
                //데이터 삭제
                btn_bookmark.setImageResource(R.drawable.ic_bookmark);

                MyData.setIs_bookmark(false);
            }else{
                //데이터 저장

                //ui
                btn_bookmark.setImageResource(R.drawable.ic_bookmark_on);
                ly_confirm.setVisibility(View.VISIBLE);

                ly_confirm.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.top));
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        ly_confirm.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplication(), R.anim.up));
                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                ly_confirm.clearAnimation();
                                ly_confirm.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                }, 1000);

                //adapter data
                MyData.setIs_bookmark(true);
            }
            isChkBookmark = !isChkBookmark;
        }else if(v == ly_confirm){
            Log.i(TAG, "Click ly_confirm");
        }else if(v == btn_prev || v == btn_close){
            findNavController(this).navigate(R.id.action_recommendFragment_to_homeFragment);
            //((MainActivity) mContext).bottom_menu.findViewById(R.id.tab_home).performClick();
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
}