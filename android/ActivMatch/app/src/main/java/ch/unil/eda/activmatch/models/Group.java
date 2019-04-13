package ch.unil.eda.activmatch.models;

public class Group {

    private String mGroupId;
    private String mDescription;
    private String mName;

    public Group(String groupId, String name, String description) {
        mGroupId = groupId;
        mDescription = description;
        mName = name;
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

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }
}
