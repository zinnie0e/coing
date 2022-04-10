package com.zinnie0e.coing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zinnie0e.coing.fragment.AddringFragment;
import com.zinnie0e.coing.fragment.HomeFragment;
import com.zinnie0e.coing.fragment.NoteBookFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    static String SERVER_URL = "http://14.37.4.189:1234/";
    //private String SERVER_URL = "http://10.0.2.2:80/";
    TextView txtVoice;
    Button btnVoice;
    ListView listView;

    private BottomNavigationView bottom_menu;
    FragmentManager fm;
    FragmentTransaction ft;
    private HomeFragment home_frag;
    private NoteBookFragment notebook_frag;
    private AddringFragment addring_frag;

    public static Intent intent;
    public static TextToSpeech tts;
    final int PERMISSION = 1;

    static RequestQueue requestQueue;
    JSONArray json_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findId();
        initBottom();
        initRecommend();

        MediaUtil mediaUtil = new MediaUtil(this);
        //btnVoice.setOnClickListener(mediaUtil);

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        // RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        tts = new TextToSpeech(this, this);

        //db통신
        //요청 큐가 없으면 요청 큐 생성하기
        //나중에 여기다가 데이터 담으면 알아서!!!!!!! 통신함 ㅋ
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        selData("dbtest");
    }

    private void findId() {
        /*txtVoice = (TextView)findViewById(R.id.txtVoice);
        btnVoice = (Button) findViewById(R.id.btnVoice);*/
        listView = (ListView)findViewById(R.id.listConv);
        bottom_menu = findViewById(R.id.bottom_menu);
    }

    @Override
    public void onClick(View v) {
    }

    private void initBottom() {
        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tab_home:
                        setFragment(0);
                        break;
                    case R.id.tab_notebook:
                        setFragment(1);
                        break;
                    case R.id.tab_addring:
                        setFragment(2);
                        break;
                }
                return true;
            }
        });
        home_frag = new HomeFragment(this);
        notebook_frag = new NoteBookFragment();
        addring_frag = new AddringFragment();
        setFragment(0); // 첫 프래그먼트 화면 지정
    }

    public void setFragment(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, home_frag);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, notebook_frag);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, addring_frag);
                ft.commit();
                break;
        }
    }

    private void initRecommend() {
        long miliseconds = System.currentTimeMillis();
        Date date = new Date(miliseconds);
        Log.d("!---", miliseconds + "");

        get_number("selMaxDate");

    }

    @Override public void onDestroy() {
        MediaUtil.stopSpeak();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                btn_Speak.setEnabled(true);
//                speakOut();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void get_number(String val) {
        String URL = SERVER_URL + val + ".php";
        //String URL = "http://10.0.2.2:80/" + val + ".php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                long max_date_n = jsonArray.getJSONObject(0).getInt("max_date");
                max_date_n *= 1000;
                Log.i("!***//", max_date_n +"");


                //long now_date = System.currentTimeMillis();
                Date now_date = new Date(System.currentTimeMillis());
                Date max_date = new Date(max_date_n);

                Log.d("!---///", now_date.compareTo(max_date) + "");
                Log.d("!---now_date/", now_date + "");
                Log.d("!---max_date/", max_date + "");

                long diffSec = (now_date.getTime() - max_date.getTime()) / 1000; //초 차이
                long diffDays = diffSec / (24*60*60); //일자수 차이

                Log.d("!---차이/", diffDays + "일 차이");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "실패함. 인터넷 연결 확인", Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    public void selData(String val) {
        //php url 입력
        String URL = SERVER_URL + val + ".php";
        //String URL = "http://127.0.0.1:80/" + val + ".php";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //응답이 되었을때 response로 값이 들어옴
                Log.i("!***", response);
                //Toast.makeText(getApplicationContext(), "응답:" + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //에러나면 error로 나옴
                Log.e("!***", error.getMessage());
                //Toast.makeText(getApplicationContext(), "에러:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                //php로 설정값을 보낼 수 있음
                return param;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void selDatabase(String val) {
        String URL = SERVER_URL + val + ".php";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "!!!!!!!!!" + response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //에러나면 error로 나옴
                Log.e(TAG, error.getMessage());
                //Toast.makeText(getApplicationContext(), "에러:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();
                //php로 설정값을 보낼 수 있음
                return param;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);

    }
}