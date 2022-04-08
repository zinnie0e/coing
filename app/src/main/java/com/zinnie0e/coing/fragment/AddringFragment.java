package com.zinnie0e.coing.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.AdapterWord;
import com.zinnie0e.coing.Data.WordData;
import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.Papago;
import com.zinnie0e.coing.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddringFragment extends Fragment implements View.OnClickListener{

    LinearLayout btnUpImg, layWord;
    EditText edtEnglish, edtKorea;
    ImageButton btnClose;
    ListView listWord;

    Context mContext;
    AdapterWord adapterWord;
    ArrayList<WordData> wordDataList;

    String language = "English";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String present_result_ko;
    private ArrayList words_ko = new ArrayList();

    public AddringFragment(Context context) {
        mContext = context;
    }

    public AddringFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddringFragment newInstance(String param1, String param2) {
        AddringFragment fragment = new AddringFragment();
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
        View view = inflater.inflate(R.layout.fragment_addring, container, false);

        edtEnglish = (EditText) view.findViewById(R.id.edtEnglish);
        edtKorea = (EditText) view.findViewById(R.id.edtKorea);
        btnUpImg = (LinearLayout) view.findViewById(R.id.btnUpImg);
        layWord = (LinearLayout) view.findViewById(R.id.layWord);
        btnClose = (ImageButton) view.findViewById(R.id.btnClose);
        listWord = (ListView) view.findViewById(R.id.listWord);


        btnUpImg.setOnClickListener(this::onClick);
        btnClose.setOnClickListener(this);
        layWord.setVisibility(view.GONE);


        edtEnglish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = edtEnglish.getText().toString();
                if(edtEnglish.length() == 0){
                    layWord.setVisibility(view.GONE);
                    edtKorea.setText("");
                }
                else {
                    layWord.setVisibility(view.VISIBLE);

                    String input_text = edtEnglish.getText().toString();

                    if(input_text.indexOf(" ") != -1) {
                        String[] words = input_text.split(" ");
                        wordThread(words, words_ko);
                    }

                    words_ko.clear();


                    /*for (int i = 0; i < words.length; i++){
                        int finalI = i;
                        new Thread(){
                            @Override
                            public void run() {
                                Translation(words[finalI], 2);
                                words_ko.add(present_result_ko) ;
                                wordThread(words, words_ko);
                            }
                        }.start();
                    }*/



                    /*if(input_text.indexOf(" ")!=-1) {
                        Log.i("tag", "띄어쓰기 있습니다.");

                        if((input.charAt(input.length() - 1)) == ' '){
                            Log.i("^^^^^^","T");

                            words = input_text.split(" ");
                            for(int i =0 ; i < words.length; i++){

                                int finalI = i;
                                new Thread(){
                                    @Override
                                    public void run() {
                                        Translation(words[finalI], 2);
                                        words_ko.add(present_result_ko) ;
                                        wordThread(words, words_ko);
                                    }
                                }.start();

                                Log.i("tag11", words[i]);
                            }
                        }


                    }else {
                        System.out.println("문자가 포함되어 있지 않습니다.");
                    }*/



                    new Thread(){
                        @Override
                        public void run() {
                            Translation(input_text, 1);
                        }
                    }.start();
                }
            }
        });

        return view;
    }

    private void Translation(String input_text, int status) {
        String word = input_text;
        Papago papago = new Papago();
        String resultWord;
                        /*if(language == "ko"){
                            resultWord= papago.getTranslation(word,"ko","en");
                        }else{
                            resultWord= papago.getTranslation(word,"en","ko");
                        }*/

        resultWord= papago.getTranslation(word,"en","ko");

        Bundle papagoBundle = new Bundle();
        papagoBundle.putString("resultWord",resultWord);
        Message msg;

        switch (status){
            case 1:
                msg = papago_handler.obtainMessage();
                msg.setData(papagoBundle);
                papago_handler.sendMessage(msg);
                break;
            case 2:
                msg = papago_handler2.obtainMessage();
                msg.setData(papagoBundle);
                papago_handler2.sendMessage(msg);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnUpImg){

        }else if(v == btnClose){
            ((MainActivity) getActivity()).setFragment(0);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            edtKorea.setText(resultWord);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler papago_handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            Log.i("hand2  ", resultWord);
            present_result_ko = resultWord;
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기

            wordDataList = new ArrayList<WordData>();
            for(int i = 0; i < bundle.getStringArray("word").length; i++){
                /*Log.i("****", "----------------");
                Log.i("****", bundle.getStringArray("word")[i]);
                Log.i("****", bundle.getStringArrayList("word_ko") + "");
                Log.i("****", "----------------");*/

               // wordDataList.add(new WordData(bundle.getStringArray("word")[i], bundle.getStringArrayList("word_ko").get(i)));
                wordDataList.add(new WordData(bundle.getStringArray("word")[i], "ko//"+bundle.getStringArray("word")[i]));
            }
            adapterWord = new AdapterWord(getContext(), wordDataList);
            listWord.setAdapter(adapterWord);
        }
    };

    private void wordThread(String[] words, ArrayList words_ko) {
        final Bundle bundle = new Bundle();
        bundle.putStringArray("word", words);
        bundle.putStringArrayList("word_ko", words_ko);
        Message msg = handler.obtainMessage();
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}