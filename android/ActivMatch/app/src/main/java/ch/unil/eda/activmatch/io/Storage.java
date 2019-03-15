package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.models.Message;
import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;

import static android.content.Context.MODE_PRIVATE;

public class Storage implements ActivMatchService {

    private static final String STATUS_KEY = "STATUS_KEY";
    private static final String GROUPS_KEY = "GROUPS_KEY";
    private static final String GROUP_KEY = "GROUP_KEY";
    private static final String USER_KEY = "USER_KEY";

    private SharedPreferences storage;
    private Context mContext;

    public Storage(Context context) {
       storage = context.getSharedPreferences(getClass().getName(), MODE_PRIVATE);
       mContext = context;
    }

    @Override
    public List<GroupHeading> getGroups(String userId) {
        return null;
    }

    @Override
    public List<Message> getMessages(String groupId) {
        return null;
    }

    @Override
    public Group getGroup(String groupId) {
        return null;
    }

    @Override
    public UserStatus getStatus(String userId) {
        String status = storage.getString(STATUS_KEY, UserStatus.AVAILABLE.name());
        return UserStatus.valueOf(status);
    }

    @Override
    public void setStatus(String userId, UserStatus status) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(STATUS_KEY, status.name());
        editor.apply();
    }

    @Override
    public void joinGroup(String userId, Group group) {

    }

    @Override
    public void createGroup(Group group) {
        List<String> groupIds = getStringList(GROUPS_KEY, new ArrayList<>());
        String groupId = String.valueOf(System.identityHashCode(group));
        groupIds.add(groupId);
        putStringList(GROUPS_KEY, groupIds);


        SharedPreferences.Editor editor = storage.edit();
        editor.putString(GROUP_KEY + "_" + groupId + "_name", group.getName());
        editor.putString(GROUP_KEY + "_" + groupId + "_description", group.getDescription());
        putUser(GROUP_KEY + "_" + groupId + "_creator", group.getCreator());

        editor.putString(GROUP_KEY + "_" + groupId + "_name", group.getName());
        editor.putString(GROUP_KEY + "_" + groupId + "_name", group.getName());

    }

    @Override
    public void sendMessage(Message message) {

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

    private void putUser(String key, User user) {

    }

    private User getUser(String key) {
        return null;
    }
}

