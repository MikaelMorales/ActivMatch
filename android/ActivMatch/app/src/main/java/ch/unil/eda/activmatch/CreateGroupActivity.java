package ch.unil.eda.activmatch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.utils.ActivMatchConstants;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import ch.unil.eda.activmatch.utils.Holder;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.MatchmoreLocation;
import io.matchmore.sdk.api.models.PinDevice;
import io.matchmore.sdk.api.models.Publication;
import io.matchmore.sdk.api.models.Subscription;
import kotlin.Unit;

public class CreateGroupActivity extends ActivMatchActivity {
    private final static String TAG = "CreateGroupActivity";

    private Integer range = null;
    private MaterialButton rangeButton;
    private TextInputEditText groupDescription;
    private TextInputEditText groupName;
    private FusedLocationProviderClient mFusedLocationClient;

    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        matchmore = Matchmore.getInstance();

        rangeButton = findViewById(R.id.group_range);
        groupDescription = findViewById(R.id.group_description);
        groupName = findViewById(R.id.group_name);

        rangeButton.setOnClickListener(c -> toggleRangeView());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            Editable description = groupDescription.getText();
            Editable name = groupName.getText();
            if (range == null || description == null || name == null || description.toString().isEmpty() || name.toString().isEmpty()) {
                AlertDialogUtils.alert(this, getString(R.string.error_fields_empty), null);
            } else {
                Group group = new Group("", name.toString(), description.toString());
                publishTopic(group, range);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleRangeView() {
        Holder<AlertDialog> holder = new Holder<>();
        AlertDialog alertDialog = AlertDialogUtils.createDialog(
                this,
                getString(R.string.group_range_hint),
                ActivMatchConstants.RANGES,
                new CellView<>(
                        ViewId.of(R.layout.dialog_cell),
                        new int[]{R.id.dialog_text},
                        (id, item, view) -> {
                            if (id == R.id.dialog_text) {
                                ((TextView) view).setText(item.first);
                            }
                        },
                        (item, view) -> view.setOnClickListener(c -> {
                            range = item.second;
                            rangeButton.setText(item.first);
                            holder.value.dismiss();
                        })
                )
        );
        holder.value = alertDialog;
        alertDialog.show();
    }

    @SuppressLint("MissingPermission")
    private void publishTopic(Group group, Integer range) {
        ActivMatchPermissions.requestLocationPermission(this);
        if (!ActivMatchPermissions.hasLocationPermission(this)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_location), Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog alertDialog = AlertDialogUtils.createLoadingDialog(this);
        alertDialog.show();

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", group.getName().toLowerCase());
        properties.put("range", String.valueOf(range));
        properties.put("description", group.getDescription());

        mFusedLocationClient.getLastLocation().addOnFailureListener(e -> showErrorSnackBar(getString(R.string.error_location_unavailable)));
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                matchmore.startUsingMainDevice(matchmore.getMain(), device -> {
                    Publication publication = new Publication("ActivMatch", range.doubleValue(), ActivMatchConstants.DURATION);
                    publication.setProperties(properties);

                    PinDevice pinDevice = new PinDevice(group.getName(),
                            new MatchmoreLocation(location.getLatitude(), location.getLongitude(), location.getAltitude()));
                    matchmore.createPinDevice(pinDevice, pin -> {
                        matchmore.createPublication(publication, pin.getId(), p -> {
                            group.setGroupId(p.getId());
                            storage.addGroupId(p.getId());
                            service.createGroup(group);
                            subscribeToTopic(alertDialog, group);
                            return Unit.INSTANCE;
                        }, error -> {
                            alertDialog.dismiss();
                            Log.e(TAG, error.getMessage());
                            showErrorRetrySnackBar(() -> publishTopic(group, range));
                            return Unit.INSTANCE;
                        });
                        return Unit.INSTANCE;
                    }, e -> {
                        alertDialog.dismiss();
                        Log.e(TAG, e.getMessage());
                        showErrorRetrySnackBar(() -> publishTopic(group, range));
                        return Unit.INSTANCE;
                    });

                    return Unit.INSTANCE;
                }, e -> {
                    alertDialog.dismiss();
                    Log.e(TAG, e.getMessage());
                    showErrorRetrySnackBar(() -> publishTopic(group, range));
                    return Unit.INSTANCE;
                });
            } else {
                alertDialog.dismiss();
                showErrorSnackBar(getString(R.string.error_location_unavailable));
            }
        });
    }

    private void subscribeToTopic(AlertDialog dialog, Group group) {
        matchmore.startUsingMainDevice(matchmore.getMain(), d -> {
            Subscription subscription = new Subscription("ActivMatch", ActivMatchConstants.RANGE, ActivMatchConstants.DURATION);
            subscription.setSelector("name LIKE '" + group.getName().toLowerCase()+"'");

            matchmore.createSubscriptionForMainDevice(subscription, createdSubscription -> {
                dialog.dismiss();
                finish();
                return Unit.INSTANCE;
            }, e -> {
                dialog.dismiss();
                showErrorSnackBar(getString(R.string.error));
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        }, e -> {
            dialog.dismiss();
            showErrorSnackBar(getString(R.string.error));
            return Unit.INSTANCE;
        });
    }
}
