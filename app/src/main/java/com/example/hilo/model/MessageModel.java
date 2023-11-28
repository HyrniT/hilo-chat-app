package com.example.hilo.model;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String messageId;
    private String message;
    private String senderId;
    private String senderName;
    private Timestamp sentTimestamp;
    private String imageUrl;
    private Boolean isDeleted = false;
    private Boolean isPinned = false;

    public MessageModel() {
    }

    public MessageModel(String senderId, Timestamp sentTimestamp) {
        this(null, senderId, null, sentTimestamp, null);
    }

    public MessageModel(String message, String senderId, Timestamp sentTimestamp) {
        this(message, senderId, null, sentTimestamp, null);
    }

    public MessageModel(String senderId, Timestamp sentTimestamp, String imageUrl) {
        this(null, senderId, null, sentTimestamp, imageUrl);
    }

    public MessageModel(String message, String senderId, String senderName, Timestamp sentTimestamp) {
        this(message, senderId, senderName, sentTimestamp, null);
    }

    public MessageModel(String senderId, String senderName, Timestamp sentTimestamp, String imageUrl) {
        this(null, senderId, senderName, sentTimestamp, imageUrl);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private MessageModel(String message, String senderId, String senderName, Timestamp sentTimestamp, String imageUrl) {
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getPinned() {
        return isPinned;
    }

    public void setPinned(Boolean pinned) {
        isPinned = pinned;
    }
}
