package ch.unil.eda.activmatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.ui.AlertDialogUtils;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;
import ch.unil.eda.activmatch.utils.ActivMatchPermissions;
import io.matchmore.sdk.Matchmore;
import io.matchmore.sdk.MatchmoreSDK;
import io.matchmore.sdk.api.models.Subscription;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class SearchActivity extends ActivMatchActivity {
    private static final double DURATION = 2.628 * Math.pow(10, 6);
    private static final double RANGE = 1000;

    private rx.Subscription searchBarSubscription = null;
    private CustomSwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private EditText searchField;
    private MatchmoreSDK matchmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle(getString(R.string.activity_group_search_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        matchmore = Matchmore.getInstance();

        recyclerView = new RecyclerView(getApplicationContext());
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.removeAllViews();
        swipeRefreshLayout.addView(recyclerView);
        swipeRefreshLayout.setEnabled(false);

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                new int[] {R.id.group_name},
                (id, item, view) -> {
                    if (id == R.id.group_name) {
                        ((TextView) view).setText(item.second);
                    }
                },
                (item, view) -> view.setOnClickListener(c -> {
                    AlertDialogUtils.confirmation(this, getString(R.string.topic_sub_confirmation, item.second), () -> subscribeToTopic(view, item.second));
                })
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.small_spacer_cell)
        ));

        searchField = findViewById(R.id.searchinput);
        searchField.setOnEditorActionListener((view, id, key) -> {
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                searchField.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        searchBarSubscription = RxTextView.textChangeEvents(searchField).
                debounce(500, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(createSearchBarObserver(hasQuery -> {
                    if (!hasQuery) {
                        adapter.clearItems();
                    } else {
                        updateDisplay();
                    }
                }));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        ActivMatchPermissions.requestLocationPermission(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        recyclerView.removeAllViews();

        if (searchBarSubscription != null)
            searchBarSubscription.unsubscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ActivMatchPermissions.LOCATION_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                finish();
            } else {
                matchmore.startUpdatingLocation();
                matchmore.startRanging();
            }
        }
    }

    private void updateDisplay() {
        swipeRefreshLayout.setRefreshing(true);
        List<Pair<Integer, String>> items = new ArrayList<>();
        List<GroupHeading> matchingGroups = service.getMatchingGroups(searchField.getText().toString());

        Set<String> myGroups = storage.getGroupsId();
        List<String> matchingTopics = matchingGroups.stream().filter(g -> !myGroups.contains(g.getGroupId()))
                .map(GroupHeading::getName)
                .collect(Collectors.toList());

        if (matchingTopics.isEmpty()) {
            items.add(new Pair<>(0, getString(R.string.topic_error)));
        } else {
            items.add(new Pair<>(99, null));
            for (String s : matchingTopics) {
                items.add(new Pair<>(1, s));
            }
        }

        GenericAdapter<Pair<Integer, String>> adapter = (GenericAdapter<Pair<Integer, String>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void subscribeToTopic(View v, String topicName) {
        AlertDialog alertDialog = AlertDialogUtils.createLoadingDialog(this);
        alertDialog.show();

        GenericAdapter<Pair<Integer, String>> adapter = (GenericAdapter<Pair<Integer, String>>) recyclerView.getAdapter();
        matchmore.startUsingMainDevice(matchmore.getMain(), d -> {
            Subscription subscription = new Subscription("ActivMatch", RANGE, DURATION);
            subscription.setSelector("name LIKE '" + topicName.toLowerCase()+"'");

            matchmore.createSubscriptionForMainDevice(subscription, createdSubscription -> {
                int position = recyclerView.getChildAdapterPosition(v);
                adapter.onItemDismiss(position);
                alertDialog.dismiss();
                return Unit.INSTANCE;
            }, e -> {
                alertDialog.dismiss();
                showErrorRetrySnackBar(() -> subscribeToTopic(v, topicName));
                return Unit.INSTANCE;
            });

            return Unit.INSTANCE;
        }, e -> {
            alertDialog.dismiss();
            showErrorRetrySnackBar(() -> subscribeToTopic(v, topicName));
            return Unit.INSTANCE;
        });
    }

    private Observer<TextViewTextChangeEvent> createSearchBarObserver(Consumer<Boolean> listener) {
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                String searchQuery = onTextChangeEvent.text().toString();
                try {
                    listener.accept(searchQuery.length() > 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
