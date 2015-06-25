package com.example.wolfgang.projectschnorr;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pascal on 29.05.2015.
 */
public class MyService extends Service
{
    private static final String TAG = "MyService";
    ArrayList<String> allFirstNames;
    ArrayList<String> allLastNames;
    ArrayList<String> allDebts;
    boolean first;
    JSONParse jTask;
    int length = MainActivity.LENGTH;
    JSONArray user = null;
    String myImei="";

    @Override
    public void onCreate()
    {
        super.onCreate();
        first = true;
        Log.d(TAG, "im onCreate im MyService und länge = " + length);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "in onStartCommand im Service");
        getMyImei();
        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    try {
                        allFirstNames = new ArrayList<>();
                        allLastNames = new ArrayList<>();
                        allDebts = new ArrayList<>();
                        jTask = new JSONParse();
                        jTask.execute();
                        sleep(60000);
                    }catch(Exception ex){
                        Log.d(TAG, "in der Exception im Thread");
                    }
                }
            }
        };
        t.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    private void getMyImei(){
        String identifier = null;
        TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (tm != null)
            identifier = tm.getDeviceId();
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "IMEI or Identifier= " + identifier);
        myImei = identifier;
    }


    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "in onPreExecute im AsyncTask");
        }


        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            Log.d(TAG, "in doInBackground im AsyncTask");
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl("http://schnorrbert.webege.com/get_all_notifications.php?identifier="+myImei);
            Log.d(TAG, "json" + json.toString());
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
                if(first == true)
                {
                    length = allFirstNames.size();
                }
                first = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            Log.d(TAG, "in onPostExecute in MainActivity");

            Log.d(TAG, "lengthold: "+length+" lengthnew: "+allFirstNames.size());
            if(length<allFirstNames.size()){
                final NotificationManager manager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
                final Notification note = new Notification(
                        R.drawable.launchericon2, "aktiv", System.currentTimeMillis());
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                final PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
                note.setLatestEventInfo(getApplicationContext(), "Schnorrbert", "für neue Benachrichtigung hier klicken", intent);
                note.vibrate = new long[] {100, 200};
                manager.notify(666, note);
            }
        }
    }
}
