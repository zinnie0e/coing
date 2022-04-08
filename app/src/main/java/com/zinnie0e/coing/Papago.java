package com.zinnie0e.coing;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class Papago {
    public String getTranslation(String word, String source, String target) {

        String clientId = "tYGvtfcEpM4gS9_NRDUW";
        String clientSecret = "4EP4qhYR7q";

        try {
            String wordSource, wordTarget;
            String text = URLEncoder.encode(word, "UTF-8");             //word
            wordSource = URLEncoder.encode(source, "UTF-8");
            wordTarget = URLEncoder.encode(target, "UTF-8");

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "source="+wordSource+"&target="+wordTarget+"&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            //System.out.println(response.toString());
            String s = response.toString();
            JSONObject jsonObject = new JSONObject(s);
            jsonObject = new JSONObject(jsonObject.get("message").toString());
            jsonObject = new JSONObject(jsonObject.get("result").toString());
            s = jsonObject.get("translatedText").toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}