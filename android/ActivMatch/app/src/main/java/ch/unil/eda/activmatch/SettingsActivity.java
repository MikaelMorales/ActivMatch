package ch.unil.eda.activmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;


public class SettingsActivity extends ActivMatchActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.settings_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.list_layout);
        CustomSwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setEnabled(false);

        GenericAdapter<Pair<Integer, SwitchWrapper>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.settings_switch),
                new int[]{ R.id.settings_switch_title, R.id.settings_switch },
                (id, item, view) -> {
                    if (id == R.id.settings_switch_title) {
                        ((TextView) view).setText(item.second.mTitle);
                    } else if (id == R.id.settings_switch) {
                        Switch switchView = ((Switch) view);
                        switchView.setOnCheckedChangeListener(null);
                        switchView.setChecked(item.second.mValue);
                        view.post(() -> switchView.setOnCheckedChangeListener(item.second.mListener));
                    }
                },
                (item, view) -> view.setOnClickListener(c -> {
                    Switch switchView = findViewById(R.id.settings_switch);
                    switchView.setChecked(!switchView.isChecked());
                    item.second.mValue = switchView.isChecked();
                })
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.settings_cell),
                new int[] { R.id.settings_text },
                (id, item, view) -> {
                    if (id == R.id.settings_text) {
                        ((TextView) view).setText(R.string.settings_activity_subscriptions);
                    }
                },
                (item, view) -> view.setOnClickListener(c ->
                        startActivity(new Intent(this, SubscriptionsActivity.class)))
        ));

        adapter.setCellDefinerForType(2, new CellView<>(
                ViewId.of(R.layout.sdk_separator)
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
        updateDisplay();
    }

    private void updateDisplay() {
        List<Pair<Integer, SwitchWrapper>> items = new ArrayList<>();
        items.add(new Pair<>(1, null));
        items.add(new Pair<>(2, null));
        items.add(new Pair<>(0,
                new SwitchWrapper(
                        getString(R.string.settings_activity_notifications),
                        storage.areNotificationsEnabled(),
                        (buttonView, isChecked) -> storage.enableNotifications(isChecked)
                )));

        GenericAdapter<Pair<Integer, SwitchWrapper>> adapter = (GenericAdapter<Pair<Integer, SwitchWrapper>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
    }

    private class SwitchWrapper {
        String mTitle;
        boolean mValue;
        CompoundButton.OnCheckedChangeListener mListener;

        SwitchWrapper(String title, boolean value, CompoundButton.OnCheckedChangeListener listener) {
            mTitle = title;
            mValue = value;
            mListener = listener;
        }
    }

}
