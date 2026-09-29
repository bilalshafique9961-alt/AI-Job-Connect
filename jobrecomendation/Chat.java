package com.example.jobrecomendation;

public class Chat {
    private String id;
    private String seekerId;
    private String seekerName;
    private String employerId;
    private String lastMessage;
    private long lastMessageTime;

    public Chat() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSeekerId() { return seekerId; }
    public void setSeekerId(String seekerId) { this.seekerId = seekerId; }

    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }

    public String getEmployerId() { return employerId; }
    public void setEmployerId(String employerId) { this.employerId = employerId; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public long getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(long lastMessageTime) { this.lastMessageTime = lastMessageTime; }
}