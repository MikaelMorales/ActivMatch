package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.unil.eda.activmatch.models.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * ActivMatchStorage abstracts the local storage of the device.
 */
public class ActivMatchStorage {

    private static final String STORAGE_USER_ID = "STORAGE_USER_ID";
    private static final String STORAGE_USER_NAME = "STORAGE_USER_NAME";
    private static final String STORAGE_GROUPS_ID = "STORAGE_GROUPS_ID";
    private static final String STORAGE_GROUPS_LEFT_ID = "STORAGE_GROUPS_LEFT_ID";
    private static final String STORAGE_MATCHES_ID = "STORAGE_MATCHES_ID";
    private static final String SETTINGS_NOTIFICATION_KEY = "SETTINGS_NOTIFICATION_KEY";

    private SharedPreferences storage;

    public ActivMatchStorage(Context context) {
        storage = context.getSharedPreferences(getClass().getName(), MODE_PRIVATE);
    }

    public void setUser(final User u) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(STORAGE_USER_ID, u.getId());
        editor.putString(STORAGE_USER_NAME, u.getName());
        editor.apply();
    }

    public User getUser() {
        String id = storage.getString(STORAGE_USER_ID, "");
        String name = storage.getString(STORAGE_USER_NAME, "");
        return new User(id, name);
    }

    public Set<String> getGroupsId() {
        return new HashSet<>(getStringList(STORAGE_GROUPS_ID, new ArrayList<>()));
    }

    public void addGroupId(String groupId) {
        List<String> ids = getStringList(STORAGE_GROUPS_ID, new ArrayList<>());
        ids.add(groupId);
        putStringList(STORAGE_GROUPS_ID, ids);
    }

    public void addNewMatch(Set<String> groupId) {
        Set<String> matches = getLastMatches();
        matches.addAll(groupId);
        putStringList(STORAGE_MATCHES_ID, new ArrayList<>(matches));
    }

    public Set<String> getLastMatches() {
        return new HashSet<>(getStringList(STORAGE_MATCHES_ID, new ArrayList<>()));
    }

    public void exitGroupId(String groupId) {
        List<String> ids = getStringList(STORAGE_GROUPS_LEFT_ID, new ArrayList<>());
        ids.add(groupId);
        putStringList(STORAGE_GROUPS_LEFT_ID, ids);
    }
    public Set<String> getGroupsLeft() {
        List<String> ids = getStringList(STORAGE_GROUPS_LEFT_ID, new ArrayList<>());
        return new HashSet<>(ids);
    }

    public boolean areNotificationsEnabled() {
        return storage.getBoolean(SETTINGS_NOTIFICATION_KEY, true);
    }

    public void enableNotifications(boolean isEnabled) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putBoolean(SETTINGS_NOTIFICATION_KEY, isEnabled);
        editor.apply();
    }

    /** Utility methods **/
    private List<String> getStringList(String key, List<String> ifNotFound) {
        int length = storage.getInt(key + "_length", -1);
        if (length == -1)
            return ifNotFound;
        ifNotFound = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            ifNotFound.add(storage.getString(key + "_" + i, null));
        }
        return ifNotFound;
    }

    private void putStringList(String key, List<String> stringList) {
        SharedPreferences.Editor editor = storage.edit();
        int length = stringList == null ? -1 : stringList.size();
        editor.putInt(key + "_length", length);
        if (length > 0) {
            int i = 0;
            for (String s : stringList) {
                editor.putString(key + "_" + i++, s);
            }
        }
        editor.apply();
    }
}
