package ch.unil.eda.activmatch.models;

public class User {
    private String mId;
    private String mName;
    private UserStatus mStatus;

    public User(String id, String name, UserStatus status) {
        mId = id;
        mName = name;
        mStatus = status;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public UserStatus getStatus() {
        return mStatus;
    }

    public void setStatus(UserStatus mStatus) {
        this.mStatus = mStatus;
    }
}
