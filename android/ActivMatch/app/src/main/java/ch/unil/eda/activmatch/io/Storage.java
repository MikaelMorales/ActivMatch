package ch.unil.eda.activmatch.io;

import android.content.Context;

public abstract class Storage implements ActivMatchService {

    private Context mContext;

    public Storage(Context context) {
        mContext = context;
    }
}
