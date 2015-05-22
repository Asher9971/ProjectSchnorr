package com.example.wolfgang.projectschnorr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
{

    private static final int REQUEST_CODE = 666;

    ListView list;
    public static final String TAG = "MainActivity";
    public ArrayList<String> allNames = new ArrayList<String>();
    public ArrayList<String> allDebts = new ArrayList<String>();
    public ArrayList<String> allNummbers = new ArrayList<String>();
    public ArrayList<String> everything = new ArrayList<String>();
    JSONParse mTask = new JSONParse();
    JSONArray user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        mTask.execute();
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

        if (resultCode == Activity.RESULT_OK)
        {
            Bundle params = data.getExtras();
            if (params != null)
            {
                CharSequence name = params.getCharSequence("name");
                CharSequence schulden = params.getCharSequence("schulden");
                CharSequence nummer = params.getCharSequence("nummer");

                allNames.add(name.toString());
                allDebts.add(schulden.toString());
                allNummbers.add(nummer.toString());

                fillList();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fillList()
    {
        Log.d(TAG, "in fillList***********");
        everything = new ArrayList<String>();
        for (int i = 0; i < allNames.size(); i++)
        {
            everything.add(allNames.get(i).toString() + ":  " + allDebts.get(i).toString());
            Log.d(TAG, "in for:  " + everything.get(i));
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, everything);
        list.setAdapter(adapter);

    }
    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //uid = (TextView)findViewById(R.id.uid);
            //name1 = (TextView)findViewById(R.id.name);
            //email1 = (TextView)findViewById(R.id.email);
            Log.d(TAG, "in onPreExecute im AsyncTask");
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            Log.d(TAG, "in doInBackground im AsyncTask");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://schnorrbert.webege.com/get_all_user.php");
            pDialog.dismiss();
            try {
                // Getting JSON Array
                Log.d(TAG, "in onPostExecute in AsyncTask");
                if(json == null){
                    Log.d(TAG, "json ist null");
                }
                user = json.getJSONArray("user");
                for(int i=0; i<user.length(); i++) {
                    JSONObject c = user.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String first_name = c.getString("first_name");
                    String last_name = c.getString("last_name");
                    String note = c.getString("note");
                    allNames.add(first_name+" "+last_name);
                    allDebts.add(note);

                    Log.d(TAG, "first_name = " + first_name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG, "in onPostExecute in MainActivity");
            fillList();
        }
    }

}


