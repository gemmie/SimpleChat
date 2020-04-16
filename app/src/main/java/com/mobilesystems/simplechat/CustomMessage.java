package com.mobilesystems.simplechat;

public class CustomMessage {
    private String text;
    private MemberData memberData;
    private boolean sentByCurrentUser;

    public CustomMessage(String text, MemberData memberData, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = memberData;
        this.sentByCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberData getMemberData() {
        return memberData;
    }

    public boolean isSentByCurrentUser() {
        return sentByCurrentUser;
    }


}
