package ch.unil.eda.activmatch.notifications;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ch.unil.eda.activmatch.io.ActivMatchService;
import ch.unil.eda.activmatch.io.ActivMatchStorage;
import ch.unil.eda.activmatch.io.MockStorage;

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
        ActivMatchStorage storage = new ActivMatchStorage(getApplicationContext());
        storage.setFcmToken(s);
        // TODO: Send token to server in background
        ActivMatchService service = new MockStorage(getApplicationContext());
        service.updateUserToken(storage.getUser().getId(), s);
    }
}
