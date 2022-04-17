package com.zinnie0e.coing.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.squareup.otto.Subscribe;
import com.zinnie0e.coing.DialogImage;
import com.zinnie0e.coing.adapter.MyAdapter;
import com.zinnie0e.coing.data.ConvData;
import com.zinnie0e.coing.util.ActivityResultEvent;
import com.zinnie0e.coing.adapter.AdapterWord;
import com.zinnie0e.coing.data.WordData;
import com.zinnie0e.coing.Define;
import com.zinnie0e.coing.util.EventBus;
import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.util.Papago;
import com.zinnie0e.coing.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SaveFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SaveFragment.class.getSimpleName();
    private String mParam1;
    private String mParam2;

    /** OCR */
    private TessBaseAPI mTess; //Tess API reference
    private String imageFilePath; //이미지 파일 경로
    private Uri p_Uri;
    Bitmap bitImage; //사용되는 이미지
    String datapath = ""; //언어데이터가 있는 경로
    String ocrResult = null;

    /** 번역 */
    AdapterWord adapterWord;
    ArrayList<WordData> wordDataList;
    private ArrayList words_ko = new ArrayList();
    private String present_result_ko;

    /** VIEW */
    View view;
    TextView txt_ocr;
    ImageView img_ocr;
    Button btn_pic1, btn_pic2, btn_pic3;
    LinearLayout btn_insertImg, ly_words, ly_test;
    RelativeLayout ly_img;
    EditText edt_en, edt_ko;
    ImageButton btn_prev, btn_delImg;
    ListView lst_words;

    public SaveFragment() {
        // Required empty public constructor
    }

    public static SaveFragment newInstance(String param1, String param2) {
        SaveFragment fragment = new SaveFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getInstance().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getInstance().unregister(this);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_save, container, false);

        findId();
        ly_img.setVisibility(View.GONE);
        ly_words.setVisibility(View.GONE);
        ly_test.setVisibility(View.GONE);

        //언어파일 경로
        datapath = getActivity().getFilesDir()+ "/tesseract/";
        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(new File(datapath + "tessdata/"), "kor");
        checkFile(new File(datapath + "tessdata/"), "eng");
        mTess = new TessBaseAPI();
        mTess.init(datapath, "kor+eng");
        //processImage(BitmapFactory.decodeResource(getResources(), R.drawable.test2));

        edt_en.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String input_text = edt_en.getText().toString();
                if(edt_en.length() == 0){
                    ly_words.setVisibility(view.GONE);
                    edt_ko.setText("");
                }else {
                    ly_words.setVisibility(view.VISIBLE);
                    words_ko.clear();
                    if(input_text.indexOf(" ") != -1) {
                        String[] words = input_text.split(" ");
                        new Thread(){
                            @Override
                            public void run() {
                                transWord(words);
                            }
                        }.start();
                        wordThread(words, words_ko);
                        Log.i("!-----", words.toString());
                        Log.i("!-----", words_ko.toString());
                    }
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

    private void findId() {
        btn_pic1 = (Button) view.findViewById(R.id.btn_pic1);
        btn_pic2 = (Button) view.findViewById(R.id.btn_pic2);
        btn_pic3 = (Button) view.findViewById(R.id.btn_pic3);
        btn_prev = (ImageButton) view.findViewById(R.id.btn_prev);
        btn_delImg = (ImageButton) view.findViewById(R.id.btn_delImg);

        img_ocr = (ImageView) view.findViewById(R.id.img_ocr);

        txt_ocr = (TextView) view.findViewById(R.id.txt_ocr);
        edt_en = (EditText) view.findViewById(R.id.edt_en);
        edt_ko = (EditText) view.findViewById(R.id.edt_ko);

        btn_insertImg = (LinearLayout) view.findViewById(R.id.btn_insertImg);
        ly_words = (LinearLayout) view.findViewById(R.id.ly_words);
        ly_test = (LinearLayout) view.findViewById(R.id.ly_test);
        ly_img = (RelativeLayout) view.findViewById(R.id.ly_img);

        lst_words = (ListView) view.findViewById(R.id.lst_words);

        btn_insertImg.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_delImg.setOnClickListener(this);
        btn_pic1.setOnClickListener(this);
        btn_pic2.setOnClickListener(this);
        btn_pic3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_insertImg){
            openDialogImage();
        }else if(v == btn_prev){
            ((MainActivity) getActivity()).setFragment(0);
        }else if(v == btn_pic1){
            // 사진 찍는 버튼 클릭시 카메라 킴
            sendTakePhotoIntent();
        }else if(v == btn_pic2){
            Log.i("!---", "추출중");
            ocrThread();


        }else if(v == btn_pic3){
            Log.i("here1!*****",  "");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            getActivity().startActivityForResult(intent, Define.GET_GALLERY_IMAGE);
        }else if(v == btn_delImg){
            ly_img.setVisibility(View.GONE);
        }
    }

    public void ocrThread(){
        txt_ocr.setText("기다려");
        edt_en.setHint("잠시만 기다려주세요... 텍스트 추출 중 입니다.");
        final Bundle bundle = new Bundle();
        new Thread(() -> {
            Log.i("!---", "추출중--");
            BitmapDrawable d = (BitmapDrawable) img_ocr.getDrawable();
            bitImage = d.getBitmap();
            mTess.setImage(bitImage);
            ocrResult = mTess.getUTF8Text();    //텍스트 추출
            Log.i("!---", ocrResult);

            bundle.putString("ocrResult", ocrResult);
            Message msg = handler2.obtainMessage();
            msg.setData(bundle);
            handler2.sendMessage(msg);
        }).start();
    }

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle bundle = msg.getData();    //new Thread에서 작업한 결과물 받기
            txt_ocr.setText(bundle.getString("ocrResult"));
            edt_en.setText(bundle.getString("ocrResult"));
        }
    };

    @SuppressWarnings("unused")
    @Subscribe
    public void onActivityResultEvent(@NonNull ActivityResultEvent event) {
        onActivityResult(event.getRequestCode(), event.getResultCode(), event.getData());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.GET_GALLERY_IMAGE:
                if (resultCode == Activity.RESULT_OK  && data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    Log.i("here2!*****", selectedImageUri + "");
                    img_ocr.setImageURI(selectedImageUri);
                    ly_img.setVisibility(View.VISIBLE);
                    ocrThread();
                }else Log.e(TAG, "GET_GALLERY_IMAGE");
                break;
            case Define.REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    img_ocr.setImageURI(p_Uri);
                    ExifInterface exif = null;

                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    try {
                        exif = new ExifInterface(imageFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;
                    if (exif != null) {
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegrees(exifOrientation);
                    } else {
                        exifDegree = 0;
                    }
                    img_ocr.setImageBitmap(rotate(bitmap, exifDegree));
                    ly_img.setVisibility(View.VISIBLE);
                    ocrThread();
                }else Log.e(TAG, "REQUEST_IMAGE_CAPTURE");
                break;
        }
    }

    /** OCR 라이브러리 기능 함수 */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void sendTakePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                p_Uri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName(), photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, p_Uri);
                startActivityForResult(takePictureIntent, Define.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    //장치에 파일 복사
    private void copyFiles(String lang) {
        try{
            String filepath = datapath + "/tessdata/"+lang+".traineddata";  //파일이 있을 위치
            AssetManager assetManager = getActivity().getAssets();    //AssetManager에 액세스

            //읽기/쓰기를 위한 열린 바이트 스트림
            InputStream instream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //filepath에 의해 지정된 위치에 파일 복사
            byte[] buffer = new byte[1024];
            int read;

            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check file on the device
    private void checkFile(File dir, String lang) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if(!dir.exists()&& dir.mkdirs()) {
            copyFiles(lang);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/"+lang+".traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles(lang);
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        Toast.makeText(getActivity().getApplicationContext(), "해석 중---", Toast.LENGTH_LONG).show();
        mTess.setImage(bitmap);
        ocrResult = mTess.getUTF8Text();
        txt_ocr.setText(ocrResult);
    }

    /** 번역API 기능 함수 */
    private void transWord(String[] words) {
        Log.i("!------", "=====1");
        for(int i = 0; i < words.length; i++){
            Translation(words[i], 2);
            new Thread(){
                @Override
                public void run() {
                    words_ko.add(present_result_ko);
                    Log.i("!------//", words_ko.toString());
                }
            }.start();
        }
    }

    private void Translation(String input_text, int status) {
        String word = input_text;
        Papago papago = new Papago();
        String resultWord;
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

    @SuppressLint("HandlerLeak")
    Handler papago_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String resultWord = bundle.getString("resultWord");
            edt_ko.setText(resultWord);
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
            lst_words.setAdapter(adapterWord);
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

    /** Dialog */
    private void openDialogImage() {
        Bundle args = new Bundle();
        args.putString("key", "value");
        DialogImage dialog = new DialogImage();
        dialog.setArguments(args); // 데이터 전달
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light );
        dialog.show(getActivity().getSupportFragmentManager(), "tag");

        dialog.setDialogResult(new DialogImage.OnMyDialogResult() {
            @Override
            public void finish(String result) {
                if(result == "camera"){
                    sendTakePhotoIntent();
                }else if(result == "gallery"){
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    getActivity().startActivityForResult(intent, Define.GET_GALLERY_IMAGE);
                }
            }
        });
    }
}