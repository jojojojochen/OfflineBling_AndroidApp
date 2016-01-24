package com.utkarshlamba.offlinebling;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by utk on 16-01-24.
 */


public class SendAnsToDBTask extends AsyncTask <String, Void, String> {

    private String question;
    private String answer;


    public SendAnsToDBTask(String question, String ans) {
        this.question = question;
        this.answer = ans;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String link = "http://charliezhang.xyz/offlinebling/addAnswer.php?question="+
                    question+ "&answer="+answer;

            Log.e(question, answer);


            URL url = new URL(link);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());
        } catch (Exception e){
            Log.e("SendAnsToDBTask","Couldn't update answer in database");

        }

        return null;
    }
}
