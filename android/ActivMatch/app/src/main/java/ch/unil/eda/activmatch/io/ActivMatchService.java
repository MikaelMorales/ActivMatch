package ch.unil.eda.activmatch.io;

import java.util.List;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.Message;

public interface ActivMatchService {

    List<Message> getMessages(String groupId);

    void sendMessage(Message message);

    void createGroup(Group group);

    List<String> getMatchingTopics(String query);

}
