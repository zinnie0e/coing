package com.zinnie0e.coing.fragment;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zinnie0e.coing.Define;
import com.zinnie0e.coing.MediaUtil;
import com.zinnie0e.coing.data.ConvData;
import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.adapter.MyAdapter;
import com.zinnie0e.coing.R;
import com.zinnie0e.coing.database.SelectDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = HomeFragment.class.getSimpleName();
    private String CRAWLING_URL = "http://www.englishspeak.com/ko/english-phrases";

    TextView txtAllCount;
    ListView listView;

    Context mContext;
    MyAdapter myAdapter;
    ArrayList<ConvData> convDataList;

    private String mParam1;
    private String mParam2;

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

        initRecommend();
        crawlingThread();

        listView = (ListView)view.findViewById(R.id.listConv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                MediaUtil.speakOut(myAdapter.getItem(position).getConv_en());
            }
        });

        txtAllCount = (TextView)view.findViewById(R.id.txtAllCount);

        return view;
    }

    private void initRecommend() {
        Log.i(TAG, "start initRecommend");

        SelectDatabase s = new SelectDatabase(Define.DB_SEL_Maxdate);
        s.execute();
    }

    public static void setRecommend(long diffDays) {
        if(diffDays >= 7){ //or 없거나
            Log.i(TAG, "7일 이상 차이나니까, 또는 없으니까 새로 만들자");
        }
        else{
            Log.i(TAG, "원래꺼 세개 불러오자");
        }
    }

    private void crawlingThread() {
        int cntCrawling = 3;
        final Bundle bundle = new Bundle();
        new Thread(() -> {
            Document doc = null;
            try{
                doc = Jsoup.connect(CRAWLING_URL).get();	//URL 웹사이트에 있는 html 코드를 다 끌어오기
                Elements elements = doc.select(".test"); //원하는 태그만 찾아서 가져오기
                Elements contents = doc.select("table tbody tr td:eq(0)");
                Log.i("***", contents.outerHtml());


                Random rand = new Random();
                String[] conv = new String[cntCrawling];
                for(int i = 0 ; i < cntCrawling ; i++) {
                    int ran = rand.nextInt(elements.size());
                    if(!elements.get(ran).text().isEmpty()){
                        conv[i] = elements.get(ran).text();
                        Log.i("!---", conv[i]);
                        Log.i("!---!", contents.get(ran).attr(""));
                        //Log.i("!---", contents.get(ran).text());
                    }
                }
                //한글 가져오기 진행중

                bundle.putStringArray("conv", conv);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기

            convDataList = new ArrayList<ConvData>();
            for(int i = 0; i < bundle.getStringArray("conv").length; i++){
                convDataList.add(new ConvData(bundle.getStringArray("conv")[i], "(ko)" + bundle.getStringArray("conv")[i]));
            }

            Log.i("@@@@@", convDataList.size() + "");

            txtAllCount.setText(convDataList.size()+"개");
            myAdapter = new MyAdapter((MainActivity) mContext, convDataList);
            listView.setAdapter(myAdapter);
        }
    };
}