package com.example.hilo.model;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String message;
    private String senderId;
    private String senderName;
    private Timestamp sentTimestamp;
    private String imageUrl;

    public MessageModel() {
    }

    public MessageModel(String message, String senderId, Timestamp sentTimestamp) {
        this.message = message;
        this.senderId = senderId;
        this.sentTimestamp = sentTimestamp;
    }

    public MessageModel(String message, String senderId, Timestamp sentTimestamp, String imageUrl) {
        this.message = message;
        this.senderId = senderId;
        this.sentTimestamp = sentTimestamp;
        this.imageUrl = imageUrl;
    }

    public MessageModel(String message, String senderId, String senderName, Timestamp sentTimestamp, String imageUrl) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.sentTimestamp = sentTimestamp;
        this.imageUrl = imageUrl;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Timestamp getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(Timestamp sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
