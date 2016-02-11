package com.tarpost.bryanty.proj_t_post.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.object.Event;

/**
 * Created by BRYANTY on 11-Feb-16.
 */
public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, NotificationService.class);

        Bundle bundle= intent.getExtras();
        Event event= bundle.getParcelable("joinedEvent");

        service.putExtra("joinedEvent", event);
        context.startService(service);

    }
}
