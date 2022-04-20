package com.zinnie0e.coing.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private static String CRAWLING_URL = "http://www.englishspeak.com/ko/english-phrases?category_key=";


    static TextView txtAllCount;
    static ListView listView;

    static Context mContext;
    static MyAdapter myAdapter;
    static ArrayList<ConvData> convDataList;

    private String mParam1;
    private String mParam2;

    private RecommendFragment recommend_frag = new RecommendFragment();

    public HomeFragment(Context context) {
        mContext = context;
    }
    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initRecommend();
        crawlingThread();

        listView = (ListView)view.findViewById(R.id.listConv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                //MediaUtil.speakOut(myAdapter.getItem(position).getConv_en());

                /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                RecommendFragment recommned_frag = new RecommendFragment();*/

                Bundle bundle = new Bundle();
                bundle.putString("conv_en", myAdapter.getItem(position).getConv_en());
                bundle.putString("conv_ko", myAdapter.getItem(position).getConv_ko());

                /*recommned_frag.setArguments(bundle);
                ft.replace(MainActivity.main_frag, recommned_frag);
                ft.commit();*/

                findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_recommendFragment, bundle);
            }
        });

        txtAllCount = (TextView)view.findViewById(R.id.txtAllCount);

        return view;
    }

    private void initRecommend() {
        Log.i(TAG, "start initRecommend");

        SelectDatabase sel = new SelectDatabase(Define.DB_SEL_Maxdate);
        sel.execute();
    }

    public static void setRecommend(long diffDays) {
        if(diffDays >= 7){ //or 없거나
            Log.i(TAG, "7일 이상 차이나니까, 또는 없으니까 새로 만들자");
            crawlingThread();
        }
        else{
            Log.i(TAG, "원래꺼 세개 불러오자");
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
                doc = Jsoup.connect(CRAWLING_URL + categoryNum).get();	//URL 웹사이트에 있는 html 코드를 다 끌어오기
                Elements elements = doc.select(".test"); //원하는 태그만 찾아서 가져오기
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
            Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기

            convDataList = new ArrayList<ConvData>();
            for(int i = 0; i < bundle.getStringArray("conv_en").length; i++){
                convDataList.add(new ConvData(bundle.getStringArray("conv_en")[i], bundle.getStringArray("conv_ko")[i]));
            }
            convDataList.add(new ConvData("Executing tasks app assembleDebug in project CUsersGURUDEV PJE tudio Projectscoing", "문서로부터 html  tag 를 모두 제거 하고 순수 문자열만 얻고자 할 때는 String text = doc.text(; 를 사용합니다"));
            txtAllCount.setText(convDataList.size()+"개");
            myAdapter = new MyAdapter((MainActivity) mContext, convDataList);
            listView.setAdapter(myAdapter);
        }
    };
}