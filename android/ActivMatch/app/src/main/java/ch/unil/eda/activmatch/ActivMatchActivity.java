package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ch.unil.eda.activmatch.io.ActivMatchService;
import ch.unil.eda.activmatch.io.ActivMatchStorage;
import ch.unil.eda.activmatch.io.MockStorage;

public class ActivMatchActivity extends AppCompatActivity {
    ActivMatchService service;
    ActivMatchStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new MockStorage(getApplicationContext());
        storage = new ActivMatchStorage(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showErrorRetrySnackBar(Runnable retry) {
        View view = getRootView();
        if (view == null)
            return;
        Snackbar sb = Snackbar.make(view, R.string.error, Snackbar.LENGTH_INDEFINITE);
        if (retry != null)
            sb.setAction(R.string.sdk_retry, v -> retry.run());
        sb.show();
    }

    private View getRootView() {
        View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        return root;
    }
}
