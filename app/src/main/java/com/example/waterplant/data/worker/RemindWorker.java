package com.example.waterplant.data.worker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.waterplant.common.Constant;
import com.example.waterplant.presentation.ui.main_screen.MainActivity;
import com.example.waterplant.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RemindWorker extends Worker {
    Context context;
    public RemindWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString("name");
        String[] dates = getInputData().getStringArray("waterday");
        try {
            Log.e(this.getClass().getName(), name);
            Log.e(this.getClass().getName(), dates.toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        Notification notification = createNotification(context, Constant.CHANNEL_ID, name);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        SimpleDateFormat format = new SimpleDateFormat("EEE", Locale.US);
        Date date = new Date();
        String today = format.format(date);

        for (String i : dates) {
            if(today.toLowerCase().equals(i)) {
                notificationManagerCompat.notify(Constant.NOTIFICATION_ID, notification);
                Log.d("RemindWorker", name + " reminder is running");
            }
        }


        return Result.success();
    }

    private Notification createNotification(Context context, String channelId, String plantName) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent,PendingIntent.FLAG_IMMUTABLE) ;
        return new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Time for plant your " + plantName)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.ic_drop_water)
                .setContentIntent(pendingIntent)
                .build();
    }
}
