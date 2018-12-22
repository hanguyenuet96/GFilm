package vnu.uet.hanguyen.gfilm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator.hasVibrator()){
            vibrator.vibrate(500);
        }
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent  pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
       // notificationBuilder.setContentTitle("OnFilm");
        if(remoteMessage.getNotification() != null){
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        }
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setColor(Color.rgb(12,13,143));
        notificationBuilder.setSmallIcon(R.drawable.icon_google_play_film);
        notificationBuilder.setColorized(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}
