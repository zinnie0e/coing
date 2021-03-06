package com.zinnie0e.coing.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.zinnie0e.coing.Define;
import com.zinnie0e.coing.MediaUtil;
import com.zinnie0e.coing.data.ConvData;
import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.adapter.MyAdapter;
import com.zinnie0e.coing.R;
import com.zinnie0e.coing.database.InsertDatabase;
import com.zinnie0e.coing.database.SelectDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static String CRAWLING_URL = "http://www.englishspeak.com/ko/english-phrases?category_key=";
    static Resources res;

    View view;
    static TextView txt_allCnt;
    EditText edt_search;
    static ListView lst_conv;
    ImageButton btn_searchClose;
    ImageView img_search;

    static Context mContext;
    static MyAdapter myAdapter;
    static ArrayList<ConvData> convDataList;
    static ArrayList<ConvData> arraylist;

    private RecommendFragment recommend_frag = new RecommendFragment();

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        res = mContext.getResources();

        findID();
        btn_searchClose.setVisibility(View.INVISIBLE);

        //initRecommend();
        crawlingThread();

        arraylist = new ArrayList<ConvData>();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_search.length() == 0){
                    img_search.setVisibility(View.VISIBLE);
                    btn_searchClose.setVisibility(View.INVISIBLE);
                }else{
                    img_search.setVisibility(View.GONE);
                    btn_searchClose.setVisibility(View.VISIBLE);

                    search(edt_search.getText().toString());
                }

            }
        });

        lst_conv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                RecommendFragment recommned_frag = new RecommendFragment();*/

                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("conv_en", myAdapter.getItem(position).getConv_en());
                bundle.putString("conv_ko", myAdapter.getItem(position).getConv_ko());

                /*recommned_frag.setArguments(bundle);
                ft.replace(MainActivity.main_frag, recommned_frag);
                ft.commit();*/

                findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_recommendFragment, bundle);
                //Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_recommendFragment, bundle);
            }
        });

        return view;
    }



    private void findID() {
        txt_allCnt = (TextView)view.findViewById(R.id.txt_allCnt);
        edt_search = (EditText) view.findViewById(R.id.edt_search);
        lst_conv = (ListView)view.findViewById(R.id.lst_conv);
        img_search = (ImageView)view.findViewById(R.id.img_search);
        btn_searchClose = (ImageButton) view.findViewById(R.id.btn_searchClose);

        btn_searchClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_searchClose){
            edt_search.setText(null);
            search(edt_search.getText().toString());
        }
    }

    private void search(String charText) {
        convDataList.clear();
        if (charText.length() == 0) {
            convDataList.addAll(arraylist);
        }else{
            for(int i = 0;i < arraylist.size(); i++) {
                if (arraylist.get(i).getConv_en().contains(charText)){
                    convDataList.add(arraylist.get(i));
                }
            }
        }
        myAdapter.notifyDataSetChanged();
    }

    private void initRecommend() {
        Log.i(TAG, "start initRecommend");

        SelectDatabase sel = new SelectDatabase(Define.DB_SEL_Maxdate);
        sel.execute();
    }

    public static void setRecommend(long diffDays) {
        if(diffDays >= 7){ //or ?????????
            Log.i(TAG, "7??? ?????? ???????????????, ?????? ???????????? ?????? ?????????");
            crawlingThread();
        }
        else{
            Log.i(TAG, "????????? ?????? ????????????");
            SelectDatabase sel = new SelectDatabase(Define.DB_SEL_Recommend);
            sel.execute();
        }
    }

    public static void insertRecommend(String nowdate, String conv_en, String conv_ko){
        String sentence_en = conv_en;
        String sentence_ko = conv_ko;
        String date = nowdate;

        HashMap<String, String> requestedParams = new HashMap<>();
        requestedParams.put("sentence_en", sentence_en);
        requestedParams.put("sentence_ko", sentence_ko);
        requestedParams.put("date", date);

        InsertDatabase task = new InsertDatabase(Define.DB_IN_Recommend, requestedParams);
        task.execute();
    }

    private static void crawlingThread() {
        int cntCrawling = 3;
        final Bundle bundle = new Bundle();
        Random rand = new Random();
        int categoryNum = rand.nextInt(10) + 1;
        new Thread(() -> {
            Document doc = null;
            try{
                doc = Jsoup.connect(CRAWLING_URL + categoryNum).get();	//URL ??????????????? ?????? html ????????? ??? ????????????
                Elements elements = doc.select(".test"); //????????? ????????? ????????? ????????????
                Elements contents = doc.select(".table tbody tr td:eq(0)");
                List<String> arr_en = elements.eachText();

                Elements aTag = doc.select("a"); for(Element a : aTag) { a.remove(); }
                List<String> arr_ko = contents.eachText();

                /*Log.i("***", arr_en.toString());
                Log.i("***", arr_ko.toString());*/

                String[] conv_en = new String[cntCrawling];
                String[] conv_ko = new String[cntCrawling];
                for(int i = 0 ; i < cntCrawling ; i++) {
                    int ran = rand.nextInt(arr_en.size());
                    conv_en[i] =  arr_en.get(ran);
                    conv_ko[i] =  arr_ko.get(ran);
                    /*Log.i("!---", arr_en.get(ran));
                    Log.i("!---", arr_ko.get(ran));*/
                }

                bundle.putStringArray("conv_en", conv_en);
                bundle.putStringArray("conv_ko", conv_ko);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);

                //insert DB
                String strNowDate = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
                for(int i = 0; i < conv_en.length; i++){
                    //insertRecommend(strNowDate, conv_en[i], conv_ko[i]);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();    //new Thread?????? ????????? ????????? ??????
            convDataList = new ArrayList<ConvData>();
            for(int i = 0; i < bundle.getStringArray("conv_en").length; i++){
                convDataList.add(new ConvData(bundle.getStringArray("conv_en")[i], bundle.getStringArray("conv_ko")[i], res.getString(R.string.status_random)));
            }
            convDataList.add(new ConvData("Executing tasks app assembleDebug in project CUsersGURUDEV PJE tudio Projectscoing", "??????????????? html  tag ??? ?????? ?????? ?????? ?????? ???????????? ????????? ??? ?????? String text = doc.text(; ??? ???????????????", mContext.getResources().getString(R.string.status_new)));
            convDataList.add(new ConvData("test1", "test", res.getString(R.string.status_done)));
            convDataList.add(new ConvData("test2", "test", res.getString(R.string.status_learning)));
            convDataList.add(new ConvData("test3", "test", res.getString(R.string.status_nothing)));
            convDataList.add(new ConvData("test44444444444444444444444444444444444444444", "test", res.getString(R.string.status_nothing)));

            txt_allCnt.setText(convDataList.size()+"???");
            myAdapter = new MyAdapter(mContext, convDataList);
            lst_conv.setAdapter(myAdapter);
            arraylist.addAll(convDataList);
        }
    };
}