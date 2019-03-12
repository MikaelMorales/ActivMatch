package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class CreateGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_create_group_title));
        setSupportActionBar(toolbar);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setEnabled(false);

        MaterialButton groupRange = findViewById(R.id.group_range);
        TextInputEditText groupDescription = findViewById(R.id.group_description);
        TextInputEditText groupName = findViewById(R.id.group_name);

        groupRange.setOnClickListener(c -> {

        });

        // TODO: HANDLE INPUT !
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            // TODO: CREATE GROUP !
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
