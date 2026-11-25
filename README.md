# PS No. 71 — Chat Application Simulator

**Language:** Java (JDK 17+ recommended)  
**Interface:** Console  
**DSA:** Linked List, Queue, Stack, HashMap  
**Persistence:** `chat_messages.txt`

## Project structure

```text
ChatApplicationSimulator/
├── src/
│   └── edu/college/chat/
│       ├── Main.java
│       ├── model/
│       │   ├── ChatMessage.java
│       │   └── User.java
│       ├── dsa/
│       │   └── MessageLinkedList.java
│       └── service/
│           ├── ChatService.java
│           └── MessageFileManager.java
├── chat_messages.txt
└── README.md
```

## Run in VS Code / Windows

1. Install a JDK (17 or newer recommended).
2. Open the **ChatApplicationSimulator** folder in VS Code.
3. Open Terminal → PowerShell.
4. Compile:

```powershell
javac -d out src\edu\college\chat\Main.java src\edu\college\chat\model\*.java src\edu\college\chat\dsa\*.java src\edu\college\chat\service\*.java
```

5. Run:

```powershell
java -cp out edu.college.chat.Main
```

No Maven is required.

## Default users

- U101 — Aman
- U102 — Riya
- U103 — Rahul
- U104 — Priya

User IDs are unique. Enter IDs when sending/searching messages.

## Menu features

1. Send message
2. Deliver next message
3. View conversation
4. Search user
5. Undo latest unsent message
6. View outgoing queue
0. Save and exit

## Test cases from PS No. 71

| Test | Input/action | Expected result |
|---|---|---|
| 1 | Aman sends `Hello` to Riya | `Message queued: M101 \| Aman -> Riya` |
| 2 | Deliver when queue is empty | `No messages to deliver.` |
| 3 | Send to `U999` | `User U999 not found. Message not sent.` |
| 4 | Send two messages, then undo | `Last message removed from outgoing queue/history.` |

Message IDs are generated sequentially. After loading saved messages, the next ID continues from the highest saved ID.

## Data file

`chat_messages.txt` is created in the project root when the program exits. Only delivered messages are saved; pending messages are not saved as delivered history. Each line uses tab-separated fields.

## DSA notes for viva

- **Queue:** `ArrayDeque` with `offer()` and `poll()` provides FIFO outgoing delivery.
- **Stack:** `Stack<ChatMessage>` tracks pending messages for undo. Delivered messages are removed from the pending stack when they reach its top; undo skips already delivered entries.
- **Linked List:** A custom singly linked list stores each conversation's delivered messages.
- **HashMap:** Maps unique user IDs to `User` objects for fast lookup (average O(1)).
- **File handling:** `BufferedReader` loads records; `BufferedWriter` saves delivered conversation history.

## Complexity (typical)

- User lookup: average O(1)
- Queue enqueue/dequeue: O(1)
- Add conversation message: O(1)
- View conversation: O(n)
- Undo pending message: O(n) queue rebuild in this beginner-friendly implementation
