package com.example.wolfgang.projectschnorr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;


public class MainActivity extends ActionBarActivity
{

    private static final int REQUEST_CODE = 666;

    ListView list;
    public static final String TAG = "MainActivity";
    public ArrayList<String> allFirstNames = new ArrayList<String>();
    public ArrayList<String> allLastNames = new ArrayList<String>();
    public ArrayList<String> allDebts = new ArrayList<String>();
    public ArrayList<String> allNummbers = new ArrayList<String>();
    public ArrayList<String> everything = new ArrayList<String>();
    public SharedPreferences prefs;
    private String myImei="";
    //Test
    String selectedName="";
    String selectedNote="";
    private int position=0;
    JSONParse mTask = new JSONParse();
    JSONDelete dTask;
    JSONArray user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        list = (ListView) findViewById(R.id.listView);
        getMyImei();
        registerForContextMenu(findViewById(R.id.listView));
        askForPhoneNumber();
        mTask.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Log.d(TAG, "in onCreateContextMenu");
        AdapterView.AdapterContextMenuInfo infoOfItem = (AdapterView.AdapterContextMenuInfo) menuInfo;
        position = infoOfItem.position;
        if (v.getId()==R.id.listView){
            Log.d(TAG, "IN IF im onCreateContextMenu");
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(allFirstNames.get(info.position)+" "+allLastNames.get(info.position));
            String[] menuItems = {"DELETE", "INFO"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    public void askForPhoneNumber()
    {
        String phoneNumber = prefs.getString("number", "");
        if(phoneNumber.equals(""))
        {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        Toast.makeText(this, "Nummer: "+phoneNumber, Toast.LENGTH_LONG).show();
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        Log.d(TAG, "in onContextItemSelected");
        Log.d(TAG, "menuItemIndex= "+menuItemIndex);
        if(menuItemIndex==0){
            deleteNotification();
        }
        return true;
    }

    private void deleteNotification()
    {
        dTask = new JSONDelete();
        dTask.execute();
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

        switch (id)
        {
            case R.id.action_add:
                Intent intentAdd = new Intent(this, AddActivity.class);
                startActivityForResult(intentAdd, REQUEST_CODE);
                break;

            case R.id.action_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivityForResult(intentAbout, REQUEST_CODE);
                break;

            case R.id.action_prefs:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
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
                CharSequence first_name = params.getCharSequence("first_name");
                CharSequence last_name = params.getCharSequence("last_name");
                CharSequence schulden = params.getCharSequence("schulden");
                CharSequence nummer = params.getCharSequence("nummer");

                allFirstNames.add(first_name.toString());
                allLastNames.add(last_name.toString());
                allDebts.add(schulden.toString());
                allNummbers.add(nummer.toString());
                fillList();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void loadAgain()
    {
        allFirstNames = new ArrayList<>();
        allLastNames = new ArrayList<>();
        allDebts = new ArrayList<>();
        mTask.cancel(true);
        mTask = new JSONParse();
        dTask.cancel(true);
        dTask = new JSONDelete();
        mTask.execute();
    }

    public void fillList()
    {
        Log.d(TAG, "in fillList***********");
        everything = new ArrayList<String>();
        for (int i = 0; i < allFirstNames.size(); i++)
        {
            everything.add(allFirstNames.get(i).toString() + " " + allLastNames.get(i).toString() + ":  " + allDebts.get(i).toString());
            Log.d(TAG, "in for:  " + everything.get(i));
        }

        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, everything);
        list.setAdapter(adapter);

    }

    private void getMyImei(){
        String identifier = null;
        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "IMEI or Identifier= " + identifier);
        String testtt = tm.getLine1Number();
        Log.d(TAG, "meineNummer: "+testtt);
        myImei = identifier;

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();

        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            Log.d(TAG, "acname: "+acname+ "actype: "+actype);
        }
    }


    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            JSONObject json = jParser.getJSONFromUrl("http://schnorrbert.webege.com/get_all_notifications.php?identifier="+myImei);
            Log.d(TAG, "json"+json.toString());
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
                    String identifier = c.getString("identifier");
                    allFirstNames.add(first_name);
                    allLastNames.add(last_name);
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

    private class JSONDelete extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private final static String URLdelete_notification = "http://schnorrbert.webege.com/delete_new.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "in onPreExecute im AsyncTask");
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Lösche Notiz...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(URLdelete_notification);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("identifier", ""+myImei));
            params.add(new BasicNameValuePair("first_name", allFirstNames.get(position)));
            params.add(new BasicNameValuePair("last_name", allLastNames.get(position)));
            params.add(new BasicNameValuePair("note", allDebts.get(position)));
            Log.d(TAG, "identifier: " + myImei);
            Log.d(TAG, "first_name: "+allFirstNames.get(position));
            Log.d(TAG, "last_name: "+allLastNames.get(position));
            Log.d(TAG, "note: "+allDebts.get(position));
            try{
                request.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(request);
                return null;
            }catch(Exception e){
                Log.d(TAG, "**** in Exception e in doInBackground: "+ e.toString());
            }

            Log.d(TAG, "delete in doInBackground");
            DefaultHttpClient clientIdentifier = new DefaultHttpClient();
            HttpPost requestIdentifier = new HttpPost(URLdelete_notification);
            List<NameValuePair> paramsIdentifier = new ArrayList<NameValuePair>();
            paramsIdentifier.add(new BasicNameValuePair("identifier", ""+myImei));
            try{
                requestIdentifier.setEntity(new UrlEncodedFormEntity(paramsIdentifier));
                HttpResponse responseIdentifier = clientIdentifier.execute(requestIdentifier);
                return null;
            }catch(Exception e){
                Log.d(TAG, "**** in Exception e in doInBackground: "+ e.toString());
            }

            return null;

        }

        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG, "in onPostExecute in MainActivity");
            pDialog.dismiss();
            loadAgain();
        }
    }
}


