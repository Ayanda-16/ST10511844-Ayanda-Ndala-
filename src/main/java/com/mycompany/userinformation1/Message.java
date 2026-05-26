/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.userinformation1;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author anomp
 */
public class Message {
          // Message properties
    public String messageID;
    public String recipient;
    public String content;
    public String messageHash;
    public int messageNumber;
    public long timestamp;
    
    // Static counters and storage
    public static int totalMessagesSent = 0;
    public static List<Message> allMessages = new ArrayList<>();
    
    // Constants
    public static final int MAX_MESSAGE_LENGTH = 250;
    
    /**
     * Constructor for creating a new message
     * @param recipient The recipient's phone number
     * @param content The message content
     */
    public Message(String recipient, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot send empty message!");
        }
        
        if (content.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message exceeds " + MAX_MESSAGE_LENGTH + " characters! " +
                                               "Your message has " + content.length() + " characters.");
        }
        
        this.recipient = recipient;
        this.content = content;
        this.messageID = generateMessageID();
        this.messageNumber = totalMessagesSent + 1;
        this.timestamp = System.currentTimeMillis();
        this.messageHash = calculateMessageHash();
        
        totalMessagesSent++;
        allMessages.add(this);
    }
    
    /**
     * Private constructor for loading existing messages from storage
     */
    private Message(String messageID, String recipient, String content, 
                   String messageHash, int messageNumber, long timestamp) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.content = content;
        this.messageHash = messageHash;
        this.messageNumber = messageNumber;
        this.timestamp = timestamp;
    }
    
    /**
     * Generate a unique message ID
     * @return Unique message ID string
     */
    public String generateMessageID() {
        return "MSG" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    /**
     * Calculate message hash for integrity verification
     * @return Message hash string
     */
    public String calculateMessageHash() {
        // Extract first two numbers from messageID
        String firstTwoNumbers = "";
        int numbersFound = 0;
        
        for (int i = 0; i < messageID.length() && numbersFound < 2; i++) {
            char c = messageID.charAt(i);
            if (c >= '0' && c <= '9') {
                firstTwoNumbers += c;
                numbersFound++;
            }
        }
        
        // Pad with zeros if needed
        while (firstTwoNumbers.length() < 2) {
            firstTwoNumbers = "0" + firstTwoNumbers;
        }
        
        // Get first and last word from content
        String[] words = content.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        // Clean words (remove leading/trailing punctuation)
        firstWord = firstWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        lastWord = lastWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord;
        return hash.toUpperCase();
    }
    
    /**
     * Verify message integrity using hash
     * @return true if message hash is valid
     */
    public boolean verifyIntegrity() {
        String calculatedHash = calculateMessageHash();
        return calculatedHash.equals(this.messageHash);
    }
    
    /**
     * Display message details
     */
    public void displayMessage() {
        System.out.println("Message #" + messageNumber);
        System.out.println("Message ID: " + messageID);
        System.out.println("Recipient: " + recipient);
        System.out.println("Content: " + content);
        System.out.println("Hash: " + messageHash);
        System.out.println("Length: " + content.length() + "/" + MAX_MESSAGE_LENGTH);
        System.out.println("Timestamp: " + new java.util.Date(timestamp));
        System.out.println("---------------------------");
    }
    
    /**
     * Get message preview (first 30 characters)
     * @return Preview string
     */
    public String getPreview() {
        return content.length() > 30 ? content.substring(0, 30) + "..." : content;
    }
    
    // Static methods for managing messages
    
    /**
     * Get all messages
     * @return List of all messages
     */
    public static List<Message> getAllMessages() {
        return new ArrayList<>(allMessages);
    }
    
    /**
     * Get total number of messages sent
     * @return Total message count
     */
    public static int getTotalMessagesSent() {
        return totalMessagesSent;
    }
    
    /**
     * Delete a message by index
     * @param index The index of the message to delete
     * @return The deleted message, or null if index is invalid
     */
    public static Message deleteMessage(int index) {
        if (index >= 0 && index < allMessages.size()) {
            Message deleted = allMessages.remove(index);
            totalMessagesSent--;
            return deleted;
        }
        return null;
    }
    
    /**
     * Delete a message by ID
     * @param messageID The ID of the message to delete
     * @return The deleted message, or null if not found
     */
    public static Message deleteMessageByID(String messageID) {
        for (int i = 0; i < allMessages.size(); i++) {
            if (allMessages.get(i).getMessageID().equals(messageID)) {
                return deleteMessage(i);
            }
        }
        return null;
    }
    
    /**
     * Find message by ID
     * @param messageID The message ID to search for
     * @return The message or null if not found
     */
    public static Message findMessageByID(String messageID) {
        for (Message msg : allMessages) {
            if (msg.getMessageID().equals(messageID)) {
                return msg;
            }
        }
        return null;
    }
    
    /**
     * Load messages from a data structure (for JSON loading)
     * @param messagesData List of message data arrays
     */
    public static void loadMessages(List<Object[]> messagesData) {
        allMessages.clear();
        totalMessagesSent = 0;
        
        for (Object[] data : messagesData) {
            Message msg = new Message(
                (String) data[0], // messageID
                (String) data[1], // recipient
                (String) data[2], // content
                (String) data[3], // messageHash
                (Integer) data[4], // messageNumber
                (Long) data[5]    // timestamp
            );
            allMessages.add(msg);
            totalMessagesSent = Math.max(totalMessagesSent, msg.getMessageNumber());
        }
    }
    
    /**
     * Export messages to a serializable format for JSON storage
     * @return List of message data arrays
     */
    public static List<Object[]> exportMessages() {
        List<Object[]> exportData = new ArrayList<>();
        for (Message msg : allMessages) {
            exportData.add(new Object[]{
                msg.messageID,
                msg.recipient,
                msg.content,
                msg.messageHash,
                msg.messageNumber,
                msg.timestamp
            });
        }
        return exportData;
    }
    
    /**
     * Clear all messages (useful when switching users)
     */
    public static void clearAllMessages() {
        allMessages.clear();
        totalMessagesSent = 0;
    }
    
    // Getters
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public String getMessageHash() { return messageHash; }
    public int getMessageNumber() { return messageNumber; }
    public long getTimestamp() { return timestamp; }
    public int getContentLength() { return content.length(); }
    public int getRemainingCharacters() { return MAX_MESSAGE_LENGTH - content.length(); }
    
    // Setters (with validation)
    public void setContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot set empty message!");
        }
        if (newContent.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message exceeds " + MAX_MESSAGE_LENGTH + " characters!");
        }
        this.content = newContent;
        this.messageHash = calculateMessageHash(); // Recalculate hash when content changes
    }
    
    public void setRecipient(String newRecipient) {
        if (newRecipient == null || newRecipient.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient cannot be empty!");
        }
        this.recipient = newRecipient;
    }
    
    // ========== NEW TEST METHODS ==========
    
    /**
     * Test method to create sample messages
     * @param count Number of sample messages to create
     */
    public static void createSampleMessages(int count) {
        String[] sampleRecipients = {
            "+1234567890", "+1987654321", "+15551234567", 
            "+14445556666", "+17778889999"
        };
        
        String[] sampleContents = {
            "Hello, how are you?",
            "Meeting tomorrow at 3 PM",
            "Don't forget the documents!",
            "Thanks for your help!",
            "See you soon",
            "Please call me back",
            "I'll send the report by EOD",
            "Happy birthday!",
            "Let's catch up sometime",
            "Great job on the project!"
        };
        
        for (int i = 0; i < count; i++) {
            String recipient = sampleRecipients[i % sampleRecipients.length];
            String content = sampleContents[i % sampleContents.length] + " " + (i + 1);
            try {
                new Message(recipient, content);
                System.out.println("Created sample message #" + (i + 1));
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to create message: " + e.getMessage());
            }
        }
        System.out.println("Total sample messages created: " + count);
    }
    
    /**
     * Test method to verify all messages' integrity
     * @return Statistics about integrity check
     */
    public static IntegrityReport verifyAllMessagesIntegrity() {
        int validCount = 0;
        int invalidCount = 0;
        List<Message> invalidMessages = new ArrayList<>();
        
        for (Message msg : allMessages) {
            if (msg.verifyIntegrity()) {
                validCount++;
            } else {
                invalidCount++;
                invalidMessages.add(msg);
            }
        }
        
        return new IntegrityReport(validCount, invalidCount, invalidMessages);
    }
    
    /**
     * Search messages by recipient
     * @param recipient The recipient to search for
     * @return List of messages to the specified recipient
     */
    public static List<Message> searchByRecipient(String recipient) {
        List<Message> results = new ArrayList<>();
        if (recipient == null || recipient.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = recipient.trim().toLowerCase();
        for (Message msg : allMessages) {
            if (msg.getRecipient().toLowerCase().contains(searchTerm)) {
                results.add(msg);
            }
        }
        return results;
    }
    
    /**
     * Search messages by content keyword
     * @param keyword The keyword to search for
     * @return List of messages containing the keyword
     */
    public static List<Message> searchByKeyword(String keyword) {
        List<Message> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        
        String searchTerm = keyword.trim().toLowerCase();
        for (Message msg : allMessages) {
            if (msg.getContent().toLowerCase().contains(searchTerm)) {
                results.add(msg);
            }
        }
        return results;
    }
    
    /**
     * Get messages within a date range
     * @param startTime Start timestamp (inclusive)
     * @param endTime End timestamp (inclusive)
     * @return List of messages in the date range
     */
    public static List<Message> getMessagesByDateRange(long startTime, long endTime) {
        List<Message> results = new ArrayList<>();
        for (Message msg : allMessages) {
            if (msg.getTimestamp() >= startTime && msg.getTimestamp() <= endTime) {
                results.add(msg);
            }
        }
        return results;
    }
    
    /**
     * Get the most recent N messages
     * @param count Number of messages to retrieve
     * @return List of most recent messages
     */
    public static List<Message> getRecentMessages(int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        int startIndex = Math.max(0, allMessages.size() - count);
        return new ArrayList<>(allMessages.subList(startIndex, allMessages.size()));
    }
    
    /**
     * Display statistics about all messages
     */
    public static void displayStatistics() {
        if (allMessages.isEmpty()) {
            System.out.println("No messages to display statistics.");
            return;
        }
        
        int totalLength = 0;
        int shortestLength = Integer.MAX_VALUE;
        int longestLength = 0;
        Message shortestMessage = null;
        Message longestMessage = null;
        
        for (Message msg : allMessages) {
            int length = msg.getContentLength();
            totalLength += length;
            
            if (length < shortestLength) {
                shortestLength = length;
                shortestMessage = msg;
            }
            if (length > longestLength) {
                longestLength = length;
                longestMessage = msg;
            }
        }
        
        double averageLength = (double) totalLength / allMessages.size();
        
        System.out.println("\n=== MESSAGE STATISTICS ===");
        System.out.println("Total messages: " + allMessages.size());
        System.out.println("Average message length: " + String.format("%.2f", averageLength) + " characters");
        System.out.println("Shortest message (#" + shortestMessage.getMessageNumber() + "): " + shortestLength + " chars");
        System.out.println("Longest message (#" + longestMessage.getMessageNumber() + "): " + longestLength + " chars");
        System.out.println("Total characters sent: " + totalLength);
        System.out.println("Max capacity per message: " + MAX_MESSAGE_LENGTH);
        System.out.println("===========================\n");
    }
    
    /**
     * Interactive test console
     */
    public static void runTestConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        System.out.println("=== MESSAGE TEST CONSOLE ===");
        System.out.println("Type 'help' for commands\n");
        
        while (running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            switch (command) {
                case "help":
                    printHelp();
                    break;
                    
                case "create":
                    System.out.print("Recipient: ");
                    String recipient = scanner.nextLine();
                    System.out.print("Content: ");
                    String content = scanner.nextLine();
                    try {
                        Message msg = new Message(recipient, content);
                        System.out.println("Message created! ID: " + msg.getMessageID());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                    
                case "samples":
                    System.out.print("How many sample messages? ");
                    int count = Integer.parseInt(scanner.nextLine());
                    createSampleMessages(count);
                    break;
                    
                case "list":
                    displayAllMessages();
                    break;
                    
                case "verify":
                    IntegrityReport report = verifyAllMessagesIntegrity();
                    System.out.println("Integrity Check Results:");
                    System.out.println("  Valid messages: " + report.validCount);
                    System.out.println("  Invalid messages: " + report.invalidCount);
                    if (report.invalidCount > 0) {
                        System.out.println("  Invalid message IDs:");
                        for (Message msg : report.invalidMessages) {
                            System.out.println("    - " + msg.getMessageID());
                        }
                    }
                    break;
                    
                case "search":
                    System.out.print("Search by (recipient/content): ");
                    String searchType = scanner.nextLine().toLowerCase();
                    System.out.print("Search term: ");
                    String term = scanner.nextLine();
                    List<Message> results;
                    if (searchType.equals("recipient")) {
                        results = searchByRecipient(term);
                    } else {
                        results = searchByKeyword(term);
                    }
                    System.out.println("Found " + results.size() + " messages:");
                    for (Message msg : results) {
                        System.out.println("  #" + msg.getMessageNumber() + ": " + msg.getPreview());
                    }
                    break;
                    
                case "stats":
                    displayStatistics();
                    break;
                    
                case "delete":
                    System.out.print("Delete by (id/index): ");
                    String deleteType = scanner.nextLine().toLowerCase();
                    if (deleteType.equals("id")) {
                        System.out.print("Message ID: ");
                        String id = scanner.nextLine();
                        Message deleted = deleteMessageByID(id);
                        System.out.println(deleted != null ? "Message deleted" : "Message not found");
                    } else {
                        System.out.print("Index (0-" + (allMessages.size() - 1) + "): ");
                        int index = Integer.parseInt(scanner.nextLine());
                        Message deleted = deleteMessage(index);
                        System.out.println(deleted != null ? "Message deleted" : "Invalid index");
                    }
                    break;
                    
                case "clear":
                    clearAllMessages();
                    System.out.println("All messages cleared");
                    break;
                    
                case "quit":
                case "exit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                    
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
        scanner.close();
    }
    
    private static void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  help     - Show this help");
        System.out.println("  create   - Create a new message");
        System.out.println("  samples  - Create sample messages");
        System.out.println("  list     - List all messages");
        System.out.println("  verify   - Verify message integrity");
        System.out.println("  search   - Search messages");
        System.out.println("  stats    - Display statistics");
        System.out.println("  delete   - Delete a message");
        System.out.println("  clear    - Clear all messages");
        System.out.println("  quit     - Exit the test console\n");
    }
    
    /**
     * Display all messages in a formatted way
     */
    public static void displayAllMessages() {
        if (allMessages.isEmpty()) {
            System.out.println("No messages to display.");
            return;
        }
        
        System.out.println("\n=== ALL MESSAGES (" + allMessages.size() + ") ===");
        for (Message msg : allMessages) {
            System.out.println(msg);
        }
        System.out.println("================================\n");
    }
    
    // Inner class for integrity report
    public static class IntegrityReport {
        public final int validCount;
        public final int invalidCount;
        public final List<Message> invalidMessages;
        
        public IntegrityReport(int validCount, int invalidCount, List<Message> invalidMessages) {
            this.validCount = validCount;
            this.invalidCount = invalidCount;
            this.invalidMessages = invalidMessages;
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        System.out.println("Starting Message Class Tests...\n");
        
        // Test 1: Create sample messages
        System.out.println("TEST 1: Creating sample messages");
        createSampleMessages(5);
        System.out.println();
        
        // Test 2: Display all messages
        System.out.println("TEST 2: Displaying all messages");
        displayAllMessages();
        
        // Test 3: Verify integrity
        System.out.println("TEST 3: Verifying message integrity");
        IntegrityReport report = verifyAllMessagesIntegrity();
        System.out.println("Valid messages: " + report.validCount);
        System.out.println("Invalid messages: " + report.invalidCount + "\n");
        
        // Test 4: Search functionality
        System.out.println("TEST 4: Searching messages");
        List<Message> searchResults = searchByKeyword("hello");
        System.out.println("Messages containing 'hello': " + searchResults.size());
        searchResults = searchByRecipient("123");
        System.out.println("Messages to recipients containing '123': " + searchResults.size() + "\n");
        
        // Test 5: Statistics
        System.out.println("TEST 5: Displaying statistics");
        displayStatistics();
        
        // Test 6: Test edge cases
        System.out.println("TEST 6: Testing edge cases");
        try {
            new Message("+1234567890", ""); // Empty message
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught empty message: " + e.getMessage());
        }
        
        try {
            String longMessage = "A".repeat(MAX_MESSAGE_LENGTH + 1);
            new Message("+1234567890", longMessage); // Too long message
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Caught too long message: " + e.getMessage());
        }
        System.out.println();
        
        // Test 7: Delete and modify
        System.out.println("TEST 7: Deleting a message");
        if (!allMessages.isEmpty()) {
            Message deleted = deleteMessage(0);
            System.out.println("Deleted message #" + deleted.getMessageNumber());
            System.out.println("Remaining messages: " + allMessages.size());
        }
        System.out.println();
        
        // Test 8: Interactive test mode (optional)
        System.out.println("TEST 8: Interactive test mode");
        System.out.println("Do you want to run the interactive test console? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes") || response.equals("y")) {
            runTestConsole();
        }
        
        System.out.println("\nAll tests completed!");
    }
    
    @Override
    public String toString() {
        return String.format("Message{id='%s', to='%s', content='%s', hash='%s', #%d}",
                           messageID, recipient, getPreview(), messageHash, messageNumber);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageID.equals(message.messageID);
    }
    
    @Override
    public int hashCode() {
        return messageID.hashCode();
    }
}
