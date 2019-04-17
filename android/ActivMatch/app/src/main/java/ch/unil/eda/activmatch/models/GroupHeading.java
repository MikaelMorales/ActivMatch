package ch.unil.eda.activmatch.models;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Utility class used in the client to display matches. This can be modified without any side effects
 * on the webservices.
 */
public class GroupHeading {

    private String mGroupId;
    private String mName;
    private String mDescription;
    private double mLatitude;
    private double mLongtitude;

    public GroupHeading(String groupId, String name, String description, double latitude, double longitude) {
        mGroupId = groupId;
        mName = name;
        mDescription = description;
        mLatitude = latitude;
        mLongtitude = longitude;
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

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongtitude() {
        return mLongtitude;
    }

    public void setLongtitude(double longtitude) {
        this.mLongtitude = longtitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupHeading that = (GroupHeading) o;
        return Objects.equals(mGroupId, that.mGroupId) &&
                Objects.equals(mName, that.mName) &&
                Objects.equals(mDescription, that.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mGroupId, mName, mDescription);
    }
}
