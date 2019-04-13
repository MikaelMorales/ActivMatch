package ch.unil.eda.activmatch.io;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.Message;
import ch.unil.eda.activmatch.models.User;

import static android.content.Context.MODE_PRIVATE;

public class MockStorage implements ActivMatchService {
    private static final String GROUPS_KEY = "GROUPS_KEY";
    private static final String USER_KEY = "USER_KEY";
    private static final String MESSAGE_KEY = "MESSAGE_KEY";
    private static final String MESSAGES_KEY = "MESSAGES_KEY";

    private SharedPreferences storage;

    public MockStorage(Context context) {
       storage = context.getSharedPreferences(getClass().getName(), MODE_PRIVATE);
    }

    @Override
    public List<String> getMatchingTopics(String query) {
        return getStringList(GROUPS_KEY, new ArrayList<>()).stream()
                .filter(s -> s.startsWith(query)).collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessages(String groupId) {
        return getMessagesById(groupId);
    }

    @Override
    public void sendMessage(Message message) {
        message.setMessageId(String.valueOf(System.identityHashCode(message)));
        putMessage(message);
    }

    @Override
    public void createGroup(Group group) {
        List<String> groups = getStringList(GROUPS_KEY, new ArrayList<>());
        groups.add(group.getName());
        putStringList(GROUPS_KEY, groups);
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


    private void putUser(SharedPreferences.Editor editor, String key, User user) {
        editor.putString(key + "_" + USER_KEY + "_name", user.getName());
        editor.putString(key + "_" + USER_KEY + "_id", user.getId());
        editor.apply();
    }

    private User getUser(String key) {
        String name = storage.getString(key + "_" + USER_KEY + "_name", "");
        String id = storage.getString(key + "_" + USER_KEY + "_id", "");
        return new User(id, name);
    }

    /** Utils **/
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