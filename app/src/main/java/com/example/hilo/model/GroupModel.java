package com.example.hilo.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class GroupModel {
    private String groupId;
    private String groupName;
    private String userCreatedId;
    private List<String> userIds;
    private Timestamp lastSentMessageTimestamp;
    private String lastMessageSenderId;
    private String lastMessage;

    public GroupModel() {
    }

    public GroupModel(String groupId, String groupName, String userCreatedId, List<String> userIds, Timestamp lastSentMessageTimestamp, String lastMessageSenderId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.userCreatedId = userCreatedId;
        this.userIds = userIds;
        this.lastSentMessageTimestamp = lastSentMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserCreatedId() {
        return userCreatedId;
    }

    public void setUserCreatedId(String userCreatedId) {
        this.userCreatedId = userCreatedId;
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
}
