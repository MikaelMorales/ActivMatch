package ch.unil.eda.activmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.Device;
import io.matchmore.sdk.api.models.Match;
import io.matchmore.sdk.api.models.PublicationWithLocation;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class GroupResultActivity extends ActivMatchActivity {

    private RecyclerView recyclerView;
    private MatchmoreSDK matchmore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getSupportActionBar().setTitle(R.string.group_result_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configuration of api key/world id
        if (!Matchmore.isConfigured()) {
            Matchmore.config(this, getString(R.string.matchmore_api_key), true);
        }
        matchmore = Matchmore.getInstance();

        CustomSwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setEnabled(false);

        GenericAdapter<Pair<Integer, GroupHeading>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                new int[]{ R.id.group_name },
                (id, item, view) -> {
                    if (id == R.id.group_name) {
                        ((TextView) view).setText(item.second.getName());
                    }
                },
                (item, view) -> view.setOnClickListener(c -> {
                    Intent intent = new Intent(this, JoinGroupActivity.class);
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
    public void onDestroy() {
        super.onDestroy();
        recyclerView.removeAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerViewListItems(matchmore.getMatches());
    }

    private void setRecyclerViewListItems(Set<Match> matches) {
        List<Pair<Integer, GroupHeading>> items = new ArrayList<>();
        items.add(new Pair<>(97, null));
        if (matches.isEmpty()) {
            items.add(new Pair<>(99, new GroupHeading(null, getString(R.string.no_matches_found), null)));
        } else {
            items.addAll(matches.stream().map(m -> {
                PublicationWithLocation p = m.getPublication();
                return new Pair<>(0, new GroupHeading(p.getId(), (String) p.getProperties().get("name"), (String) p.getProperties().get("description")));
            }).distinct()
                    .filter(p -> !storage.getGroupsId().contains(p.second.getGroupId()))
                    .collect(Collectors.toList()));
        }
        updateAdapter(items);
    }

    private void updateAdapter(final List<Pair<Integer, GroupHeading>> items) {
        GenericAdapter<Pair<Integer, GroupHeading>> adapter = (GenericAdapter<Pair<Integer, GroupHeading>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }
}
