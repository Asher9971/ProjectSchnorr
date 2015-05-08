package com.example.wolfgang.projectschnorr;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
{

    private static final int REQUEST_CODE = 666;

    ListView list;
    public static final String TAG = "MainActivity";
    public ArrayList<String> allNames = new ArrayList<String>();
    public ArrayList<String> allDebts = new ArrayList<String>();
    public ArrayList<String> everything = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        Log.d(TAG, "nach intent aufruf*********");
        // nach Intent aufruf Extras auslesen

        Intent i = getIntent();
        Bundle params = i.getExtras();
        if(params != null){
            String input = params.getString("schulden");
            String name = params.getString("name");
            allNames.add(name);
            allDebts.add(input);

            fillList();
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
        if (id == R.id.action_add)
        {
            Intent intent = new Intent(this, AddActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*
        if (resultCode == Activity.RESULT_OK) {
            Intent i = getIntent();
            Bundle params = i.getExtras();
            if (params != null) {
                String input = params.getString("schulden");
                String name = params.getString("name");
                allNames.add(name);
                allDebts.add(input);

                fillList();
            }
        }
        */
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fillList()
    {
        Log.d(TAG, "in fillList***********");

        for (int i = 0; i < allNames.size(); i++)
        {
            everything.add(allNames.get(i).toString() + ":  " + allDebts.get(i).toString());
            Log.d(TAG, "in for:  " + everything.get(i));
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, everything);
        list.setAdapter(adapter);

    }

}
