package com.example.indoornavi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("navigation-start".equals(action)) {
            Intent i = new Intent(context, NaviActivity.class) ;
            context.startActivity(i);
        } else if("estimate-start".equals(action)) {

        }

    }
}