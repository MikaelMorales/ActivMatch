package ch.unil.eda.activmatch.io;

import java.util.List;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.GroupHeading;
import ch.unil.eda.activmatch.models.Message;
import ch.unil.eda.activmatch.models.User;
import ch.unil.eda.activmatch.models.UserStatus;

public interface ActivMatchService {

    List<GroupHeading> getGroups(String userId);

    List<Message> getMessages(String groupId);

    Group getGroup(String groupId);

    UserStatus getStatus(String userId);

    void setStatus(String userId, UserStatus status);

    void joinGroup(User user, GroupHeading group);

    void createGroup(Group group);

    void sendMessage(Message message);

    void quitGroup(String groupId);

    void updateUserToken(String userId, String userToken);

}
