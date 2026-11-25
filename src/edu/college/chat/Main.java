package edu.college.chat;

import edu.college.chat.service.ChatService;
import edu.college.chat.service.MessageFileManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ChatService chat = new ChatService();
        MessageFileManager fileManager = new MessageFileManager();
        fileManager.load(chat);

        try (Scanner scanner = new Scanner(System.in)) {
            int choice = -1;
            do {
                System.out.println("\n===== CHAT APPLICATION SIMULATOR =====");
                System.out.println("1. Send message");
                System.out.println("2. Deliver next message");
                System.out.println("3. View conversation");
                System.out.println("4. Search user");
                System.out.println("5. Undo latest unsent message");
                System.out.println("6. View outgoing queue");
                System.out.println("0. Save and exit");
                System.out.print("Enter choice: ");

                if (!scanner.hasNextInt()) {
                    scanner.nextLine();
                    System.out.println("Invalid choice. Enter a number.");
                    continue;
                }
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Sender ID: ");
                        String sender = scanner.nextLine();
                        System.out.print("Receiver ID: ");
                        String receiver = scanner.nextLine();
                        System.out.print("Message: ");
                        String message = scanner.nextLine();
                        chat.sendMessage(sender, receiver, message);
                        break;
                    case 2:
                        chat.deliverNext();
                        break;
                    case 3:
                        System.out.print("First user ID: ");
                        String first = scanner.nextLine();
                        System.out.print("Second user ID: ");
                        String second = scanner.nextLine();
                        chat.viewConversation(first, second);
                        break;
                    case 4:
                        System.out.print("Enter user ID: ");
                        chat.searchUser(scanner.nextLine());
                        break;
                    case 5:
                        chat.undoLastMessage();
                        break;
                    case 6:
                        chat.showQueue();
                        break;
                    case 0:
                        chat.saveAllMessages(fileManager);
                        System.out.println("Exiting Chat Application Simulator. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } while (choice != 0);
        }
    }
}
