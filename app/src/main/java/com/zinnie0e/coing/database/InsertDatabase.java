package com.zinnie0e.coing.database;

import android.os.AsyncTask;
import android.util.Log;

import com.zinnie0e.coing.Define;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class InsertDatabase extends AsyncTask<Void, Void, String> {
    private static final String TAG = InsertDatabase.class.getSimpleName();
    String fileName;
    HashMap<String, String> requestedParams;

    public InsertDatabase(String file, HashMap<String, String> params){
        this.fileName = file;
        this.requestedParams = params;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i(TAG, result);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String serverURL =  Define.PHP_URL + fileName;
            StringBuilder req = new StringBuilder();

            boolean ampersand = false;
            for (Map.Entry<String, String> params : requestedParams.entrySet() ){
                if (ampersand) req.append("&");
                else ampersand = true;
                req.append(URLEncoder.encode(params.getKey(), "UTF-8"));
                req.append("=");
                req.append(URLEncoder.encode(params.getValue(), "UTF-8"));
            }
            Log.i("TTT", serverURL);
            Log.i("TTTss", req.toString());

            URL url = new URL(serverURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(req.toString().getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int responseStatusCode = httpURLConnection.getResponseCode();
            Log.d("TAG", "POST response code - " + responseStatusCode);

            InputStream inputStream;
            if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else{
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (Exception e) {
            Log.d("TAG", "InsertData: Error ", e);
            return new String("Error: " + e.getMessage());
        }
    }
}