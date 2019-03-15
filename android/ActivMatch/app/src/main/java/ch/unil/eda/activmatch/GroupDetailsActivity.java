package ch.unil.eda.activmatch;

import android.os.Bundle;
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

public class GroupDetailsActivity extends AppCompatActivity {
    public static final String GROUP_ID_KEY = "GROUP_ID_KEY";

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        // TODO: Set toolbar title with group name
        setSupportActionBar(toolbar);

        refreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = new RecyclerView(getApplicationContext());
        refreshLayout.addView(recyclerView);
        refreshLayout.setOnRefreshListener(() -> {}); // TODO

        GenericAdapter<Pair<Integer, String>> adapter = new GenericAdapter<>(new CellView<>(
                ViewId.of(R.layout.simple_header_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(2, new CellView<>(
                ViewId.of(R.layout.simple_text_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(3, new CellView<>(
                ViewId.of(R.layout.members_card),
                new int[] {R.id.member_name, R.id.member_status},
                (id, item, view) -> {
                    if (id == R.id.member_name) {
                        ((TextView) view).setText(item.second);
                    } else if (id == R.id.member_status) {
                        // TODO
                    }
                }
        ));

        adapter.setCellDefinerForType(4, new CellView<>(
                ViewId.of(R.layout.simple_fab_cell),
                (item, view) -> view.setOnClickListener(c -> {})
        ));

        adapter.setCellDefinerForType(98, new CellView<>(
                ViewId.of(R.layout.simple_error_cell),
                (item, view) -> ((TextView) view).setText(item.second)
        ));

        adapter.setCellDefinerForType(99, new CellView<>(
                ViewId.of(R.layout.spacer_cell)));

        adapter.setViewTypeMapper(p -> p.first);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_quit) {
            // TODO: Handle exiting group
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
