package ch.unil.eda.activmatch.io;

import java.util.List;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.Message;
import retrofit2.Call;

public class WebServices {

    private ActivMatchServices client;

    public WebServices() {
        client = RetrofitClient.getRetrofitInstance();
    }

    public Call<Group> createGroup(Group group) {
        return client.createGroup(group);
    }

    public Call<List<Message>> getMessages(String groupId) {
        return client.getMessages(groupId);
    }

    public Call<Message> sendMessage(Message message) {
        return client.sendMessage(message);
    }

    public Call<List<String>> getMatchingTopics(String query) {
        return client.getMatchingTopics(query);
    }
}