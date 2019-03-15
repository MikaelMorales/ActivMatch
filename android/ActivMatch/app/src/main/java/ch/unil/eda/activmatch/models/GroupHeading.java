package ch.unil.eda.activmatch.models;

import android.support.annotation.NonNull;

public class GroupHeading {

    private String mGroupId;
    private String mName;
    private String mDescription;

    public GroupHeading(String groupId, String name, String description) {
        mGroupId = groupId;
        mName = name;
        mDescription = description;
    }

    @NonNull
    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        this.mGroupId = groupId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }
}
