package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.Subscription;
import io.matchmore.sdk.store.CRD;

public class SubscriptionsActivity extends ActivMatchActivity {

    private RecyclerView recyclerView;
    private CustomSwipeRefreshLayout refreshLayout;
    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.subscriptions_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        matchmore = Matchmore.getInstance();

        setContentView(R.layout.list_layout);
        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(this::updateDisplay);

        GenericAdapter<Pair<Integer, Subscription>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.small_spacer_cell)
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.subscription_delete_cell),
                new int[]{ R.id.subscription_name},
                (id, item, view) -> {
                    if (id == R.id.subscription_name) {
                        String s = item.second.getSelector(); // name LIKE 'TEST'
                        int index = s.indexOf("'") + 1;
                        ((TextView) view).setText(item.second.getSelector().substring(index, s.length()-1));
                    }
                },
                (item, view) -> {
                    ImageView v = view.findViewById(R.id.subscription_delete);
                    v.setOnClickListener(c -> {
                        matchmore.getSubscriptions().delete(item.second);
                        int pos = recyclerView.getChildAdapterPosition(view);
                        adapter.onItemDismiss(pos);
                    });
                }
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

    private void updateDisplay() {
        refreshLayout.setRefreshing(true);
        CRD<Subscription> subscriptions = matchmore.getSubscriptions();
        List<Pair<Integer, Subscription>> items = new ArrayList<>();
        items.add(new Pair<>(0, null));
        for (Subscription s : subscriptions.findAll()) {
            items.add(new Pair<>(1, s));
        }
        GenericAdapter<Pair<Integer, Subscription>> adapter = (GenericAdapter<Pair<Integer, Subscription>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
