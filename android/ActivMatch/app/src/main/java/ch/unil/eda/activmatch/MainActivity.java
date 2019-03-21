package ch.unil.eda.activmatch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.models.UserStatus;
import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import ch.unil.eda.activmatch.utils.Holder;
import io.matchmore.sdk.AlpsManager;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.MobileDevice;

public class MainActivity extends ActivMatchActivity {

    private CustomSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set MainDevice for matchmore if possible
        if (storage.getFcmToken() != null && Matchmore.isConfigured()) {
            MobileDevice device = new MobileDevice(storage.getUser().getId(), "Android", "fcm://" + storage.getFcmToken(), null);
            Matchmore.getInstance().startUsingMainDevice(device, null, null);
            ((AlpsManager) Matchmore.getInstance()).registerDeviceToken("fcm://" + storage.getFcmToken());
        }

        FloatingActionButton createGroup = findViewById(R.id.fab_create_group);
        createGroup.setOnClickListener(c -> {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        });

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(this::updateDisplay);

        GenericAdapter<Pair<Integer, GroupHeading>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                new int[]{R.id.group_name},
                (id, item, view) -> {
                    if (id == R.id.group_name) {
                        ((TextView) view).setText(item.second.getName());
                    }
                },
                (item, view) -> view.setOnClickListener(c -> {
                    Intent intent = new Intent(this, GroupDetailsActivity.class);
                    intent.putExtra(GroupDetailsActivity.GROUP_ID_KEY, item.second.getGroupId());
                    intent.putExtra(GroupDetailsActivity.GROUP_NAME_KEY, item.second.getName());
                    startActivity(intent);
                })
        ));

        adapter.setCellDefinerForType(97, new CellView<>(
                ViewId.of(R.layout.small_spacer_cell)
        ));

        adapter.setCellDefinerForType(98, new CellView<>(
                ViewId.of(R.layout.big_spacer_cell)
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second.getName())
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Request Location permission
        ActivMatchPermissions.requestLocationPermission(this);

        MatchmoreSDK matchmore = Matchmore.getInstance();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            matchmore.startUpdatingLocation();
            matchmore.startRanging();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerView.removeAllViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_status) {
            toggleStatusView();
            return true;
        } else if (id == R.id.action_matches) {
            Intent intent = new Intent(this, GroupResultActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ActivMatchPermissions.LOCATION_PERMISSION_CODE) {
            MatchmoreSDK matchmore = Matchmore.getInstance();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                matchmore.startUpdatingLocation();
                matchmore.startRanging();
            }
        }
    }

    private void updateDisplay() {
        refreshLayout.setRefreshing(true);
        String userId = storage.getUser().getId();

        List<Pair<Integer, GroupHeading>> items = new ArrayList<>();
        GenericAdapter<Pair<Integer, GroupHeading>> adapter = (GenericAdapter<Pair<Integer, GroupHeading>>) recyclerView.getAdapter();

        List<GroupHeading> groups = service.getGroups(userId);
        //Small spacer
        items.add(new Pair<>(97, null));
        if (groups.isEmpty()) {
            items.add(new Pair<>(99, createDummyGroupHeading(getString(R.string.no_group_result))));
        } else {
            for (GroupHeading groupHeading : groups) {
                items.add(new Pair<>(0, groupHeading));
            }
        }

        //Big spacer
        items.add(new Pair<>(98, null));

        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private GroupHeading createDummyGroupHeading(String text) {
        return new GroupHeading("", text, "");
    }

    private void toggleStatusView() {
        String userId = storage.getUser().getId();
        UserStatus userStatus = service.getStatus(userId);
        List<UserStatus> items = Arrays.asList(UserStatus.AVAILABLE, UserStatus.BUSY);

        Holder<AlertDialog> holder = new Holder<>();
        AlertDialog alertDialog = AlertDialogUtils.createDialog(
                this,
                getString(R.string.change_status),
                items,
                new CellView<>(
                        ViewId.of(R.layout.dialog_cell),
                        new int[] {R.id.dialog_text},
                        (id, item, view) -> {
                            if (id == R.id.dialog_text) {
                                int textId = item == UserStatus.AVAILABLE ? R.string.status_available : R.string.status_busy;
                                ((TextView) view).setText(textId);
                                if (userStatus == item) {
                                    ((TextView) view).setTypeface(((TextView) view).getTypeface(), Typeface.BOLD);
                                } else {
                                    ((TextView) view).setTypeface(((TextView) view).getTypeface(), Typeface.NORMAL);
                                }
                            }
                        },
                        (item, view) -> view.setOnClickListener(c -> {
                            service.setStatus(userId, item);
                            holder.value.dismiss();
                        })
                )
        );
        holder.value = alertDialog;
        alertDialog.show();

    }
}
