package edu.college.chat.dsa;

import edu.college.chat.model.ChatMessage;

/** A simple singly linked list used to store one conversation's history. */
public class MessageLinkedList {
    private static class Node {
        ChatMessage data;
        Node next;
        Node(ChatMessage data) { this.data = data; }
    }

    private Node head;
    private Node tail;
    private int size;

    public void add(ChatMessage message) {
        Node node = new Node(message);
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public boolean isEmpty() { return head == null; }
    public int size() { return size; }

    public void display() {
        if (isEmpty()) {
            System.out.println("No conversation history.");
            return;
        }
        Node current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }

    public void forEachMessage(java.util.function.Consumer<ChatMessage> action) {
        Node current = head;
        while (current != null) {
            action.accept(current.data);
            current = current.next;
        }
    }
}
