package edu.college.chat.service;

import edu.college.chat.dsa.MessageLinkedList;
import edu.college.chat.model.ChatMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;

public class MessageFileManager {
    private final Path file = Paths.get("chat_messages.txt");

    public void load(ChatService service) {
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\t", 4);
                if (parts.length == 4) {
                    ChatMessage message = new ChatMessage(parts[0], parts[1],
                            parts[2], unescape(parts[3]));
                    service.loadMessage(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load chat history: " + e.getMessage());
        }
    }

    public void save(Map<String, MessageLinkedList> conversations) {
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (MessageLinkedList history : conversations.values()) {
                history.forEachMessage(message -> {
                    try {
                        writer.write(message.getMessageId() + "\t"
                                + message.getSenderId() + "\t"
                                + message.getReceiverId() + "\t"
                                + escape(message.getText()));
                        writer.newLine();
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
            System.out.println("Conversation history saved to chat_messages.txt");
        } catch (IOException | UncheckedIOException e) {
            System.out.println("Could not save chat history: " + e.getMessage());
        }
    }

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private String unescape(String text) {
        return text.replace("\\t", "\t").replace("\\n", "\n")
                .replace("\\r", "\r").replace("\\\\", "\\");
    }
}
