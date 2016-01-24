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
import android.widget.Toast;

/**
 * Created by utk on 16-01-23.
 */
public class WolframAlphaSearchItemFragment extends Fragment {
    public static final String PHONE_NUMBER = "6476915061";

    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";

    private static final String WOLFRAM_COMMAND = "wolfram ";


    ProgressDialog progressDialog;

    public WolframAlphaSearchItemFragment(ProgressDialog pd){
        progressDialog = pd;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wolfram_search_item_fragment, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText queryEditText = (EditText) getActivity().findViewById(R.id.wolfram_query_editText);
        Button searchButton = (Button) getActivity().findViewById(R.id.wolfram_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.smsBody = queryEditText.getText().toString();
                if (MainActivity.smsBody.equals("")){
                    Toast.makeText(getActivity(), "Please fill search field",
                            Toast.LENGTH_LONG).show();
                }
                else{

                    PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_SENT), 0);
                    PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SMS_DELIVERED), 0);


                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(PHONE_NUMBER, null, WOLFRAM_COMMAND+MainActivity.smsBody, null, null);
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
