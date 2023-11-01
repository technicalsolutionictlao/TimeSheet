package com.ictlao.android.app.timesheet;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ictlao.android.app.timesheet.Items.DataResult;
import com.ictlao.android.app.timesheet.Items.MessageItems;
import com.ictlao.android.app.timesheet.Manager.DataManager;

import java.util.ArrayList;

public class NotificationService extends Service {

    // id of the notification channel
    private static final String CHANNEL_ID = "NOTIFY_ID";
    // name of the notification channel
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    // notify id
    public static final int NOTIFY_ID = 111;

    // constructor of the service
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        onNotificationMessage();
        return START_STICKY;
    }

    // create notification message
    private void onNotificationMessage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        DataManager.onNotificationMessageListener(new DataResult() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                ArrayList<MessageItems> list = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()) {
                    MessageItems items = data.getValue(MessageItems.class);
                    if (items != null) {
                        if (items.getDate().equals(DataManager.getCurrentDate())) {
                            list.add(items);
                            onShowNotification(items.getName(), items.getDate() + " " + items.getTime(), list.size());
                        }
                    }
                }
            }

            @Override
            public void onError(DatabaseError error) {
            }
        });
    }

    // show notification message
    private void onShowNotification(String title,String content, int number){
        Intent resultIntent = new Intent(this, NotificationActivity.class);
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_IMMUTABLE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_clock);
        builder.setAutoCancel(true);
        builder.setSound(alarmSound);
        builder.setNumber(number);
        //builder.setContentIntent(resultPendingIntent);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFY_ID,builder.build());
    }
}