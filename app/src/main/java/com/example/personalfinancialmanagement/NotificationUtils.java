package com.example.personalfinancialmanagement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtils extends ContextWrapper {
    private NotificationManager mManager;
    public static String mChannel_Id = null;
    public static String mChannel_Id_EX = null;

    public static String group = null;

    private CharSequence mChannleName = null;
    private CharSequence mGroupName;
    private String mDescription = "this first channel id program";
    private String mDescription2 = "this second channel id";
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationUtils(Context base) {
        super(base);
    }

    /**
     * this method create a channel.
     * this method call into a NotificationUtils class Constructor.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels(String channel1) {
        // create android channel
        mChannleName = channel1;// show channel mChannleName.
        NotificationChannel androidChannel = new NotificationChannel(channel1,
                mChannleName, NotificationManager.IMPORTANCE_DEFAULT);
        //mChannel_Id=channel1;
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        androidChannel.setDescription(mDescription);
        androidChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        androidChannel.setShowBadge(true);
        getManager().createNotificationChannel(androidChannel);
        Toast.makeText(getApplicationContext(), "Channel created", Toast.LENGTH_SHORT).show();
    }

    /**
     * this method create a channel with group _id.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannelsWithGroup(String channel1) {
        // create android channel
        mChannleName = channel1;//show channel mChannleName.
        NotificationChannel androidChannel = new NotificationChannel(channel1,
                mChannleName, NotificationManager.IMPORTANCE_DEFAULT);
        //mChannel_Id=channel1;
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        androidChannel.setDescription(mDescription);
        androidChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        androidChannel.setShowBadge(true);
        androidChannel.setGroup(group);// used when create channel with group_id.
        getManager().createNotificationChannel(androidChannel);
        Toast.makeText(getApplicationContext(), "Channel created", Toast.LENGTH_SHORT).show();
        //getManager().createNotificationChannelGroup(new NotificationChannelGroup(group,mDescription));
    }

    /**
     * this method create a group.
     *
     * @param Group
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createGroup(String Group) {
        mGroupName = Group;
        getManager().createNotificationChannelGroup(new NotificationChannelGroup(Group, mGroupName));
        Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_SHORT).show();
    }

    /**
     * this method define notification manager class service.
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //     mManager.createNotificationChannelGroup(new NotificationChannelGroup(group,mChannleName));
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), mChannel_Id)
                .setContentTitle(title)
                .setContentText(body)
                .setChannelId(mChannel_Id)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    /**
     * this method create a simple notification useing channel id.
     *
     * @param message
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder createNotification(String message) {
        Intent resultIntent = new Intent(this, AddExpenseActivity.class);
        resultIntent.putExtra("message", message);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AddExpenseActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        return new Notification.Builder(getApplicationContext(), mChannel_Id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mChannel_Id)
                .setContentText(message)
                //.setGroup(group)
                .setChannelId(mChannel_Id)
                .setContentIntent(resultPendingIntent);
    }

    /**
     * this method used create a Expand_Notification.
     *
     * @param message
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder longNotficationMessage(String message) {

        Notification.InboxStyle inboxStyle =
                new Notification.InboxStyle();
        String[] events = new String[6];
        inboxStyle.setBigContentTitle("Event tracker details:");
        for (int i = 0; i < events.length; i++) {
            events[i] = "Hii how";
            inboxStyle.addLine(events[i]);
        }
        Intent resultIntent = new Intent(this, AddExpenseActivity.class);
        resultIntent.putExtra("message", message);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AddExpenseActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        return new Notification.Builder(getApplicationContext(), mChannel_Id_EX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mChannel_Id_EX)
                .setContentText(message)
                //.setGroup(group)
                .setChannelId(mChannel_Id_EX)
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }

    /**
     * this method used to show group of notification.
     */
    public void showStackNotifications() {
        Bitmap bitmapMila = BitmapFactory.decodeResource(getResources(), R.drawable.ic_access_time_black_24dp);

        // Nuke all previous notifications and generate unique ids
        NotificationManagerCompat.from(this).cancelAll();
        int notificationId = 0;


        // Group notification that will be visible on the phone
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("2 Pet Notifications")
                .setContentText("nilay and Dylan both sent messages")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmapMila)
                .setGroup(group)
                .setGroupSummary(true)
                .build();

        // Separate notifications that will be visible on the watch
        Intent viewIntent1 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent1 =
                PendingIntent.getActivity(this, notificationId + 1, viewIntent1, 0);
        Notification notification1 = new NotificationCompat.Builder(this)
                .addAction(R.mipmap.ic_launcher, "Treat Fed", viewPendingIntent1)
                .setContentTitle("Message from nilay")
                .setContentText("What's for dinner? "
                        + "Can we have steak?")

                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroup(group)
                .build();

        Intent viewIntent2 = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent2 =
                PendingIntent.getActivity(this, notificationId + 2, viewIntent2, 0);
        Notification notification2 = new NotificationCompat.Builder(this)
                .addAction(R.mipmap.ic_launcher, "Water Filled", viewPendingIntent2)
                .setContentTitle("Message from Dylan")
                .setContentText("Can you refill our water bowl?")
                .setSmallIcon(R.drawable.ic_arrow_drop_down)
                .setGroup(group)
                .build();

        // Issue the group notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId + 0, summaryNotification);

        // Issue the separate wear notifications
        notificationManager.notify(notificationId + 2, notification2);
        notificationManager.notify(notificationId + 1, notification1);
    }
}