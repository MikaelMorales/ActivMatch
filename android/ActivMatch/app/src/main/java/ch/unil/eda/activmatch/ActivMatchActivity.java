package ch.unil.eda.activmatch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ch.unil.eda.activmatch.io.ActivMatchService;
import ch.unil.eda.activmatch.io.Storage;

public class ActivMatchActivity extends AppCompatActivity {
    ActivMatchService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = new Storage(getApplicationContext());
    }
}
