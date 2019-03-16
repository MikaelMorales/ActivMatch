package ch.unil.eda.activmatch.models;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(mId, user.mId) &&
                Objects.equals(mName, user.mName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mName, mStatus);
    }

    @Override
    public String toString() {
        return "User{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mStatus=" + mStatus +
                '}';
    }
}
