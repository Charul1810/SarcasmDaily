package com.incognisyssolutions.sarcasmdaily.alarm;

/**
 * Created by Dell on 13/02/2016.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.incognisyssolutions.sarcasmdaily.MainActivity;
import com.incognisyssolutions.sarcasmdaily.R;

public class MyAlarmService extends Service

{
    private NotificationManager mManager;
    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),MainActivity.class);
        Notification notification = new Notification(R.drawable.ic_file_download,"App of the Day", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {
//            notification.setLatestEventInfo(this.getApplicationContext(), "Daily Notification Demo", "This is a test message!", pendingNotificationIntent);
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            mManager.notify(0, notification);
//        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            notification = builder.setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.drawable.ic_file_download).setTicker("New Notification")
                    .setAutoCancel(true).setContentTitle("App of the Day")
                    .setContentText("Get today's App of the Day...").build();
            mManager.notify(0, notification);
//        }


    }
    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
