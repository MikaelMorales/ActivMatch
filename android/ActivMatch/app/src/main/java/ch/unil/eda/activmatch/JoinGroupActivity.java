package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;

public class JoinGroupActivity extends ActivMatchActivity {
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
    public static final String GROUP_ID = "GROUP_ID";

    private RecyclerView recyclerView;

    private String groupName;
    private String groupDescription;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getSupportActionBar().setTitle(R.string.join_group_search_title);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            groupName = args.getString(GROUP_NAME);
            groupId = args.getString(GROUP_ID);
            groupDescription = args.getString(GROUP_DESCRIPTION);
        }

        CustomSwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setEnabled(false);

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.simple_header_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(2, new CellView<>(
                ViewId.of(R.layout.simple_text_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(3, new CellView<>(
                ViewId.of(R.layout.button_cell),
                c -> onJoinClick()
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
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerView.removeAllViews();
    }

    private void updateDisplay() {
        List<Pair<Integer, String>> items = new ArrayList<>();
        items.add(new Pair<>(0, getString(R.string.group_name_header)));
        items.add(new Pair<>(1, groupName));
        items.add(new Pair<>(0, getString(R.string.group_description_header)));
        items.add(new Pair<>(1, groupDescription));
        Set<String> groups = service.getGroups(storage.getUser().getId()).stream().map(GroupHeading::getGroupId).collect(Collectors.toSet());
        if (!groups.contains(groupId)) {
            items.add(new Pair<>(3, null));
        }

        GenericAdapter<Pair<Integer, String>> adapter = (GenericAdapter<Pair<Integer, String>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private void onJoinClick() {
        AlertDialog loading = AlertDialogUtils.createLoadingDialog(getApplication());
        loading.show();
        service.joinGroup(storage.getUser(), new GroupHeading(groupId, groupName, groupDescription));
        storage.addGroupId(groupId);
        loading.dismiss();
        finish();
    }
}
