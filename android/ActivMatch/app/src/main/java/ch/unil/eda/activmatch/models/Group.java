package ch.unil.eda.activmatch.models;

import java.util.List;

public class Group {

    private String mGroupId;
    private String mDescription;
    private User mCreator;
    private String mName;
    private List<User> mMembers;

    public Group(String groupId, String name, String description, User creator, List<User> members) {
        mGroupId = groupId;
        mDescription = description;
        mName = name;
        mCreator = creator;
        mMembers = members;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User mCreator) {
        this.mCreator = mCreator;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public List<User> getMembers() {
        return mMembers;
    }

    public void setMembers(List<User> mMembers) {
        this.mMembers = mMembers;
    }
}
