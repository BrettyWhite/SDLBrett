package com.toyota.tcapp;

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
		// SdlService needs to be foregrounded in Android O and above
		// This will prevent apps in the background from crashing when they try to start SdlService
		// Because Android O doesn't allow background apps to start background services
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			context.startForegroundService(intent);
		} else {
			context.startService(intent);
		}
	}

    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {
        //Return a local copy of the SdlRouterService located in your project
        return SdlRouterService.class;
    }
}
