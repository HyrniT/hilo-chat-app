package com.example.hilo.model;

import com.google.firebase.Timestamp;


public class ChatroomAiModel {
    private String chatroomId;
    private String chatroomName;

    private Timestamp lastSentMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;

    public ChatroomAiModel() {
    }

    public ChatroomAiModel(String chatroomId, String chatroomName, Timestamp lastSentMessageTimestamp, String lastSentMessageId) {
        this.chatroomId = chatroomId;
        this.chatroomName = chatroomName;
        this.lastSentMessageTimestamp = lastSentMessageTimestamp;
        this.lastMessageSenderId = lastSentMessageId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public String getChatroomName() {
        return chatroomName;
    }

    public void setChatroomName(String chatroomName) {
        this.chatroomName = chatroomName;
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
}
