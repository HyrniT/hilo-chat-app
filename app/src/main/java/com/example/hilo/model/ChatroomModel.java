package com.example.hilo.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private Timestamp lastSentMessageTimestamp;
    private String lastMessageSenderId;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastSentMessageTimestamp, String lastSentMessageId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastSentMessageTimestamp = lastSentMessageTimestamp;
        this.lastMessageSenderId = lastSentMessageId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastSentMessageTimestamp() {
        return lastSentMessageTimestamp;
    }

    public void setLastSentMessageTimestamp(Timestamp lastSentMessageTimestamp) {
        this.lastSentMessageTimestamp = lastSentMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}