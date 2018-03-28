package com.toyota.tcapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.toyota.brett.tcapp.R;

public class LockScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(closeLockScreenBroadcastReceiver, new IntentFilter("CLOSE_LOCK_SCREEN"));

        //hide nav bar
        getSupportActionBar().hide();
        //attach layout
        setContentView(R.layout.activity_lock_screen);
    }


    private final BroadcastReceiver closeLockScreenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(closeLockScreenBroadcastReceiver);
        super.onDestroy();
    }
}
