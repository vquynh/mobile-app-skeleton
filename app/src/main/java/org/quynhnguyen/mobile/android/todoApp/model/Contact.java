package org.quynhnguyen.mobile.android.todoApp.model;

public class Contact {
    private long id;
    private String displayName;
    private String mobileNumber;
    private String emailAddress;
    private boolean canSendSms;
    private boolean canSendEmail;

    public Contact(long id, String displayName, String mobileNumber, String emailAddress) {
        this.id = id;
        this.displayName = displayName;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.canSendEmail = this.emailAddress != null && !this.emailAddress.isEmpty();
        this.canSendSms = this.mobileNumber != null && !this.mobileNumber.isEmpty();
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

    public boolean isCanSendSms() {
        return this.canSendSms;
    }

    public void setCanSendSms(boolean canSendSms) {
        this.canSendSms = canSendSms;
    }

    public boolean isCanSendEmail() {
        return this.canSendEmail;
    }

    public void setCanSendEmail(boolean canSendEmail) {
        this.canSendEmail = canSendEmail;
    }
}
