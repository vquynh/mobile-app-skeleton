package org.quynhnguyen.mobile.android.todoApp.model;

public class Contact {
    private long id;
    private String displayName;
    private String mobileNumber;
    private String emailAddress;

    public Contact(long id, String displayName, String mobileNumber, String emailAddress) {
        this.id = id;
        this.displayName = displayName;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
