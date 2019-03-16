package ch.unil.eda.activmatch;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;

public class GroupDetailsActivity extends ActivMatchActivity {
    public static final String GROUP_ID_KEY = "GROUP_ID_KEY";
    public static final String GROUP_NAME_KEY = "GROUP_NAME_KEY";

    private FloatingActionButton chatButton;
    private CustomSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private String groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString(GROUP_ID_KEY);
            groupName = bundle.getString(GROUP_NAME_KEY);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(groupName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatButton = findViewById(R.id.fab_chat);
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

        GenericAdapter<Pair<Integer, GroupWrapper>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.simple_header_cell),
                (item, view) -> ((TextView) view).setText(item.second.name)
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.simple_text_cell),
                (item, view) -> ((TextView) view).setText(item.second.name)
        ));

        adapter.setCellDefinerForType(2, new CellView<>(
                ViewId.of(R.layout.simple_text_cell),
                (item, view) -> ((TextView) view).setText(item.second.description)
        ));

        adapter.setCellDefinerForType(3, new CellView<>(
                ViewId.of(R.layout.members_card),
                new int[] {R.id.member_name, R.id.member_status},
                (id, item, view) -> {
                    if (id == R.id.member_name) {
                        ((TextView) view).setText(item.second.user.getName());
                    } else if (id == R.id.member_status) {
                        boolean available = item.second.user.getStatus() == UserStatus.AVAILABLE;
                        ((ImageView) view).setImageResource(available ? R.drawable.ic_status_available : R.drawable.ic_status_busy);
                        ImageViewCompat.setImageTintList((ImageView) view, ColorStateList.valueOf(available ? getColor(R.color.green) : getColor(R.color.red)));
                    }
                }
        ));

        adapter.setCellDefinerForType(98, new CellView<>(
                ViewId.of(R.layout.big_spacer_cell)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
            service.quitGroup(groupId);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDisplay() {
        refreshLayout.setRefreshing(true);

        Group group = service.getGroup(groupId);
        List<Pair<Integer, GroupWrapper>> items = new ArrayList<>();
        items.add(new Pair<>(0, new GroupWrapper(getString(R.string.group_name_header), null, null)));
        items.add(new Pair<>(1, new GroupWrapper(group.getName(), null, null)));
        items.add(new Pair<>(0, new GroupWrapper(getString(R.string.group_description_header), null, null)));
        items.add(new Pair<>(2, new GroupWrapper(null, group.getDescription(), null)));
        items.add(new Pair<>(0, new GroupWrapper(getString(R.string.group_members_header), null, null)));
        for (User user : group.getMembers()) {
            items.add(new Pair<>(3, new GroupWrapper(null, null, user)));
        }
        items.add(new Pair<>(98, null));

        GenericAdapter<Pair<Integer, GroupWrapper>> adapter = (GenericAdapter<Pair<Integer, GroupWrapper>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();

        refreshLayout.setRefreshing(false);
    }

    private class GroupWrapper {
        String description;
        String name;
        User user;

        GroupWrapper(String name, String description, User user) {
            this.user = user;
            this.description = description;
            this.name = name;
        }
    }
}
