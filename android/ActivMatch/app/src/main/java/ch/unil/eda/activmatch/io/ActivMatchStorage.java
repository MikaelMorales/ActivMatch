package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public static final String STORAGE_GROUPS_ID = "STORAGE_GROUPS_ID";
    public static final String STORAGE_MATCHES_ID = "STORAGE_MATCHES_ID";

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

    public void removeGroupId(String groupId) {
        List<String> ids = getStringList(STORAGE_GROUPS_ID, new ArrayList<>());
        ids.remove(groupId);
        putStringList(STORAGE_GROUPS_ID, ids);
    }

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
