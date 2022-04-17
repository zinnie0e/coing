package com.zinnie0e.coing.database;

import android.os.AsyncTask;
import android.util.Log;

import com.zinnie0e.coing.Define;
import com.zinnie0e.coing.MainActivity;
import com.zinnie0e.coing.fragment.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectDatabase extends AsyncTask<String, Void, String> {
    private static final String TAG = SelectDatabase.class.getSimpleName();
    String errorString = null;
    String filePath;

    public SelectDatabase(String file){
        this.filePath = file;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.d(TAG, result);
        if (result == null){
            Log.e(TAG, errorString);
        }else {
            if(filePath == Define.DB_SEL_Maxdate) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    String max_date_s = jsonArray.getJSONObject(0).getString("max_date");

                    Date now_date = new Date(System.currentTimeMillis());
                    Date max_date = StringToDate(max_date_s);

                    long diffDays = ((now_date.getTime() - max_date.getTime()) / 1000) / (24*60*60); //일자수 차이

                    Log.d("!---차이/", diffDays + "일 차이");
                    HomeFragment.setRecommend(diffDays);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String serverURL =  Define.PHP_URL + filePath;
        String postParameters = "";

        Log.i(TAG,  "serverURL" +  serverURL);

        try {
            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d(TAG, "response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            bufferedReader.close();
            return sb.toString().trim();
        } catch (Exception e) {
            Log.d(TAG, "GetData : Error ", e);
            errorString = e.toString();
            return null;
        }
    }

    private Date StringToDate(String val) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date = formatter.parse(val);
        return date;
    }
}