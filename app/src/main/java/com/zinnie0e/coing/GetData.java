package com.zinnie0e.coing;

import android.os.AsyncTask;
import android.util.Log;

import com.zinnie0e.coing.fragment.HomeFragment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetData extends AsyncTask<String, Void, String>{
    private static final String TAG = GetData.class.getSimpleName();
    String errorString = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "response - " + result);

        if (result == null){
        }else {
            HomeFragment.mJsonString = result;
        }
    }


    @Override
    protected String doInBackground(String... params) {
        String serverURL = params[0];
        String postParameters = params[1];
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
            } else{
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
}