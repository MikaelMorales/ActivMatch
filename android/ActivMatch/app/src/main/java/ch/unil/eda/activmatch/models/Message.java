package ch.unil.eda.activmatch.models;

public class Message {
    private String mGroupId;
    private String mText;
    private User mCreator;
    private String mDate;

    public Message(String groupId, String date, String text, User creator) {
        mGroupId = groupId;
        mDate = date;
        mText = text;
        mCreator = creator;
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
