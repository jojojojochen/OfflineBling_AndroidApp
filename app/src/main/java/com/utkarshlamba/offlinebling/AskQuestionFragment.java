package com.utkarshlamba.offlinebling;

import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by utk on 16-01-23.
 */
public class AskQuestionFragment extends Fragment {

    public static final String PHONE_NUMBER = "6476915061";

    private static String QUESTION_COMMAND = "question ";

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_askq_fragment, container, false);
    }

    public AskQuestionFragment(ProgressDialog pd) {
        progressDialog = pd;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText questionEditText = (EditText) getActivity().findViewById(R.id.question_editText);
        Button postButton = (Button) getActivity().findViewById(R.id.post_button);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.smsBody = questionEditText.getText().toString();
                if (MainActivity.smsBody.equals("")){
                    Toast.makeText(getActivity(), "Please fill the question field",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(PHONE_NUMBER, null, QUESTION_COMMAND+MainActivity.smsBody, null, null);
                    SpannableString ss=  new SpannableString("Please wait.");
                    ss.setSpan(new RelativeSizeSpan(1.5f), 0, ss.length(), 0);
                    progressDialog.setMessage(ss);
                    progressDialog.setTitle("Searching");
                    progressDialog.show();
                }

            }
        });



    }
}
