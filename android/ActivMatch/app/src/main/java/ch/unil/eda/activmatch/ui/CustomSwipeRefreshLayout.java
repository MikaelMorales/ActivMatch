package ch.unil.eda.activmatch.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    public CustomSwipeRefreshLayout(@NonNull Context context) {
        super(context);
        setNiceColors();
    }

    public CustomSwipeRefreshLayout(@NonNull Context context, @Nullable AttributeSet set) {
        super(context, set);
        setNiceColors();
    }

    private void setNiceColors() {
        setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_red_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
    }

}