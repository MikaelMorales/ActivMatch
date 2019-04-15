package ch.unil.eda.activmatch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.Match;
import io.matchmore.sdk.api.models.MatchmoreLocation;
import io.matchmore.sdk.api.models.PublicationWithLocation;
import io.matchmore.sdk.managers.LocationSender;
import io.matchmore.sdk.managers.MatchmoreLocationProvider;

public class MainActivity extends ActivMatchActivity {

    private RecyclerView recyclerView;
    private MatchmoreSDK matchmore;
    private Handler handler = new Handler();
    private Set<GroupHeading> displayedGroups = new HashSet<>();
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        FloatingActionButton createGroup = findViewById(R.id.fab_create_group);
        createGroup.setOnClickListener(c -> {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recycler_view);
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
                    intent.putExtra(GroupDetailsActivity.GROUP_DESCRIPTION_KEY, item.second.getDescription());
                    intent.putExtra(GroupDetailsActivity.GROUP_LONGITUDE_KEY, item.second.getLongtitude());
                    intent.putExtra(GroupDetailsActivity.GROUP_LATITUDE_KEY, item.second.getLatitude());
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
                ViewId.of(R.layout.scanning_groups_cell)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        // Request Location permission
        ActivMatchPermissions.requestLocationPermission(this);

        matchmore = Matchmore.getInstance();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            matchmore.startUpdatingLocation(new LocationProvider());
            matchmore.startRanging();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerView.removeAllViews();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();

        int delay = 1000; // 1s
        handler.postDelayed(new Runnable() {
            public void run() {
                pollForNewMatches();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ActivMatchSettingsActivity.class);
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
                matchmore.startUpdatingLocation(new LocationProvider());
                matchmore.startRanging();
            } else {
                finishAffinity();
            }
        }
    }

    private void updateDisplay() {
        List<Pair<Integer, GroupHeading>> items = new ArrayList<>();
        GenericAdapter<Pair<Integer, GroupHeading>> adapter = (GenericAdapter<Pair<Integer, GroupHeading>>) recyclerView.getAdapter();
        Set<GroupHeading> groups = getGroups();
        List<GroupHeading> sortedGroups = groups.stream().sorted(Comparator.comparing(GroupHeading::getName)).collect(Collectors.toList());

        //Small spacer
        items.add(new Pair<>(97, null));
        if (sortedGroups.isEmpty()) {
            items.add(new Pair<>(99, null));
        } else {
            for (GroupHeading g : sortedGroups) {
                items.add(new Pair<>(0, g));
            }
            displayedGroups.addAll(groups);
        }

        //Big spacer
        items.add(new Pair<>(98, null));
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private void pollForNewMatches() {
        GenericAdapter<Pair<Integer, GroupHeading>> adapter = (GenericAdapter<Pair<Integer, GroupHeading>>) recyclerView.getAdapter();
        Set<GroupHeading> groups = getGroups();
        groups.removeAll(displayedGroups);
        if (groups.isEmpty()) {
            return;
        }
        List<GroupHeading> sortedGroups = groups.stream().sorted(Comparator.comparing(GroupHeading::getName)).collect(Collectors.toList());
        if (adapter.getItemCount() == 3 && adapter.getItems().get(1).first == 99) {
            adapter.onItemDismiss(1);
        }
        int length = adapter.getItemCount() - 1;
        for (GroupHeading g : sortedGroups) {
            adapter.onItemAdd(length, new Pair<>(0, g));
            length++;
        }
        adapter.onItemAdd(length, new Pair<>(98, null));
        displayedGroups.addAll(groups);
    }

    private Set<GroupHeading> getGroups() {
        Set<String> groupsLeft = storage.getGroupsLeft();
        Set<Match> matches = matchmore.getMatches();
        Set<GroupHeading> groups = new HashSet<>();
        for (Match m : matches) {
            PublicationWithLocation p = m.getPublication();
            if (groupsLeft.contains(p.getId())) {
                continue;
            }
            groups.add(new GroupHeading(p.getId(), (String) p.getProperties().get("name"),
                    (String) p.getProperties().get("description"), p.getLocation().getLatitude(), p.getLocation().getLongitude()));
        }
        return groups;
    }

    private class LocationProvider implements MatchmoreLocationProvider {

        @Override
        public void startUpdatingLocation(@NotNull LocationSender locationSender) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    locationSender.sendLocation(new MatchmoreLocation(location.getLatitude(),
                            location.getLongitude(), location.getAltitude()));
                }
            });
        }

        @Override
        public void stopUpdatingLocation() { }
    }
}
