package com.munity.cjswyou;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MusicManager extends Service {
    private static final String TAG = MusicManager.class.getSimpleName();

    private StreamPlayer mCjsw;

    @Override
    public void onCreate() {
        super.onCreate();

        mCjsw = new StreamPlayer();
    }

    public class MusicControl extends Binder
    {
        public void play() {
            try {
                mCjsw.startCJSW();
            } catch (IOException e) {
                Log.e(TAG, "Starting CJSW Stream failed");
                Toast.makeText(MusicManager.this, "Starting CJSW Stream failed", Toast.LENGTH_LONG).show();
            }
        }

        public void stop() {
            mCjsw.stopCJSW();
        }
    }

    public IBinder onBind(Intent intent) {
        return new MusicControl();
    }
}
