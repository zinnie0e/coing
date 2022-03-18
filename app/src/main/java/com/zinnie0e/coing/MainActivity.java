package com.zinnie0e.coing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String URL = "http://www.englishspeak.com/ko/english-phrases";
    TextView txtConv1, txtConv2, txtConv3, txtConv4, txtConv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtConv1 = (TextView) findViewById(R.id.txtConv1);
        txtConv2 = (TextView) findViewById(R.id.txtConv2);
        txtConv3 = (TextView) findViewById(R.id.txtConv3);
        txtConv4 = (TextView) findViewById(R.id.txtConv4);
        txtConv5 = (TextView) findViewById(R.id.txtConv5);
        final Bundle bundle = new Bundle();

        new Thread(() -> {
            Document doc = null;
            try{
                doc = Jsoup.connect(URL).get();	//URL 웹사이트에 있는 html 코드를 다 끌어오기
                Elements elements = doc.select(".test"); //원하는 태그만 찾아서 가져오기

                Random rand = new Random();
                String[] conv = new String[5];
                for(int i = 0 ; i < 5 ; i++) {
                    conv[i] = elements.get(rand.nextInt(elements.size())).text();
                    Log.i("!---", conv[i]);
                }
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
            txtConv1.setText(bundle.getStringArray("conv")[0]);    //받아온 데이터 textView에 출력
            txtConv2.setText(bundle.getStringArray("conv")[1]);    //받아온 데이터 textView에 출력
            txtConv3.setText(bundle.getStringArray("conv")[2]);    //받아온 데이터 textView에 출력
            txtConv4.setText(bundle.getStringArray("conv")[3]);    //받아온 데이터 textView에 출력
            txtConv5.setText(bundle.getStringArray("conv")[4]);    //받아온 데이터 textView에 출력

        }
    };
}