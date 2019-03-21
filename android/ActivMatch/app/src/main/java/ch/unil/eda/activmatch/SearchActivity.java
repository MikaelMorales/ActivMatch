package ch.unil.eda.activmatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Collections;

import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.Subscription;
import kotlin.Unit;

public class SearchActivity extends ActivMatchActivity {
    private static final double DURATION = 2.628 * Math.pow(10, 6);
    private static final double RANGE = 1000;
    private EditText searchField;
    private MaterialButton subscribeButton;
    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle(getString(R.string.activity_group_search_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        matchmore = Matchmore.getInstance();

        subscribeButton = findViewById(R.id.topic_subscribe_button);
        subscribeButton.setOnClickListener(c -> onSubscribeClick());
        searchField = findViewById(R.id.searchinput);

        ActivMatchPermissions.requestLocationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivMatchPermissions.LOCATION_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
            } else {
                matchmore.startUpdatingLocation();
                matchmore.startRanging();
            }
        }
    }

    private void onSubscribeClick() {
        Editable topic = searchField.getText();
        if (topic == null || topic.toString().isEmpty()) {
            AlertDialogUtils.alert(this, getString(R.string.topic_error_empty), null);
        } else {
            String fcmToken = storage.getFcmToken();
            if (fcmToken == null) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                // TODO: Show error and retry option
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            storage.setFcmToken(token);
                            subscribeToTopic(token, topic.toString());
                        });
            } else {
                subscribeToTopic(fcmToken, topic.toString());
            }
        }
    }

    private void subscribeToTopic(String FCMToken, String topicName) {
        if (!ActivMatchPermissions.hasLocationPermission(this)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_location), Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog alertDialog = AlertDialogUtils.createLoadingDialog(this);
        alertDialog.show();

        matchmore.startUsingMainDevice(device1 -> {
            Subscription subscription = new Subscription("ActivMatch", RANGE, DURATION);
            subscription.setSelector("name LIKE '%" + topicName.toLowerCase()+"%'");
            subscription.setPushers(Collections.singletonList("fcm://" + FCMToken));

            matchmore.createSubscriptionForMainDevice(subscription, createdSubscription -> {
                alertDialog.dismiss();
                finish();
                return Unit.INSTANCE;
            }, e -> {
                alertDialog.dismiss();
                showErrorRetrySnackBar(() -> subscribeToTopic(FCMToken, topicName));
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        }, e -> {
            alertDialog.dismiss();
            showErrorRetrySnackBar(() -> subscribeToTopic(FCMToken, topicName));
            return Unit.INSTANCE;
        });

        matchmore.getMatchMonitor().startPollingMatches();
    }
}
