package com.example.wolfgang.projectschnorr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Pascal on 29.05.2015.
 */
public class MyService extends Service
{
    //´testttzzt
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
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
}
