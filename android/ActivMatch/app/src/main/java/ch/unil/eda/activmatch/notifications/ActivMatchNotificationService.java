package ch.unil.eda.activmatch.notifications;

import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.unil.eda.activmatch.R;
import ch.unil.eda.activmatch.io.ActivMatchService;
import ch.unil.eda.activmatch.io.ActivMatchStorage;
import ch.unil.eda.activmatch.io.MockStorage;

public class ActivMatchNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Notification notification = new NotificationCompat.Builder(this, "ActivMatch")
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("body"))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(123, notification);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        ActivMatchStorage storage = new ActivMatchStorage(getApplicationContext());
        storage.setFcmToken(s);
        // TODO: Send token to server in background
        ActivMatchService service = new MockStorage(getApplicationContext());
        service.updateUserToken(storage.getUser().getId(), s);
    }
}
