package ch.unil.eda.activmatch.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ActivMatchNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        getSharedPreferences("FCM_TOKEN", MODE_PRIVATE).edit().putString("fcm", s).apply();
    }

    @Nullable
    public static String getToken(@NonNull Context context) {
        return context.getSharedPreferences("FCM_TOKEN", MODE_PRIVATE).getString("fcm", null);
    }
}
