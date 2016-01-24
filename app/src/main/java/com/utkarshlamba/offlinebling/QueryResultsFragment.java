package com.utkarshlamba.offlinebling;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by utk on 16-01-23.
 */
public class QueryResultsFragment extends Fragment {

    private String info;
    private String query;

    public QueryResultsFragment(String info) {
        //this.query = query;
        this.info = info;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_query_results_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView infoTextView = (TextView) getActivity().findViewById(R.id.query_info_textView);
        TextView titleTextView = (TextView) getActivity().findViewById(R.id.query_title_textView);
        titleTextView.setText(MainActivity.smsBody);
        infoTextView.setText(info);
    }
}
