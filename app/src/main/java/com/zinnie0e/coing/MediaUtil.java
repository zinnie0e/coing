package com.zinnie0e.coing;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class MediaUtil implements View.OnClickListener{
    Context mContext;

    SpeechRecognizer mRecognizer;

    public MediaUtil(Context context){
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /*case R.id.btnVoice:
                //객체에 Context와 listener를 할당한 후 실행
                mRecognizer = SpeechRecognizer.createSpeechRecognizer((MainActivity) mContext);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(MainActivity.intent);
                break;*/
        }
    }

    /** STT */
    //RecognizerIntent 객체에 할당할 listener 생성
    private RecognitionListener listener = new RecognitionListener() {
        @Override public void onReadyForSpeech(Bundle params) {
            Toast.makeText((MainActivity) mContext,"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
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
            Toast.makeText((MainActivity) mContext, "에러가 발생하였습니다. : " + message,Toast.LENGTH_SHORT).show();
        }
        @Override public void onResults(Bundle results) {
            //말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i = 0; i < matches.size() ; i++){
                ((MainActivity) mContext).txtVoice.setText(matches.get(i));
            }
        }
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    };

    /** TTS */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void speakOut(String conv) {
        Log.i("!---", conv);
        CharSequence text = conv;
        MainActivity.tts.setPitch((float) 1.5);
        MainActivity.tts.setSpeechRate((float) 1.0);
        MainActivity.tts.speak(text, TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    static void stopSpeak(){
        if (MainActivity.tts != null) {
            MainActivity.tts.stop();
            MainActivity.tts.shutdown();
        }
    }

}
