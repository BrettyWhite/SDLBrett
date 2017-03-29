package com.example.brett.sdlbrett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.smartdevicelink.transport.SdlBroadcastReceiver;

public class SdlReceiver extends SdlBroadcastReceiver {

    @Override
    public void onSdlEnabled(Context context, Intent intent) {

    }


    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {

    }
}
