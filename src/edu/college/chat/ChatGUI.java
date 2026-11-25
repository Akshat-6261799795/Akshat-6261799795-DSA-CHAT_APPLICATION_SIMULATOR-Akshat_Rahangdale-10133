
package edu.college.chat;

import edu.college.chat.service.ChatService;
import edu.college.chat.service.MessageFileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ChatGUI extends JFrame {

    private final ChatService chat = new ChatService();
    private final MessageFileManager fileManager =
            new MessageFileManager();

    private final JComboBox<String> sender = new JComboBox<>(
            new String[]{"U101 - Aman", "U102 - Riya",
                    "U103 - Rahul", "U104 - Priya"});

    private final JComboBox<String> receiver = new JComboBox<>(
            new String[]{"U102 - Riya", "U101 - Aman",
                    "U103 - Rahul", "U104 - Priya"});

    private final JTextField messageField = new JTextField();
    private final JTextArea history = new JTextArea();
    private final JTextArea status = new JTextArea(4, 20);

    public ChatGUI() {
        fileManager.load(chat);

        setTitle("Chat Application Simulator - PS 71");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel(
                "CHAT APPLICATION SIMULATOR",
                SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 5, 10, 5));
        add(heading, BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        // User selection
        JPanel users = new JPanel(new GridLayout(2, 2, 10, 10));
        users.add(new JLabel("Sender:"));
        users.add(sender);
        users.add(new JLabel("Receiver:"));
        users.add(receiver);

        // Conversation
        history.setEditable(false);
        history.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane historyScroll = new JScrollPane(history);
        historyScroll.setBorder(
                BorderFactory.createTitledBorder("Conversation History"));

        // Message input
        JPanel input = new JPanel(new BorderLayout(8, 8));
        input.add(messageField, BorderLayout.CENTER);

        JButton send = new JButton("Send Message");
        input.add(send, BorderLayout.EAST);

        // DSA buttons
        JPanel buttons = new JPanel(new GridLayout(2, 3, 8, 8));

        JButton deliver = new JButton("Deliver Next");
        JButton undo = new JButton("Undo Message");
        JButton queue = new JButton("View Queue");
        JButton search = new JButton("Search User");
        JButton view = new JButton("View Conversation");
        JButton save = new JButton("Save History");

        buttons.add(deliver);
        buttons.add(undo);
        buttons.add(queue);
        buttons.add(search);
        buttons.add(view);
        buttons.add(save);

        // Status
        status.setEditable(false);
        status.setLineWrap(true);
        status.setWrapStyleWord(true);

        JScrollPane statusScroll = new JScrollPane(status);
        statusScroll.setBorder(
                BorderFactory.createTitledBorder("Application Status"));

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(input, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.CENTER);
        bottom.add(statusScroll, BorderLayout.SOUTH);

        main.add(users, BorderLayout.NORTH);
        main.add(historyScroll, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);

        // Send
        send.addActionListener(e -> {
            String message = messageField.getText().trim();

            if (message.isEmpty()) {
                showStatus("Please enter a message.");
                return;
            }

            String result = capture(() ->
                    chat.sendMessage(
                            getId(sender), getId(receiver), message));

            showStatus(result);
            messageField.setText("");
        });

        // Deliver
        deliver.addActionListener(e -> {
            showStatus(capture(chat::deliverNext));
            refreshHistory();
        });

        // Undo
        undo.addActionListener(e ->
                showStatus(capture(chat::undoLastMessage)));

        // Queue
        queue.addActionListener(e ->
                showStatus(capture(chat::showQueue)));

        // Search
        search.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this, "Enter User ID:");

            if (id != null && !id.trim().isEmpty()) {
                showStatus(capture(() -> chat.searchUser(id)));
            }
        });

        // View conversation
        view.addActionListener(e -> refreshHistory());

        // Save
        save.addActionListener(e -> saveHistory());

        // Save automatically when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveHistory();
                dispose();
            }
        });

        setVisible(true);
        refreshHistory();
    }

    private String getId(JComboBox<String> combo) {
        return combo.getSelectedItem().toString().split(" - ")[0];
    }

    private String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (PrintStream temporary = new PrintStream(buffer)) {
            System.setOut(temporary);
            action.run();
        } finally {
            System.setOut(original);
        }

        return buffer.toString().trim();
    }

    private void showStatus(String text) {
        status.setText(text.isEmpty()
                ? "Operation completed." : text);
    }

    private void refreshHistory() {
        String result = capture(() ->
                chat.viewConversation(getId(sender), getId(receiver)));

        history.setText(result);
    }

    private void saveHistory() {
        chat.saveAllMessages(fileManager);
        showStatus("Conversation history saved.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatGUI::new);
    }
}
