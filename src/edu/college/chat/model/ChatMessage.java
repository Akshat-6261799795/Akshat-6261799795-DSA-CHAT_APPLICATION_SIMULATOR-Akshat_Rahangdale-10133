package edu.college.chat.model;

public class ChatMessage {
    private final String messageId;
    private final String senderId;
    private final String receiverId;
    private final String text;
    private boolean delivered;

    public ChatMessage(String messageId, String senderId, String receiverId, String text) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.delivered = false;
    }

    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public boolean isDelivered() { return delivered; }
    public void markDelivered() { delivered = true; }

    @Override
    public String toString() {
        return messageId + " | " + senderId + " -> " + receiverId + " | " + text;
    }
}
