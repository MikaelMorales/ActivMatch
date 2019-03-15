package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.models.Message;
import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;

import static android.content.Context.MODE_PRIVATE;

public class MockStorage implements ActivMatchService {
    private static final String STATUS_KEY = "STATUS_KEY";
    private static final String GROUPS_KEY = "GROUPS_KEY";
    private static final String GROUP_KEY = "GROUP_KEY";
    private static final String USER_KEY = "USER_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";
    private static final String MESSAGES_KEY = "MESSAGES_KEY";

    private SharedPreferences storage;

    public MockStorage(Context context) {
       storage = context.getSharedPreferences(getClass().getName(), MODE_PRIVATE);
    }

    @Override
    public List<GroupHeading> getGroups(String userId) {
       List<String> groupIds = getStringList(GROUPS_KEY, new ArrayList<>());
       List<Group> groups = new ArrayList<>();
       for (String groupdId : groupIds) {
           groups.add(getGroup(groupdId));
       }

       return groups.stream()
               .map(g -> new GroupHeading(g.getGroupId(), g.getName(), g.getDescription()))
               .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessages(String groupId) {
        return getMessagesById(groupId);
    }

    @Override
    public void sendMessage(Message message) {
        putMessage(message);
    }

    @Override
    public Group getGroup(String groupId) {
        return getGroupById(groupId);
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
    public void joinGroup(User user, Group group) {
        //create group locally since offline
        group.setCreator(user);
        group.setMembers(Collections.singletonList(user));
        createGroup(group);
    }

    @Override
    public void createGroup(Group group) {
        putGroup(group.getGroupId(), group);
    }

    @Override
    public void quitGroup(String groupId) {
        deleteGroup(groupId);
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

    private void putMessage(Message message) {
        List<String> messageIds = getStringList(MESSAGES_KEY + "_" + message.getGroupId(), new ArrayList<>());
        messageIds.add(message.getMessageId());
        putStringList(MESSAGES_KEY + "_" + message.getGroupId(), messageIds);

        SharedPreferences.Editor editor = storage.edit();
        editor.putString(MESSAGE_KEY + "_" + message.getMessageId() + "_text", message.getText());
        editor.putString(MESSAGE_KEY + "_" + message.getMessageId() + "_date", message.getDate());
        putUser(editor, MESSAGE_KEY + "_" + message.getMessageId() + "_creator", message.getCreator());
        editor.apply();
    }

    private List<Message> getMessagesById(String groupId) {
        List<Message> messages = new ArrayList<>();
        List<String> messageIds = getStringList(MESSAGES_KEY + "_" + groupId, new ArrayList<>());
        for (String messageId : messageIds) {
            String text = storage.getString(MESSAGE_KEY + "_" + messageId + "_text", "");
            String date = storage.getString(MESSAGE_KEY + "_" + messageId + "_date", "");
            User creator = getUser(MESSAGE_KEY + "_" + messageId + "_creator");
            messages.add(new Message(messageId, groupId, date, text, creator));
        }
        return messages;
    }

    private void putGroup(String groupId, Group group) {
        List<String> groupIds = getStringList(GROUPS_KEY, new ArrayList<>());
        groupIds.add(group.getGroupId());
        putStringList(GROUPS_KEY, groupIds);

        SharedPreferences.Editor editor = storage.edit();
        editor.putString(GROUP_KEY + "_" + groupId + "_name", group.getName());
        editor.putString(GROUP_KEY + "_" + groupId + "_description", group.getDescription());
        putUser(editor, GROUP_KEY + "_" + groupId + "_creator", group.getCreator());
        putUser(editor,GROUP_KEY + "_" + groupId + "_members", group.getCreator());
        editor.apply();
    }

    private Group getGroupById(String groupId) {
        String name = storage.getString(GROUP_KEY + "_" + groupId + "_name", "");
        String description = storage.getString(GROUP_KEY + "_" + groupId + "_description", "");
        User creator = getUser(GROUP_KEY + "_" + groupId + "_creator");
        User member = getUser(GROUP_KEY + "_" + groupId + "_members");
        return new Group(groupId, name, description, creator, Collections.singletonList(member));
    }


    private void deleteGroup(String groupId) {
        List<String> groupIds = getStringList(GROUPS_KEY, new ArrayList<>());
        groupIds.remove(groupId);
        putStringList(GROUPS_KEY, groupIds);

        SharedPreferences.Editor editor = storage.edit();
        editor.remove(GROUP_KEY + "_" + groupId + "_name");
        editor.remove(GROUP_KEY + "_" + groupId + "_description");
        removeUserFromGroup(editor, groupId);
        editor.apply();
    }

    private void putUser(SharedPreferences.Editor editor, String key, User user) {
        editor.putString(key + "_" + USER_KEY + "_name", user.getName());
        editor.putString(key + "_" + USER_KEY + "_id", user.getId());
        setStatus(user.getId(), user.getStatus());
        editor.apply();
    }

    private User getUser(String key) {
        String name = storage.getString(key + "_" + USER_KEY + "_name", "");
        String id = storage.getString(key + "_" + USER_KEY + "_id", "");
        return new User(id, name, getStatus(id));
    }

    private void removeUserFromGroup(SharedPreferences.Editor editor, String groupId) {
        editor.remove(GROUP_KEY + "_" + groupId + "_creator" + "_" + USER_KEY + "_name");
        editor.remove(GROUP_KEY + "_" + groupId + "_creator" + "_" + USER_KEY + "_id");
        editor.apply();
    }
}