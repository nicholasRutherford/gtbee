package com.beeminder.gtbee.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by nick on 25/07/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private final String LOG_TAG = this.getClass().getSimpleName();

    public void onReceive(Context context, Intent intent){
        Log.v(LOG_TAG, "Network Connectivity change");

        if (intent.getExtras() != null){
            final ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()){
                Intent paymentIntent = new Intent(context, PaymentService.class);
                context.startService(paymentIntent);
            }
        }

    }
}
