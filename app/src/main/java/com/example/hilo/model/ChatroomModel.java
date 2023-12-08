package com.example.hilo.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    private String chatroomId;
    private List<String> userIds;
    private Timestamp lastSentMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;
    private Boolean isRead;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastSentMessageTimestamp, String lastMessage, String lastSentMessageId, Boolean isRead) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastSentMessageTimestamp = lastSentMessageTimestamp;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastSentMessageId;
        this.isRead = isRead;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
