package com.utkarshlamba.offlinebling;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by utk on 16-01-23.
 */
public class FetchDataFromDBTask extends AsyncTask<String, Void, String> {



    @Override
    protected String doInBackground(String... params) {

        try{
            String link = "http://charliezhang.xyz/offlinebling/getAllData.php";

            URL url = new URL(link);



            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            StringBuilder stringBuilder = new StringBuilder();
            String inputString="";
            while ((inputString = reader.readLine())!=null){
                stringBuilder.append(inputString);
            }

            JSONObject json = new JSONObject(stringBuilder.toString());
            JSONArray questions = json.getJSONArray("questions");



            MainActivity.questionsList.clear();
            MainActivity.answersList.clear();
            MainActivity.countList.clear();

            for (int i = 0; i<questions.length(); i++){
                JSONObject obj = questions.getJSONObject(i);
                MainActivity.questionsList.add(obj.getString("question"));

                String ans = obj.getString("answer");
                if (ans.equals("")){
                    MainActivity.answersList.add("Not answered yet");
                }
                else {
                    MainActivity.answersList.add(ans);
                }
                MainActivity.countList.add(obj.getInt("count"));
            }
        } catch(Exception e){
            Log.e("FetchDataFromDBTask","exception");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        FAQFragment.adapter.notifyDataSetChanged();
        Log.e("FetchDataFromDBTask", "datasetnotified");
    }

}
