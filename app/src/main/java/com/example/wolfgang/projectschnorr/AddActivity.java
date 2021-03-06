package com.example.wolfgang.projectschnorr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;
import java.util.zip.GZIPInputStream;


public class AddActivity extends Activity
{
    ListView lv;
    Cursor cursor1;
    Intent intent;
    final static String TAG = "AddActivity";
    public ArrayList <String> allFirstNames = new ArrayList<String>();
    public ArrayList <String> allLastNames = new ArrayList<String>();
    public ArrayList <String> allnumbers = new ArrayList<String>();
    public ArrayList <String> allnames = new ArrayList<String>();
    ArrayAdapter <String> arrayadapter;
    int clickedName = 0;
    String newSchulden = "";
    TextView tv;
    String myImei="";
    String ihm_mir="";
    String ownfirst_name="";
    String ownlast_name="";
    public SharedPreferences prefs;

    EditText search;
    ArrayList<String> searchnames = new ArrayList<String>();
    ArrayList<String> filteredNames = new ArrayList<String>();
    String [] filternames = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        lv = (ListView) findViewById(R.id.listView);
        search = (EditText) findViewById(R.id.editTextSearch);
        //Schriftart ändern
        Typeface type = Typeface.createFromAsset(getAssets(),"lastcall.ttf");
        search.setTypeface(type);
        search.setTextSize(22);

        getMyImei();
        getMyName();
        fillListWithContacts();

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String input = search.getText().toString();
                refreshListView(input);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                clickedName = position;
                showAddDialog();
                
            }
        });
    }

    private void refreshListView(String input)
    {
        allFirstNames = new ArrayList<>();
        allLastNames = new ArrayList<>();
        allnumbers = new ArrayList<>();
        allnames = new ArrayList<>();
        Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = name+" .";
            String [] names = name.split(" ");
            String first_name = names[0];
            String last_name = names[1];
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(first_name.toLowerCase().contains(input.toLowerCase()) || last_name.toLowerCase().contains(input.toLowerCase())) {
                allFirstNames.add(first_name);
                allLastNames.add(last_name);
                allnames.add(first_name+" "+last_name);
                allnumbers.add(number);
            }
        }
        String [] all_array = new String [allnames.size()];
        for(int j=0; j<allnames.size();j++){
            all_array[j] = allnames.get(j).toString();
        }
        CustomAdapter listAdapter = new CustomAdapter(this, R.layout.custom_list_text, all_array, "lastcall.ttf");
        lv.setAdapter(listAdapter);
        cursor.close();
    }

    private void getMyName()
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ownfirst_name = prefs.getString("first_name", "");
        ownlast_name = prefs.getString("last_name", "");
    }

    private void getMyImei()
    {
        String identifier = null;
        TelephonyManager tm = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "IMEI or Identifier= " + identifier);
        myImei = identifier;
    }

    public void fillListWithContacts()
    {

        Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        int i = 0;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            name = name+" .";
            String [] names = name.split(" ");
            String first_name = names[0];
            String last_name = names[1];
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            allFirstNames.add(first_name);
            allLastNames.add(last_name);
            allnames.add(first_name+" "+last_name);
            allnumbers.add(number);
        }
        String [] all_array = new String [allnames.size()];
        for(int j=0; j<allnames.size();j++){
            all_array[j] = allnames.get(j).toString();
        }
        CustomAdapter listAdapter = new CustomAdapter(this, R.layout.custom_list_text, all_array, "lastcall.ttf");
        lv.setAdapter(listAdapter);
        cursor.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    private void showAddDialog()
    {
        final EditText txtName = new EditText(this);
        final Spinner howto = new Spinner(this);
        final boolean ok = false;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final Spinner whoto = new Spinner(this);
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        whoto.setAdapter(spinnerAdapter);
        spinnerAdapter.add("Ich - Ihm");
        spinnerAdapter.add("Er - Mir");
        spinnerAdapter.notifyDataSetChanged();
        whoto.setPadding(20, 20, 20, 20);
        layout.addView(whoto);


        final EditText note = new EditText(this);
        note.setHint("Notiz");
        layout.addView(note);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(allnames.get(clickedName)+" Schuldet Ihnen: ")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("OK.", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ihm_mir = whoto.getSelectedItem().toString();
                        Log.d(TAG, "ihm_mir: "+ihm_mir);
                        newSchulden = note.getText().toString();
                        new JSONPost().execute();
                        setIntent(newSchulden);
                    }
                })
                .setNegativeButton("Cancel.", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void setIntent(String input)
    {
        intent = new Intent();
        intent.putExtra("schulden", input);
        if(ihm_mir.equals("Ich - Ihm"))
        {
            intent.putExtra("first_name", "to "+allFirstNames.get(clickedName));
        }else{
            intent.putExtra("first_name", "from "+allFirstNames.get(clickedName));
        }
        intent.putExtra("last_name", allLastNames.get(clickedName));
        intent.putExtra("nummer", allnumbers.get(clickedName));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class JSONPost extends AsyncTask<String, String, JSONObject>
    {
        private final static String URLinsert_notification = "http://schnorrbert.webege.com/insert_notification.php";
        SharedPreferences prefs;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Log.d(TAG, "in onPreExecute im AsyncTask in AddActivity");
            prefs = PreferenceManager.getDefaultSharedPreferences(AddActivity.this);
        }

        @Override
        protected JSONObject doInBackground(String... args)
        {
            Log.d(TAG, "doInBackground in AddActivity");
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(URLinsert_notification);
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            String phone_to = allnumbers.get(clickedName);
            phone_to = phone_to.replaceAll(" ", "");
            int length = phone_to.length();
            if (phone_to.startsWith("+")) {
                phone_to = phone_to.substring(4, length);
            } else {
                phone_to = phone_to.substring(2, length);
            }

            String phone_from = prefs.getString("number", "");
            phone_from = phone_from.replaceAll(" ", "");
            length = phone_from.length();
            if (phone_from.startsWith("+")) {
                phone_from = phone_from.substring(4, length);
            } else {
                phone_from = phone_from.substring(2, length);
            }
            phone_to = phone_to.replace("-", "");
            phone_from = phone_from.replace("-","");
            phone_to = phone_to.replace(" ", "");
            phone_from = phone_from.replace(" ","");

            if(ihm_mir.equals("Er - Mir")) {
                params.add(new BasicNameValuePair("first_name", "from " + allFirstNames.get(clickedName)));
                params.add(new BasicNameValuePair("ownfirst_name", "to "+ownfirst_name));
            }else{
                params.add(new BasicNameValuePair("first_name", "to " + allFirstNames.get(clickedName)));
                params.add(new BasicNameValuePair("ownfirst_name", "from "+ownfirst_name));
            }
            params.add(new BasicNameValuePair("last_name", ""+allLastNames.get(clickedName)));
            params.add(new BasicNameValuePair("identifier", ""+myImei));
            params.add(new BasicNameValuePair("phone_to", ""+phone_to));
            Log.d(TAG, "phone_to: " + phone_to);
            params.add(new BasicNameValuePair("phone_from", ""+phone_from));
            Log.d(TAG, "phone_from: " + phone_from);
            params.add(new BasicNameValuePair("note", "" + newSchulden));
            params.add(new BasicNameValuePair("ownlast_name", ""+ownlast_name));

            try
            {
                request.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(request);
                return null;
            }catch(Exception e)
            {
                Log.d(TAG, "**** in Exception e in doInBackground: "+ e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json)
        {
            Log.d(TAG, "in onPostExecute in AddActivity");
        }
    }

 }
