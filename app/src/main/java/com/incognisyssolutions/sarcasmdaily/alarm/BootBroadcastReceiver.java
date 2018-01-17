package com.incognisyssolutions.sarcasmdaily.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Dell on 19/02/2016.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    int MID=0;
    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        // we can set time by open date and time picker dialog
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (now.after(calendar)) {
            //Log.d("Hey","Added a day");
            calendar.add(Calendar.DATE, 1);
        }
        Intent intent1 = new Intent(context, AlarmReceiver1.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context
                .getSystemService(context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

    }

}
