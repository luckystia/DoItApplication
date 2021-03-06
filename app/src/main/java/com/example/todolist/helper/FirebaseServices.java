package com.example.todolist.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.todolist.R;
import com.example.todolist.activity.AddEditActivity;
import com.example.todolist.activity.MainActivity;
import com.example.todolist.activity.RegisterActivity;
import com.example.todolist.model.user.User;
import com.example.todolist.model.user.UserData;
import com.example.todolist.remote.ApiService;
import com.example.todolist.remote.ApiUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseServices extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private ApiService apiService = ApiUtils.getUsetService();

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("token", "Refreshed token: " + s);
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String id = "0";
        String title = "0";
        String date = "0";
        String content = "0";
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            id = remoteMessage.getData().get("id");
            title = remoteMessage.getData().get("title");
            date = remoteMessage.getData().get("date");
            content = remoteMessage.getData().get("content");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(), id, title,date, content);
        }

    }

    // [END receive_message]
    private void sendNotification(String messageBody, String id, String title, String date, String content) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra(MainActivity.TAG_ID, id);
        intent.putExtra(MainActivity.TAG_TITLE, title);
        intent.putExtra(MainActivity.TAG_DATE, date);
        intent.putExtra(MainActivity.TAG_CONTENT, content);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "testt";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_assessment)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
