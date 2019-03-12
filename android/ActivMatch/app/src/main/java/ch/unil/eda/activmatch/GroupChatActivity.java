package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class GroupChatActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Subscription searchBarSubscription = null;
    private String message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // TODO: Set toolbar title with group name
        getSupportActionBar().setTitle("");

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(() -> {}); // TODO

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.chat_bubble_left),
                new int[] {R.id.message_user_name, R.id.message_text, R.id.message_date},
                (id, item, view) -> {
                    if (id == R.id.message_user_name) {
                        ((TextView) view).setText("");
                        // TODO: ((TextView) view).setTextColor(0);
                    } else if (id == R.id.message_text) {
                        ((TextView) view).setText("");
                    } else if (id == R.id.message_date) {
                        ((TextView) view).setText("");
                    }
                }
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.chat_bubble_right),
                new int[] {R.id.message_user_name, R.id.message_text, R.id.message_date},
                (id, item, view) -> {
                    if (id == R.id.message_user_name) {
                        ((TextView) view).setText("");
                        // TODO: ((TextView) view).setTextColor(0);
                    } else if (id == R.id.message_text) {
                        ((TextView) view).setText("");
                    } else if (id == R.id.message_date) {
                        ((TextView) view).setText("");
                    }
                }
        ));

        adapter.setCellDefinerForType(98, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FloatingActionButton sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(c -> {
            //TODO
        });

        EditText message = findViewById(R.id.message_text);
        searchBarSubscription = RxTextView.textChangeEvents(message).
                debounce(500, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(createSearchBarObserver(hasQuery -> {
                    if (!hasQuery) {
                        //TODO
                    } else {
                        //TODO
                    }
                }));
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
                message = onTextChangeEvent.text().toString();
                listener.accept(message.length() > 0);
            }
        };
    }
}
