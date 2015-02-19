package com.munity.cjswyou;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Activity;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RadioDisplay extends Activity {

    private MusicManager.MusicControl mControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_display);

        Intent musicPlayerIntent = new Intent(this, MusicManager.class);
        startService(musicPlayerIntent);
        bindService(musicPlayerIntent, mMusicPlayerConnection, Activity.BIND_AUTO_CREATE);

        ((Button)findViewById(R.id.button_play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControls.play();
            }
        });

        ((Button)findViewById(R.id.button_stop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControls.stop();
            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(mMusicPlayerConnection);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.radio_display, menu);
        return true;
    }

    public ServiceConnection mMusicPlayerConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mControls = (MusicManager.MusicControl) service;

            // Setting the on onclick listeners only when we have a valid controls object
            ((Button)findViewById(R.id.button_play)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mControls.play();
                }
            });

            ((Button)findViewById(R.id.button_stop)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mControls.stop();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ((Button)findViewById(R.id.button_play)).setOnClickListener(null);
            ((Button)findViewById(R.id.button_stop)).setOnClickListener(null);
        }
    };
}
