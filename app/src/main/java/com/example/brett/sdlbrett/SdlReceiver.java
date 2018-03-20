package com.example.brett.sdlbrett;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.smartdevicelink.transport.SdlBroadcastReceiver;

public class SdlReceiver extends SdlBroadcastReceiver {

    private static final String TAG = "SdlBroadcastReciever";

    @Override
    public void onSdlEnabled(Context context, Intent intent) {
        //Use the provided intent but set the class to the SdlService

        Log.d(TAG, "SDL Enabled");
        intent.setClass(context, SdlService.class);
		context.startService(intent);
	}

    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {
        //Return a local copy of the SdlRouterService located in your project
        return com.example.brett.sdlbrett.SdlRouterService.class;
    }
}
