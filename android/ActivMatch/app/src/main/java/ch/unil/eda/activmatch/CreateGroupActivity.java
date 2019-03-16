package ch.unil.eda.activmatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Collections;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import ch.unil.eda.activmatch.utils.ActivMatchRanges;
import ch.unil.eda.activmatch.utils.Holder;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;

public class CreateGroupActivity extends ActivMatchActivity {

    private Integer range = null;
    private MaterialButton rangeButton;
    private TextInputEditText groupDescription;
    private TextInputEditText groupName;

    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_create_group_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        matchmore = Matchmore.getInstance();

        rangeButton = findViewById(R.id.group_range);
        groupDescription = findViewById(R.id.group_description);
        groupName = findViewById(R.id.group_name);

        rangeButton.setOnClickListener(c -> toggleRangeView());

        ActivMatchPermissions.requestLocationPermission(this);
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
                //matchmore.createPublication();
                // TODO: ADD MATCHMORE AND GET GROUP ID AND SAVE SERVICE.CREATEGROUP();
               // AlertDialog alertDialog = AlertDialogUtils.createLoadingDialog(this);
               // alertDialog.show();
                service.createGroup(new Group("TEST", name.toString(), description.toString(), storage.getUser(), Collections.singletonList(storage.getUser())));
                finish();
                // When done we should call
                // alertDialog.dismiss();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivMatchPermissions.LOCATION_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
            } else {
                matchmore.startUpdatingLocation();
            }
        }
    }

    private void toggleRangeView() {
        Holder<AlertDialog> holder = new Holder<>();
        AlertDialog alertDialog = AlertDialogUtils.createDialog(
                this,
                getString(R.string.group_range_hint),
                ActivMatchRanges.RANGES,
                new CellView<>(
                        ViewId.of(R.layout.dialog_cell),
                        new int[] {R.id.dialog_text},
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
}
