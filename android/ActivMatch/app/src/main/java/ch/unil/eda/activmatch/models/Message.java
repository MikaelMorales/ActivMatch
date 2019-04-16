package ch.unil.eda.activmatch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {
    @Expose
    @SerializedName("id")
    private String mMessageId;
    @Expose
    @SerializedName("group")
    private String mGroupId;
    @Expose
    @SerializedName("text")
    private String mText;
    @Expose
    @SerializedName("creator")
    private User mCreator;
    @Expose
    @SerializedName("date")
    private String mDate;

    public Message(String messageId, String groupId, String date, String text, User creator) {
        mMessageId = messageId;
        mGroupId = groupId;
        mDate = date;
        mText = text;
        mCreator = creator;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String messageId) {
        this.mMessageId = messageId;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String mGroupId) {
        this.mGroupId = mGroupId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User mCreator) {
        this.mCreator = mCreator;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }
}
