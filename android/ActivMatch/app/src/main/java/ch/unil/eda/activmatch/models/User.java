package ch.unil.eda.activmatch.models;

import android.support.annotation.NonNull;

import java.util.Objects;

public class User {
    private String mId;
    private String mName;

    public User(String id, String name) {
        mId = id;
        mName = name;
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
        return Objects.hash(mId, mName);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mStatus=" +
                '}';
    }
}
