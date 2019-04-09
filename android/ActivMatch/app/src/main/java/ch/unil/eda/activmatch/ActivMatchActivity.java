package ch.unil.eda.activmatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.io.ActivMatchService;
import ch.unil.eda.activmatch.io.ActivMatchStorage;
import ch.unil.eda.activmatch.io.MockStorage;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import kotlin.Unit;

public class ActivMatchActivity extends AppCompatActivity {
    ActivMatchService service;
    ActivMatchStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new MockStorage(getApplicationContext());
        storage = new ActivMatchStorage(getApplicationContext());

        // Configuration of api key/world id
        if (!Matchmore.isConfigured()) {
            Matchmore.config(this, getString(R.string.matchmore_api_key), false);
        }

        MatchmoreSDK matchmore = Matchmore.getInstance();
        matchmore.startUsingMainDevice(device -> {
            matchmore.getMatchMonitor().addOnMatchListener((matches, d) -> {
                Set<String> myGroups = storage.getGroupsId();
                Set<String> lastMatches = storage.getLastMatches();
                Set<String> currentMatches = matches.stream()
                        .filter(p -> !myGroups.contains(p.getPublication().getId()))
                        .map(p -> p.getPublication().getId()).collect(Collectors.toSet());
                if (!lastMatches.containsAll(currentMatches)) {
                    storage.addNewMatch(currentMatches);
                    sendMatchNotification();
                }
                return Unit.INSTANCE;
            });

            matchmore.getMatchMonitor().startPollingMatches();

            return Unit.INSTANCE;
        }, e -> Unit.INSTANCE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showErrorRetrySnackBar(Runnable retry) {
        View view = getRootView();
        if (view == null)
            return;
        Snackbar sb = Snackbar.make(view, R.string.error, Snackbar.LENGTH_INDEFINITE);
        if (retry != null)
            sb.setAction(R.string.sdk_retry, v -> retry.run());
        sb.show();
    }

    private View getRootView() {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        return rootView;
    }

    private void sendMatchNotification() {
        if (!storage.areNotificationsEnabled()) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel("ActivMatch", "ActivMatch", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        Intent intent = new Intent(this, GroupResultActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), "ActivMatch")
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notifications)
                .setColor(getColor(R.color.colorAccent))
                .setContentText(getString(R.string.notifications_matches))
                .setWhen(System.currentTimeMillis())
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        notificationManager.notify("ActivMatch", 1, notificationBuilder.build());
    }
}
