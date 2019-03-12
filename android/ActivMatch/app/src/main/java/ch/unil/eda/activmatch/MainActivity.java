package ch.unil.eda.activmatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ch.unil.eda.activmatch.adapter.CellView;
import ch.unil.eda.activmatch.adapter.GenericAdapter;
import ch.unil.eda.activmatch.adapter.ViewId;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton createGroup = findViewById(R.id.fab_create_group);
        createGroup.setOnClickListener(c -> {
            Intent intent = new Intent(this, CreateGroupActivity.class);
            startActivity(intent);
        });

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(() -> {}); // TODO

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.group_simple_card),
                (item, view) -> {
                    ((TextView) view).setText(item.second);
                    view.setOnClickListener(c -> {
                        // TODO:
                    });
                }
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_status) {
            // TODO: Handle status
            return true;
        } else if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
