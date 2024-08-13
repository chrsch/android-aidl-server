package com.example.aidlserver;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Random;

public class AIDLColorService extends Service {

    private static final String TAG = "AIDLColorService";
    public AIDLColorService() {
        Log.d(TAG, "AIDLColorService() called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "AIDLColorService::onBind called");
        return binder;
    }

    private final IAIDLColorInterface.Stub binder = new IAIDLColorInterface.Stub(){
        @Override
        public int getColor() throws RemoteException {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Log.d(TAG, "getColor: " + color);
            return color;
        }
    };
}