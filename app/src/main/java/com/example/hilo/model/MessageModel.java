package com.example.hilo.model;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String message;
    private String senderId;
    private Timestamp sentTimestamp;

    public MessageModel() {
    }

    public MessageModel(String message, String senderId, Timestamp sentTimestamp) {
        this.message = message;
        this.senderId = senderId;
        this.sentTimestamp = sentTimestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(Timestamp sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }
}
