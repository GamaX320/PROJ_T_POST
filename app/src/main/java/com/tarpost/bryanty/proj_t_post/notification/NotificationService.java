package com.tarpost.bryanty.proj_t_post.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tarpost.bryanty.proj_t_post.MainActivity;
import com.tarpost.bryanty.proj_t_post.R;
import com.tarpost.bryanty.proj_t_post.object.Event;
import com.tarpost.bryanty.proj_t_post.object.Post;

/**
 * Created by BRYANTY on 11-Feb-16.
 */
public class NotificationService extends Service {

    private NotificationManager mManager;
    private int numberNotification = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implement");
    }

    @Override
    public void onCreate() {
        Toast.makeText(getApplicationContext(), "Service created", Toast
                .LENGTH_SHORT).show();
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,numberNotification,
                intent1,0);

        if(Build.VERSION.SDK_INT >= 21){

            Bundle bundle= intent.getExtras();
            Event event= bundle.getParcelable("joinedEvent");

            Notification notification = new Notification.Builder(this)
                    .setContentTitle(event.getTitle())
                    .setContentText(event.getContent())
                    .setSmallIcon(R.drawable.ic_launcher_notification)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_member, "Call", pIntent)
                    .addAction(R.drawable.ic_action_action_search, "More", pIntent)
                    .addAction(R.drawable.ic_addmember, "And more", pIntent).build();

            mManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.notify(numberNotification, notification);

            numberNotification++;
        }

    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Service destroyed", Toast
                .LENGTH_SHORT).show();
        super.onDestroy();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "Service running", Toast
//                .LENGTH_SHORT).show();
//        return super.onStartCommand(intent, flags, startId);
//    }
}
