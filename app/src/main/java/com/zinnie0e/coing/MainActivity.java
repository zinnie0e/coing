package com.zinnie0e.coing;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener{
    private String URL = "http://www.englishspeak.com/ko/english-phrases";
    TextView txtVoice;
    Button btnVoice;
    ListView listView;

    Intent intent;
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;
    final int PERMISSION = 1;

    ArrayList<ConvData> convDataList;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtVoice = (TextView)findViewById(R.id.txtVoice);
        btnVoice = (Button) findViewById(R.id.btnVoice);

        final Bundle bundle = new Bundle();

        new Thread(() -> {
            Document doc = null;
            try{
                doc = Jsoup.connect(URL).get();	//URL 웹사이트에 있는 html 코드를 다 끌어오기
                Elements elements = doc.select(".test"); //원하는 태그만 찾아서 가져오기

                Random rand = new Random();
                String[] conv = new String[5];
                for(int i = 0 ; i < 5 ; i++) {
                    if(!elements.get(rand.nextInt(elements.size())).text().isEmpty()){
                        conv[i] = elements.get(rand.nextInt(elements.size())).text();
                        Log.i("!---", conv[i]);
                    }

                }
                Elements elements2 = doc.select(".table tbody");
                for (Element tr : elements2.select("tr")) {
                    if(!tr.text().isEmpty()){
                        Log.i("!----", tr.text());
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

        listView = (ListView)findViewById(R.id.listConv);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                //Toast.makeText(getApplicationContext(), myAdapter.getItem(position).getConv_en(), Toast.LENGTH_LONG).show();
                speakOut(myAdapter.getItem(position).getConv_en());
            }
        });


        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        // RecognizerIntent 객체 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        // 버튼을 클릭 이벤트 - 객체에 Context와 listener를 할당한 후 실행
        /*btnVoice.setOnClickListener(v -> {
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });*/

        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnVoice){
            //객체에 Context와 listener를 할당한 후 실행
            mRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        }
        /*switch (v.getId()){
            case R.id.txtConv1: case R.id.txtConv2: case R.id.txtConv3: case R.id.txtConv4: case R.id.txtConv5:
                speakOut(((TextView)findViewById(v.getId())));
                break;
        }*/
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기

            convDataList = new ArrayList<ConvData>();
            for(int i = 0; i < 5; i++){
                convDataList.add(new ConvData(bundle.getStringArray("conv")[i], "(ko)" + bundle.getStringArray("conv")[i]));
            }
            myAdapter = new MyAdapter(getApplicationContext(), convDataList);
            listView.setAdapter(myAdapter);
        }
    };

    // RecognizerIntent 객체에 할당할 listener 생성
    private RecognitionListener listener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
        }
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }
        @Override public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                txtVoice.setText(matches.get(i));
            }
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(String conv) {
        Log.i("!---", conv);
        CharSequence text = conv;
        tts.setPitch((float) 0.6);
        tts.setSpeechRate((float) 0.1);
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override public void onDestroy() {
        if (tts != null) {
            tts.stop(); tts.shutdown();
        }
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
}