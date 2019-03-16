package ch.unil.eda.activmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;

public class GroupResultActivity extends ActivMatchActivity {

    private CustomSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        matchmore = Matchmore.getInstance();

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(this::updateDisplay);

        GenericAdapter<Pair<Integer, GroupHeading>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                new int[]{ R.id.group_name },
                (id, item, view) -> {
                    if (id == R.id.group_name) {
                        ((TextView) view).setText(item.second.getName());
                    }
                },
                (item, view) -> view.setOnClickListener(c -> {
                    Intent intent = new Intent(this, GroupDetailsActivity.class);
                    intent.putExtra(JoinGroupActivity.GROUP_ID, item.second.getGroupId());
                    intent.putExtra(JoinGroupActivity.GROUP_NAME, item.second.getName());
                    intent.putExtra(JoinGroupActivity.GROUP_DESCRIPTION, item.second.getDescription());

                    startActivity(intent);
                })
        ));

        adapter.setCellDefinerForType(97, new CellView<>(
                ViewId.of(R.layout.small_spacer_cell)
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second.getName())
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

    private void updateDisplay() {
        List<Pair<Integer, GroupHeading>> items = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        items.add(new Pair<>(97, null));
        // Get result from matchmore and display them


        GenericAdapter<Pair<Integer, GroupHeading>> adapter = (GenericAdapter<Pair<Integer, GroupHeading>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
