package ch.unil.eda.activmatch.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.unil.eda.activmatch.io.ActivMatchStorage;

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
        ActivMatchStorage amStorage = new ActivMatchStorage(getApplicationContext());
        amStorage.setFcmToken(s);
    }
}
