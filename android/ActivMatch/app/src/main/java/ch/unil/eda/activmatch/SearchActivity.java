package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.text.Editable;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import ch.unil.eda.activmatch.ui.AlertDialogUtils;

public class SearchActivity extends ActivMatchActivity {

    private EditText searchField;
    private MaterialButton subscribeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle(getString(R.string.activity_group_search_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subscribeButton = findViewById(R.id.topic_subscribe_button);
        subscribeButton.setOnClickListener(c -> onSubscribeClick());
        searchField = findViewById(R.id.searchinput);

        String fcmToken = storage.getFcmToken();
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

    private void onSubscribeClick() {
        Editable topic = searchField.getText();
        if (topic == null || topic.toString().isEmpty()) {
            AlertDialogUtils.alert(this, getString(R.string.topic_error_empty), null);
        } else {
            // TODO
            // AlertDialog alertDialog = AlertDialogUtils.createLoadingDialog(this);
            // alertDialog.show();
        }
    }
}
