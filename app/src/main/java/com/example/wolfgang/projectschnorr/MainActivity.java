package com.example.wolfgang.projectschnorr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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


public class MainActivity extends ActionBarActivity
{

    private static final int REQUEST_CODE = 666;

    ListView list;
    public static final String TAG = "MainActivity";
    public ArrayList<String> allNames = new ArrayList<String>();
    public ArrayList<String> allDebts = new ArrayList<String>();
    public ArrayList<String> allNummbers = new ArrayList<String>();
    public ArrayList<String> everything = new ArrayList<String>();
    public String myImei="";
    String selectedName="";
    String selectedNote="";
    JSONParse mTask = new JSONParse();
    JSONDelete dTask = new JSONDelete();
    JSONArray user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView) findViewById(R.id.listView);
        getMyImei();
        registerForContextMenu(list);
        mTask.execute();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(allNames.get(info.position));
            String[] menuItems = {"DELETE", "INFO"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                String temp = list.getItemAtPosition(index).toString();
                String[] temp2 = temp.split(" ");
                String selectedNameTemp = temp2[0] + " " + temp2[1];
                String[] selectedNameTemp2 = selectedNameTemp.split(":");
                selectedName = selectedNameTemp2[0];
                selectedNote = temp2[3];
                Log.d(TAG, "selectedName= " + selectedName);
                return true;
            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        Log.d(TAG, "menuItemIndex= "+menuItemIndex);
        if(menuItemIndex==0){
            deleteNotification();
        }
        return true;
    }

    private void deleteNotification() {
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

    private void getMyImei(){
        String identifier = null;
        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "IMEI or Identifier= "+identifier);
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

    private class JSONDelete extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        private final static String URLdelete_notification = "http://schnorrbert.webege.com/delete_notification.php";

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
            DefaultHttpClient clientIdentifier = new DefaultHttpClient();
            HttpPost requestIdentifier = new HttpPost(URLdelete_notification);
            List<NameValuePair> paramsDelete = new ArrayList<NameValuePair>();
            AddActivity aa = new AddActivity();
            String phone_to ="";

            for(int i=0; i<aa.allnames.size(); i++)
            {
                if(aa.allnames.get(i).equals(selectedName))
                {
                    phone_to = aa.allnumbers.get(i);
                }
            }

            paramsDelete.add(new BasicNameValuePair("identifier", ""+myImei));
            paramsDelete.add(new BasicNameValuePair("phone_to", ""+phone_to));
            paramsDelete.add(new BasicNameValuePair("note", ""+selectedNote));
            //paramsDelete.add(new BasicNameValuePair("phone_to", ""+allNummbers.get()));
            try{
                //requestIdentifier.setEntity(new UrlEncodedFormEntity(paramsIdentifier));
                HttpResponse responseIdentifier = clientIdentifier.execute(requestIdentifier);
                return null;
            }catch(Exception e){
                Log.d(TAG, "**** in Exception e im doInBackground: "+ e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG, "in onPostExecute in MainActivity");
            fillList();
        }
    }
}


