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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zinnie0e.coing.database.InsertDatabase;
import com.zinnie0e.coing.database.SelectDatabase;
import com.zinnie0e.coing.fragment.HomeFragment;
import com.zinnie0e.coing.fragment.NoteBookFragment;
import com.zinnie0e.coing.fragment.SaveFragment;
import com.zinnie0e.coing.util.ActivityResultEvent;
import com.zinnie0e.coing.util.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    TextView txtVoice;
    Button btnVoice;
    ListView listView;

    private BottomNavigationView bottom_menu;
    FragmentManager fm;
    FragmentTransaction ft;
    private HomeFragment home_frag;
    private NoteBookFragment notebook_frag;
    private SaveFragment save_frag;

    public static Intent intent;
    public static TextToSpeech tts;
    final int PERMISSION = 1;

    public static JSONArray json_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findId();
        initBottom();

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
                    case R.id.tab_save:
                        setFragment(2);
                        break;
                }
                return true;
            }
        });
        home_frag = new HomeFragment(this);
        notebook_frag = new NoteBookFragment();
        save_frag = new SaveFragment();
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
                ft.replace(R.id.main_frame, save_frag);
                ft.commit();
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EventBus.getInstance().post(ActivityResultEvent.create(requestCode, resultCode, data));
    }

    /*private void initRecommend() {
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
    }*/

    public void insertReco(){
        String sentence_en = "test";
        String sentence_ko = "테스트";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());

        HashMap<String, String> requestedParams = new HashMap<>();
        requestedParams.put("sentence_en", sentence_en);
        requestedParams.put("sentence_ko", sentence_ko);
        requestedParams.put("date", date);
        Log.d("HashMap", requestedParams.get("sentence_en"));
        Toast.makeText(getApplicationContext(), "Success!!! Added sentence_en: " + requestedParams.get("sentence_en"), Toast.LENGTH_LONG).show();

        InsertDatabase task = new InsertDatabase(Define.DB_IN_Recommend, requestedParams);
        task.execute();
    }
}