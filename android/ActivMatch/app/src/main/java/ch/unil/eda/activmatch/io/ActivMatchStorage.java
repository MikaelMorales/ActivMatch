package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;

import static android.content.Context.MODE_PRIVATE;

/**
 * ActivMatchStorage abstracts the local storage of the device.
 */
public class ActivMatchStorage {

    public static final String STORAGE_USER_ID = "STORAGE_USER_ID";
    public static final String STORAGE_USER_NAME = "STORAGE_USER_NAME";
    public static final String STORAGE_USER_STATUS = "STORAGE_USER_STATUS";
    public static final String FCM_TOKEN = "FCM_TOKEN";

    private SharedPreferences storage;

    public ActivMatchStorage(Context context) {
        storage = context.getSharedPreferences(getClass().getName(), MODE_PRIVATE);
    }

    public void setUser(final User u) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(STORAGE_USER_ID, u.getId());
        editor.putString(STORAGE_USER_NAME, u.getName());
        editor.putString(STORAGE_USER_STATUS, u.getStatus().name());
        editor.apply();
    }

    public User getUser() {
        String id = storage.getString(STORAGE_USER_ID, "");
        String name = storage.getString(STORAGE_USER_NAME, "");
        String status = storage.getString(STORAGE_USER_STATUS, UserStatus.AVAILABLE.name());
        return new User(id, name, UserStatus.valueOf(status));
    }

    public void setFcmToken(final String t) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(FCM_TOKEN, t);
        editor.apply();
    }

    public String getFcmToken() {
        return storage.getString(FCM_TOKEN, "");
    }
}
