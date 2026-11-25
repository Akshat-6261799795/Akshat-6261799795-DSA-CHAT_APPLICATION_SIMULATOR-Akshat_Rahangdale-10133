package edu.college.chat.service;

import edu.college.chat.dsa.MessageLinkedList;
import edu.college.chat.model.ChatMessage;
import edu.college.chat.model.User;

import java.util.*;

public class ChatService {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, MessageLinkedList> conversations = new HashMap<>();
    private final Queue<ChatMessage> outgoingQueue = new ArrayDeque<>();
    private final Stack<ChatMessage> undoStack = new Stack<>();
    private int nextMessageNumber = 101;

    public ChatService() {
        addUser(new User("U101", "Aman"));
        addUser(new User("U102", "Riya"));
        addUser(new User("U103", "Rahul"));
        addUser(new User("U104", "Priya"));
    }

    private void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUser(String id) {
        return users.get(id.trim().toUpperCase());
    }

    public void searchUser(String id) {
        User user = findUser(id);
        if (user == null) System.out.println("User " + id + " not found.");
        else System.out.println("User found: " + user);
    }

    public void sendMessage(String senderId, String receiverId, String text) {
        User sender = findUser(senderId);
        User receiver = findUser(receiverId);

        if (sender == null) {
            System.out.println("User " + senderId + " not found. Message not sent.");
            return;
        }
        if (receiver == null) {
            System.out.println("User " + receiverId + " not found. Message not sent.");
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            System.out.println("Message cannot be empty.");
            return;
        }

        String messageId = "M" + nextMessageNumber++;
        ChatMessage message = new ChatMessage(messageId, sender.getUserId(),
                receiver.getUserId(), text.trim());
        outgoingQueue.offer(message);
        undoStack.push(message);
        System.out.println("Message queued: " + messageId + " | "
                + sender.getName() + " -> " + receiver.getName());
    }

    public void deliverNext() {
        ChatMessage message = outgoingQueue.poll();
        if (message == null) {
            System.out.println("No messages to deliver.");
            return;
        }
        message.markDelivered();
        String key = conversationKey(message.getSenderId(), message.getReceiverId());
        conversations.computeIfAbsent(key, k -> new MessageLinkedList()).add(message);
        System.out.println("Message delivered: " + message.getMessageId());
        removeFromUndoStack(message);
    }

    private void removeFromUndoStack(ChatMessage delivered) {
        // Stack contains pending messages. Rebuild it while preserving stack order.
        Stack<ChatMessage> temporary = new Stack<>();
        while (!undoStack.empty()) {
            ChatMessage item = undoStack.pop();
            if (item != delivered) temporary.push(item);
        }
        while (!temporary.empty()) undoStack.push(temporary.pop());
    }

    public void undoLastMessage() {
        while (!undoStack.empty() && undoStack.peek().isDelivered()) {
            undoStack.pop();
        }
        if (undoStack.empty()) {
            System.out.println("No messages to undo.");
            return;
        }

        ChatMessage latest = undoStack.pop();
        Queue<ChatMessage> rebuilt = new ArrayDeque<>();
        boolean removed = false;
        while (!outgoingQueue.isEmpty()) {
            ChatMessage item = outgoingQueue.poll();
            if (!removed && item == latest) {
                removed = true;
            } else {
                rebuilt.offer(item);
            }
        }
        outgoingQueue.addAll(rebuilt);

        if (removed) {
            System.out.println("Last message removed from outgoing queue/history.");
        } else {
            System.out.println("No unsent message found to undo.");
        }
    }

    public void viewConversation(String firstId, String secondId) {
        User first = findUser(firstId);
        User second = findUser(secondId);
        if (first == null || second == null) {
            System.out.println("One or both users not found.");
            return;
        }
        MessageLinkedList history = conversations.get(conversationKey(
                first.getUserId(), second.getUserId()));
        if (history == null) {
            history = conversations.get(conversationKey(
                    second.getUserId(), first.getUserId()));
        }
        if (history == null) {
            System.out.println("No conversation history.");
        } else {
            history.display();
        }
    }

    public void showQueue() {
        if (outgoingQueue.isEmpty()) {
            System.out.println("Outgoing queue is empty.");
            return;
        }
        for (ChatMessage message : outgoingQueue) System.out.println(message);
    }

    public void loadMessage(ChatMessage message) {
        message.markDelivered();
        String key = conversationKey(message.getSenderId(), message.getReceiverId());
        conversations.computeIfAbsent(key, k -> new MessageLinkedList()).add(message);
        try {
            int number = Integer.parseInt(message.getMessageId().substring(1));
            nextMessageNumber = Math.max(nextMessageNumber, number + 1);
        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
            // Keep the default counter if a file record has a malformed ID.
        }
    }

    public void saveAllMessages(MessageFileManager fileManager) {
        fileManager.save(conversations);
    }

    private String conversationKey(String a, String b) {
        // Canonical key ensures both users see the same conversation.
        return a.compareTo(b) < 0 ? a + "_" + b : b + "_" + a;
    }
}
