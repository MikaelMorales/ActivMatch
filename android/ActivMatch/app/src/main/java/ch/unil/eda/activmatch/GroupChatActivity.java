package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;
import ch.unil.eda.activmatch.models.Message;
import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.ui.CustomSwipeRefreshLayout;

public class GroupChatActivity extends ActivMatchActivity {
    public static final String GROUP_ID_KEY = "GROUP_ID_KEY";
    public static final String GROUP_NAME_KEY = "GROUP_NAME_KEY";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private CustomSwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private boolean reachedBottom = false;

    private String groupId;
    private String groupName;

    private EditText message;
    private FloatingActionButton sendButton;

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString(GROUP_ID_KEY);
            groupName = bundle.getString(GROUP_NAME_KEY);
        }
        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setEnabled(false);

        GenericAdapter<Pair<Integer, Message>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.chat_bubble_left),
                new int[]{R.id.message_user_name, R.id.message_text, R.id.message_date},
                (id, item, view) -> {
                    if (id == R.id.message_user_name) {
                        ((TextView) view).setText(item.second.getCreator().getName());
                        ((TextView) view).setTextColor(getNameTint(item.second.getCreator().getName()));
                    } else if (id == R.id.message_text) {
                        ((TextView) view).setText(item.second.getText());
                    } else if (id == R.id.message_date) {
                        ((TextView) view).setText(item.second.getDate());
                    }
                }
        ));

        adapter.setCellDefinerForType(1, new CellView<>(
                ViewId.of(R.layout.chat_bubble_right),
                new int[]{R.id.message_user_name, R.id.message_text, R.id.message_date},
                (id, item, view) -> {
                    if (id == R.id.message_user_name) {
                        ((TextView) view).setText(item.second.getCreator().getName());
                        ((TextView) view).setTextColor(getNameTint(item.second.getCreator().getName()));
                    } else if (id == R.id.message_text) {
                        ((TextView) view).setText(item.second.getText());
                    } else if (id == R.id.message_date) {
                        ((TextView) view).setText(item.second.getDate());
                    }
                }
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                reachedBottom = !recyclerView.canScrollVertically(1);
            }
        });

        sendButton = findViewById(R.id.send_button);
        sendButton.setEnabled(false);
        sendButton.setOnClickListener(c -> sendMessage());

        message = findViewById(R.id.message_text);
        message.addTextChangedListener(new CustomTextWatcher());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.removeAllViews();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshLayout.setRefreshing(false);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMessages();

        int delay = 5000; // 10s
        handler.postDelayed(new Runnable(){
            public void run(){
                pollForNewMessages();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void getMessages() {
        refreshLayout.setRefreshing(true);
        User user = storage.getUser();
        List<Message> messages = service.getMessages(groupId);
        List<Pair<Integer, Message>> items = new ArrayList<>();

        for (Message message : messages) {
            int cellType = message.getCreator().equals(user) ? 1 : 0;
            items.add(new Pair<>(cellType, message));
        }
        GenericAdapter<Pair<Integer, Message>> adapter = (GenericAdapter<Pair<Integer, Message>>) recyclerView.getAdapter();
        adapter.setItems(items);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(items.size()-1);
        refreshLayout.setRefreshing(false);
    }

    private void pollForNewMessages() {
        GenericAdapter<Pair<Integer, Message>> adapter = (GenericAdapter<Pair<Integer, Message>>) recyclerView.getAdapter();
        List<Message> messages = service.getMessages(groupId);
        int currentLength = adapter.getItems().size();

        for (int i=currentLength; i < messages.size(); i++)
            adapter.onItemAdd(i, new Pair<>(0, messages.get(i)));

        if (reachedBottom)
            recyclerView.scrollToPosition(adapter.getItems().size() - 1);
    }

    private void sendMessage() {
        GenericAdapter<Pair<Integer, Message>> adapter = (GenericAdapter<Pair<Integer, Message>>) recyclerView.getAdapter();
        int length = adapter.getItems().size();

        String text = message.getText().toString();
        User user = storage.getUser();
        String date = dateFormat.format(new Date());
        Message value = new Message("", groupId, date, text, user);
        service.sendMessage(value);

        adapter.onItemAdd(length, new Pair<>(1, value));
        recyclerView.scrollToPosition(length);

        message.setText("");
    }

    private static int getNameTint(String userName) {
        int hashedColor = 0xFF000000;
        if (userName != null)
            hashedColor = userName.hashCode() | 0xFF808080;
        return hashedColor;
    }

    private class CustomTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            sendButton.setEnabled(!s.toString().isEmpty());
        }
    }
}
