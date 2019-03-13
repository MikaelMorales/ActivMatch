package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.notifications.ActivMatchNotificationService;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SearchActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private String searchQuery;
    private Subscription searchBarSubscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle(getString(R.string.action_search));

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(() -> {}); // TODO

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        EditText searchField = findViewById(R.id.searchinput);
        searchBarSubscription = RxTextView.textChangeEvents(searchField).
                debounce(500, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(createSearchBarObserver(hasQuery -> {
                    if (!hasQuery) {
                        //TODO
                    } else {
                        //TODO
                    }
                }));


        String fcmToken = ActivMatchNotificationService.getToken(getApplicationContext());
        if (fcmToken == null) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Subscription matchmore
                    });
        } else {
            // Subscription matchmore
        }

    }

    private Observer<TextViewTextChangeEvent> createSearchBarObserver(Consumer<Boolean> listener) {
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.e(e.getMessage(), "the search bar observer encountered an error");
            }
            @Override
            public void onNext(TextViewTextChangeEvent onTextChangeEvent) {
                searchQuery = onTextChangeEvent.text().toString();
                listener.accept(searchQuery.length() > 0);
            }
        };
    }
}
