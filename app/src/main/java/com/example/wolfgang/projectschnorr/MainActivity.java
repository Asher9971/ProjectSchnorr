package com.example.wolfgang.projectschnorr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView lv;
    public static final String TAG = "MainActivity";
    ArrayList<String> allNames = new ArrayList<String>();
    ArrayList<String> allDebts = new ArrayList<String>();

    ArrayList<String> fertigeEintraege = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listView);
        Log.d(TAG, "nach intent aufruf*********");
        // nach Intent aufruf Extras auslesen
        Intent i = getIntent();
        Bundle params = i.getExtras();
        if(params != null){
            String input = params.getString("Schulden");
            String name = params.getString("Name");
            allNames.add(name);
            allDebts.add(input);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
