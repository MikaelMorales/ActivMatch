package ch.unil.eda.activmatch;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;

public class GroupDetailsActivity extends ActivMatchActivity {
    public static final String GROUP_ID_KEY = "GROUP_ID_KEY";
    public static final String GROUP_DESCRIPTION_KEY = "GROUP_DESCRIPTION_KEY";
    public static final String GROUP_NAME_KEY = "GROUP_NAME_KEY";
    public static final String GROUP_LONGITUDE_KEY = "GROUP_LONGITUDE_KEY";
    public static final String GROUP_LATITUDE_KEY = "GROUP_LATITUDE_KEY";

    private CustomSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private String groupId;
    private String groupName;
    private String groupDescription;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString(GROUP_ID_KEY);
            groupName = bundle.getString(GROUP_NAME_KEY);
            groupDescription = bundle.getString(GROUP_DESCRIPTION_KEY);
            latitude = bundle.getDouble(GROUP_LATITUDE_KEY);
            longitude = bundle.getDouble(GROUP_LONGITUDE_KEY);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(groupName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton chatButton = findViewById(R.id.fab_chat);
        chatButton.setOnClickListener(c -> {
            Intent intent = new Intent(this, GroupChatActivity.class);
            intent.putExtra(GroupChatActivity.GROUP_ID_KEY, groupId);
            intent.putExtra(GroupChatActivity.GROUP_NAME_KEY, groupName);
            startActivity(intent);
        });

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(this::updateDisplay);

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.simple_header_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.simple_text_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(98, new CellView<>(
                ViewId.of(R.layout.big_spacer_cell)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.group_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_quit) {
            storage.exitGroupId(groupId);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDisplay() {
        refreshLayout.setRefreshing(true);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            if (latitude == null || longitude == null) {
                setRecyclerViewItems(Optional.empty());
            }
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                setRecyclerViewItems(Optional.of(addresses.get(0).getAddressLine(0)));
            } else {
                setRecyclerViewItems(Optional.empty());
            }
        } catch (IOException e) {
            setRecyclerViewItems(Optional.empty());
        }
    }

    private void setRecyclerViewItems(Optional<String> address) {
        List<Pair<Integer, String>> items = new ArrayList<>();
        items.add(new Pair<>(0, getString(R.string.group_name_header)));
        items.add(new Pair<>(1, groupName));
        items.add(new Pair<>(0, getString(R.string.group_description_header)));
        items.add(new Pair<>(1, groupDescription));
        if (address.isPresent()) {
            items.add(new Pair<>(0, getString(R.string.group_location_header)));
            items.add(new Pair<>(1, address.get()));
        }
        items.add(new Pair<>(98, null));

        GenericAdapter<Pair<Integer, String>> adapter = (GenericAdapter<Pair<Integer, String>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();

        refreshLayout.setRefreshing(false);
    }
}
