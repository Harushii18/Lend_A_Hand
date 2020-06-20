package com.example.lendahand;

//this class is for the expandable card views for pending requests
public class PendingRequestItem {
    private String doneeName;
    private String motivationLetter;
    private boolean isShrink=true;
    private String status;
    private int checkedID;
    private String username;

    public PendingRequestItem() {
    }

    public PendingRequestItem(String doneeName, String motivationLetter, String status, String username) {
        this.doneeName = doneeName;
        this.motivationLetter = motivationLetter;
        this.status=status;
        this.username=username;
    }


    public int getCheckedID() {
        return checkedID;
    }

    public void setCheckedID(int checkedID) {
        this.checkedID = checkedID;
    }

    public String getPendingUsername() {
        return username;
    }

    public void setPendingUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDoneeName() {
        return doneeName;
    }

    public void setDoneeName(String doneeName) {
        this.doneeName = doneeName;
    }

    public String getMotivationLetter() {
        return motivationLetter;
    }

    public void setMotivationLetter(String motivationLetter) {
        this.motivationLetter = motivationLetter;
    }

    public boolean isShrink() {
        return isShrink;
    }

    public void setShrink(boolean shrink) {
        isShrink = shrink;
    }
}
