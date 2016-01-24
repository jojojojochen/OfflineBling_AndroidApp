package com.utkarshlamba.offlinebling;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    ProgressDialog pd;
    TextView toolbarTitle;

    static ArrayList<String> questionsList;
    static ArrayList<String> answersList;
    static QuestionListAdapter adapter;
    static ArrayList<Integer> countList;

    public static String smsBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView)findViewById(R.id.toolbar_title);

        pd = new ProgressDialog(this);

        String [] drawerListOptions = getResources().getStringArray(R.array.list_options_array);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        ListArrayAdapter drawerAdapter = new ListArrayAdapter(this, drawerListOptions);


        drawerList.setAdapter(drawerAdapter);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame, new SearchItemFragment(pd)).commit();
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = null;
                if (position == 0) {
                    fragment = new SearchItemFragment(pd);
                    toolbarTitle.setText("Wikipedia");
                } else if (position == 1) {
                    fragment = new WolframAlphaSearchItemFragment(pd);
                    toolbarTitle.setText("Wolfram Alpha");
                } else if (position == 2) {

                    fragment = new AskQuestionFragment(pd);
                    toolbarTitle.setText("Ask Question");
                } else {
                    if (isNetworkAvailable()) {
                        fragment = new FAQFragment();
                        toolbarTitle.setText("Helpful Questions");
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "This feature is only available with an network connection",
                                Toast.LENGTH_LONG).show();
                    }
                }
                if (fragment != null) {
                    fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }

                drawerLayout.closeDrawer(drawerList);
            }
        });

        Button toggleButton = (Button) findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.closeDrawer(drawerList);

                }
                else{
                    drawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        });
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(999999999);
        registerReceiver(new SMSReceiver(fm, pd), intentFilter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            final FragmentManager fm = getFragmentManager();
            Fragment fragment = new InfoFragment();
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("MainActivity", "Error checking internet connection", e);
            }
        } else {
            Log.d("MainActivity", "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
