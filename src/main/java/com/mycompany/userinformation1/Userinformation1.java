/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.userinformation1;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Files;



/**
 *
 * @author anomp
 */
public class Userinformation1 {
// Store data in memory according to requirements
    static ArrayList<String> serialMessages = new ArrayList<>();      // All messages sent
    static ArrayList<String> dispatchedMessages = new ArrayList<>(); // Messages that were dispatched
    static ArrayList<String> storedMessages = new ArrayList<>();     // Messages that have been stored
    static ArrayList<String> messageHash = new ArrayList<>();        // All message hashes
    static ArrayList<String> messageID = new ArrayList<>();          // All message IDs
    static ArrayList<String> recipients = new ArrayList<>();         // Recipients for messages
    static ArrayList<String> messageFlags = new ArrayList<>();       // Status: Sent, Stored, Disregard
    
    static int totalMessagesSent = 0;
    static String loggedInUser = "";
    static String loggedInUsername = "";
    static String currentUserFirstName = "";
    static String currentUserLastName = "";
    static String currentUserPhone = "";
    
    private static final String DATA_FILE = "userdata.json";

    public static void main(String[] args) {
        loadDataFromJSON();
        
        Scanner input = new Scanner(System.in);

        // User registration
        System.out.print("Enter your first name: ");
        String fullName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        System.out.print("Enter username (<=5 characters and must contain _): ");
        String username = input.nextLine();
        boolean isValidUsername = checkUsername(username);
        if (!isValidUsername) {
            System.out.println("Username unsuccessful: must be <=5 characters AND contain '_'");
            return;
        }
        System.out.println("Username successfully captured");

        System.out.print("Enter password (>=8 characters and must contain special character): ");
        String password = input.nextLine();
        boolean isValidPassword = checkPassword(password);
        if (!isValidPassword) {
            System.out.println("Password unsuccessful: must be >=8 characters AND contain at least one special character (!@#$%^&*()_+ etc.)");
            return;
        }
        System.out.println("Password successfully captured");

        System.out.print("Enter phone number (+27...): ");
        String phone = input.nextLine();
        boolean isValidPhone = checkPhone(phone);
        if (!isValidPhone) {
            System.out.println("Phone number unsuccessful: must start with +27");
            return;
        }
        System.out.println("Phone number successfully captured");

        // Login
        System.out.println("------------- LOGIN -------------");
        System.out.print("Enter username: ");
        String loginName = input.nextLine();
        System.out.print("Enter password: ");
        String loginPass = input.nextLine();

        if (loginName.equals(username) && loginPass.equals(password)) {
            System.out.println("Login successful, welcome back " + fullName + " " + lastName + "!");
            System.out.println("Your phone number is: " + hidePhone(phone));
            loggedInUser = fullName + " " + lastName;
            loggedInUsername = username;
            currentUserFirstName = fullName;
            currentUserLastName = lastName;
            currentUserPhone = phone;
            
            loadUserMessages(username);
            populateWithTestData(); // Load test data for unit tests
            showWelcomeScreen();
            
        } else {
            System.out.println("Login failed, please try again.");
        }
        
        saveDataToJSON();
    }
    
    // Populate arrays with test data from requirements
    public static void populateWithTestData() {
        if (serialMessages.isEmpty()) {
            // Test Data Message 1
            addMessage("+27834557896", "Did you get the cake?", "Sent");
            // Test Data Message 2
            addMessage("+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored");
            // Test Data Message 3
            addMessage("+27834484567", "Yohoooo, I am at your gate.", "Disregard");
            // Test Data Message 4
            addMessage("08388884567", "It is dinner time!", "Sent");
            // Test Data Message 5
            addMessage("+27838884567", "Ok, I am leaving without you.", "Stored");
        }
    }
    
    // Add a new message to all arrays
    public static void addMessage(String recipient, String content, String flag) {
        String msgID = generateMessageID();
        String hash = calculateMessageHash(msgID, totalMessagesSent + 1, content);
        
        serialMessages.add(content);
        messageID.add(msgID);
        recipients.add(recipient);
        messageHash.add(hash);
        messageFlags.add(flag);
        
        if (flag.equals("Dispatched")) {
            dispatchedMessages.add(content);
        } else if (flag.equals("Stored")) {
            storedMessages.add(content);
        }
        
        totalMessagesSent++;
    }
    
    public static void loadUserMessages(String username) {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) return;
            
            String content = new String(Files.readAllBytes(file.toPath()));
            JSONObject jsonData = new JSONObject(content);
            JSONArray usersArray = jsonData.getJSONArray("users");
            
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("username").equals(username)) {
                    clearAllArrays();
                    totalMessagesSent = user.getInt("totalMessagesSent");
                    JSONArray userMessages = user.getJSONArray("messages");
                    
                    for (int j = 0; j < userMessages.length(); j++) {
                        JSONObject msg = userMessages.getJSONObject(j);
                        serialMessages.add((String) msg.getString("content"));
                        messageID.add((String) msg.getString("messageID"));
                        recipients.add((String) msg.getString("recipient"));
                        messageHash.add((String) msg.getString("messageHash"));
                        messageFlags.add((String) msg.getString("flag"));
                        
                        if (msg.getString("flag").equals("Stored")) {
                            storedMessages.add((String) msg.getString("content"));
                        } else if (msg.getString("flag").equals("Dispatched")) {
                            dispatchedMessages.add((String) msg.getString("content"));
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Note: No existing data found. Starting fresh.");
        }
    }
    
    public static void clearAllArrays() {
        serialMessages.clear();
        dispatchedMessages.clear();
        storedMessages.clear();
        messageHash.clear();
        messageID.clear();
        recipients.clear();
        messageFlags.clear();
    }
    
    public static void loadDataFromJSON() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) return;
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    public static void saveDataToJSON() {
        try {
            JSONObject jsonData = new JSONObject();
            JSONArray usersArray = new JSONArray();
            
            JSONObject currentUser = new JSONObject();
            currentUser.put("firstName", currentUserFirstName);
            currentUser.put("lastName", currentUserLastName);
            currentUser.put("username", loggedInUsername);
            currentUser.put("phoneNumber", currentUserPhone);
            currentUser.put("totalMessagesSent", totalMessagesSent);
            
            JSONArray userMessages = new JSONArray();
            for (int i = 0; i < serialMessages.size(); i++) {
                JSONObject msg = new JSONObject();
                msg.put("messageID", messageID.get(i));
                msg.put("recipient", recipients.get(i));
                msg.put("content", serialMessages.get(i));
                msg.put("messageHash", messageHash.get(i));
                msg.put("flag", messageFlags.get(i));
                userMessages.put(msg);
            }
            currentUser.put("messages", userMessages);
            
            File file = new File(DATA_FILE);
            boolean userExists = false;
            
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONObject existingData = new JSONObject(content);
                JSONArray existingUsers = existingData.getJSONArray("users");
                
                for (int i = 0; i < existingUsers.length(); i++) {
                    JSONObject user = existingUsers.getJSONObject(i);
                    if (user.getString("username").equals(loggedInUsername)) {
                        existingUsers.put(i, currentUser);
                        userExists = true;
                        break;
                    }
                }
                
                if (!userExists) {
                    existingUsers.put(currentUser);
                }
                jsonData.put("users", existingUsers);
            } else {
                usersArray.put(currentUser);
                jsonData.put("users", usersArray);
            }
            
            try (FileWriter fileWriter = new FileWriter(DATA_FILE)) {
                fileWriter.write(jsonData.toString(4));
            }
            
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    public static void showWelcomeScreen() {
        System.out.println("\n=====================================");
        System.out.println("     WELCOME TO QUICKCHAT");
        System.out.println("=====================================");
        System.out.println("Hello " + loggedInUser + "!");
        showMainMenu();
    }
    
    public static void showMainMenu() {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send New Message");
            System.out.println("2. View All Messages");
            System.out.println("3. Delete Message");
            System.out.println("4. Stored Messages Management");
            System.out.println("5. Run Unit Tests");
            System.out.println("6. Exit");
            System.out.println("===============================");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            input.nextLine();
            
            switch(choice) {
                case 1:
                    sendMessage();
                    break;
                case 2:
                    viewMessages();
                    break;
                case 3:
                    deleteMessage();
                    break;
                case 4:
                    storedMessagesMenu();
                    break;
                case 5:
                    runUnitTests();
                    break;
                case 6:
                    saveDataToJSON();
                    System.out.println("Thank you for using QuickChat! Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while(choice != 6);
    }
    
    // Stored Messages Management Menu
    public static void storedMessagesMenu() {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n====== STORED MESSAGES MENU ======");
            System.out.println("1. Display sender and recipient of all stored messages");
            System.out.println("2. Display the longest stored message");
            System.out.println("3. Search for a message ID and display recipient and message");
            System.out.println("4. Search all messages for a particular recipient");
            System.out.println("5. Delete a message using message hash");
            System.out.println("6. Display full report of all stored messages");
            System.out.println("7. Return to Main Menu");
            System.out.println("===================================");
            System.out.print("Enter your choice: ");
            choice = input.nextInt();
            input.nextLine();
            
            switch(choice) {
                case 1:
                    displaySenderAndRecipient();
                    break;
                case 2:
                    displayLongestStoredMessage();
                    break;
                case 3:
                    searchMessageByID();
                    break;
                case 4:
                    searchMessagesByRecipient();
                    break;
                case 5:
                    deleteMessageByHash();
                    break;
                case 6:
                    displayFullReport();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        } while(choice != 7);
    }
    
    // 2a: Display sender and recipient of all stored messages
    public static void displaySenderAndRecipient() {
        System.out.println("\n===== STORED MESSAGES - SENDER & RECIPIENT =====");
        boolean found = false;
        
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                System.out.println("Sender: " + currentUserFirstName + " " + currentUserLastName);
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("---------------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No stored messages found.");
        }
    }
    
    // 2b: Display the longest stored message
    public static void displayLongestStoredMessage() {
        System.out.println("\n===== LONGEST STORED MESSAGE =====");
        String longestMessage = "";
        int longestIndex = -1;
        
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                if (serialMessages.get(i).length() > longestMessage.length()) {
                    longestMessage = serialMessages.get(i);
                    longestIndex = i;
                }
            }
        }
        
        if (longestIndex != -1) {
            System.out.println("Message: " + longestMessage);
            System.out.println("Length: " + longestMessage.length() + " characters");
            System.out.println("Recipient: " + recipients.get(longestIndex));
            System.out.println("Message ID: " + messageID.get(longestIndex));
        } else {
            System.out.println("No stored messages found.");
        }
    }
    
    // 2c: Search for a message ID and display recipient and message
    public static void searchMessageByID() {
        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter Message ID to search: ");
        String searchID = input.nextLine();
        
        boolean found = false;
        for (int i = 0; i < messageID.size(); i++) {
            if (messageID.get(i).equals(searchID)) {
                System.out.println("\n===== MESSAGE FOUND =====");
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Message: " + serialMessages.get(i));
                System.out.println("Message Hash: " + messageHash.get(i));
                System.out.println("Status: " + messageFlags.get(i));
                found = true;
                break;
            }
        }
        
        if (!found) {
            System.out.println("No message found with ID: " + searchID);
        }
    }
    
    // 2d: Search all messages for a particular recipient
    public static void searchMessagesByRecipient() {
        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter recipient phone number to search: ");
        String searchRecipient = input.nextLine();
        
        boolean found = false;
        System.out.println("\n===== MESSAGES FOR RECIPIENT " + searchRecipient + " =====");
        
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equals(searchRecipient)) {
                System.out.println("Message: " + serialMessages.get(i));
                System.out.println("Status: " + messageFlags.get(i));
                System.out.println("---------------------------");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No messages found for recipient: " + searchRecipient);
        }
    }
    
    // 2e: Delete a message using message hash
    public static void deleteMessageByHash() {
        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter Message Hash to delete: ");
        String searchHash = input.nextLine();
        
        boolean found = false;
        for (int i = 0; i < messageHash.size(); i++) {
            if (messageHash.get(i).equals(searchHash)) {
                System.out.println("Message: \"" + serialMessages.get(i) + "\" successfully deleted.");
                // Remove from all arrays
                serialMessages.remove(i);
                dispatchedMessages.remove(serialMessages.get(i));
                storedMessages.remove(serialMessages.get(i));
                messageHash.remove(i);
                messageID.remove(i);
                recipients.remove(i);
                messageFlags.remove(i);
                totalMessagesSent--;
                found = true;
                saveDataToJSON();
                break;
            }
        }
        
        if (!found) {
            System.out.println("No message found with hash: " + searchHash);
        }
    }
    
    // 2f: Display full report of all stored messages
    public static void displayFullReport() {
        System.out.println("\n===== FULL STORED MESSAGES REPORT =====");
        boolean found = false;
        
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                System.out.println("Message Hash: " + messageHash.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Message: " + serialMessages.get(i));
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("===================================");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No stored messages found.");
        }
    }
    
    public static void sendMessage() {
        Scanner input = new Scanner(System.in);
        
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = input.nextInt();
        input.nextLine();
        
        for(int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
            System.out.print("Enter recipient cell number: ");
            String recipient = input.nextLine();
            System.out.print("Enter your message (max 250 characters): ");
            String messageContent = input.nextLine();
            
            if(messageContent.length() > 250) {
                System.out.println("Message exceeds 250 characters! Message not sent.");
                continue;
            }
            
            System.out.print("Enter flag (Sent/Stored/Dispatched/Disregard): ");
            String flag = input.nextLine();
            
            String msgID = generateMessageID();
            String hash = calculateMessageHash(msgID, totalMessagesSent + 1, messageContent);
            
            serialMessages.add(messageContent);
            messageID.add(msgID);
            recipients.add(recipient);
            messageHash.add(hash);
            messageFlags.add(flag);
            
            if (flag.equals("Stored")) {
                storedMessages.add(messageContent);
            } else if (flag.equals("Dispatched")) {
                dispatchedMessages.add(messageContent);
            }
            
            totalMessagesSent++;
            
            System.out.println("\n✓ Message sent successfully!");
            System.out.println("Message ID: " + msgID);
            System.out.println("Message hash: " + hash);
            
            saveDataToJSON();
        }
        
        returnToMenu();
    }
    
    public static void viewMessages() {
        System.out.println("\n========== ALL MESSAGES ==========");
        System.out.println("Total messages stored: " + totalMessagesSent);
        System.out.println("===================================");
        
        if(serialMessages.isEmpty()) {
            System.out.println("No messages to display!");
        } else {
            for(int i = 0; i < serialMessages.size(); i++) {
                System.out.println("\nMessage #" + (i+1));
                System.out.println("Message ID: " + messageID.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Content: " + serialMessages.get(i));
                System.out.println("Hash: " + messageHash.get(i));
                System.out.println("Status: " + messageFlags.get(i));
                System.out.println("---------------------------");
            }
        }
        
        returnToMenu();
    }
    
    public static void deleteMessage() {
        Scanner input = new Scanner(System.in);
        
        if(serialMessages.isEmpty()) {
            System.out.println("No messages to delete!");
            returnToMenu();
            return;
        }
        
        for(int i = 0; i < serialMessages.size(); i++) {
            System.out.println((i+1) + ". ID: " + messageID.get(i) + " - To: " + recipients.get(i));
        }
        
        System.out.print("\nEnter message number to delete (or 0 to cancel): ");
        int deleteChoice = input.nextInt();
        
        if(deleteChoice > 0 && deleteChoice <= serialMessages.size()) {
            String removedMessage = serialMessages.get(deleteChoice - 1);
            serialMessages.remove(deleteChoice - 1);
            messageID.remove(deleteChoice - 1);
            recipients.remove(deleteChoice - 1);
            messageHash.remove(deleteChoice - 1);
            messageFlags.remove(deleteChoice - 1);
            totalMessagesSent--;
            
            System.out.println("\n✓ Message \"" + removedMessage + "\" successfully deleted.");
            saveDataToJSON();
        } else if (deleteChoice != 0) {
            System.out.println("Invalid choice!");
        }
        
        returnToMenu();
    }
    
    // UNIT TESTS
    public static void runUnitTests() {
        System.out.println("\n========== RUNNING UNIT TESTS ==========");
        
        // Test 1: Sent Messages array correctly populated
        testSentMessagesArray();
        
        // Test 2: Display longest message
        testLongestMessage();
        
        // Test 3: Search for message ID
        testSearchByMessageID();
        
        // Test 4: Search messages for particular recipient
        testSearchByRecipient();
        
        // Test 5: Delete message using hash
        testDeleteByHash();
        
        // Test 6: Display report
        testDisplayReport();
        
        System.out.println("\n========== UNIT TESTS COMPLETED ==========");
    }
    
    public static void testSentMessagesArray() {
        System.out.println("\n--- TEST 1: Sent Messages Array ---");
        ArrayList<String> sentMessages = new ArrayList<>();
        
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Sent")) {
                sentMessages.add(serialMessages.get(i));
            }
        }
        
        boolean containsTestData = false;
        for (String msg : sentMessages) {
            if (msg.equals("Did you get the cake?") || msg.equals("It is dinner time!")) {
                containsTestData = true;
                System.out.println("Found: \"" + msg + "\"");
            }
        }
        
        if (containsTestData) {
            System.out.println("✓ TEST PASSED: Sent messages array contains expected test data");
        } else {
            System.out.println("✗ TEST FAILED: Expected messages not found");
        }
    }
    
    public static void testLongestMessage() {
        System.out.println("\n--- TEST 2: Longest Message ---");
        String expectedLongest = "Where are you? You are late! I have asked you to be on time.";
        String longestMessage = "";
        
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                if (serialMessages.get(i).length() > longestMessage.length()) {
                    longestMessage = serialMessages.get(i);
                }
            }
        }
        
        if (longestMessage.equals(expectedLongest)) {
            System.out.println("✓ TEST PASSED: Longest message is \"" + longestMessage + "\"");
        } else {
            System.out.println("✗ TEST FAILED: Expected \"" + expectedLongest + "\" but got \"" + longestMessage + "\"");
        }
    }
    
    public static void testSearchByMessageID() {
        System.out.println("\n--- TEST 3: Search by Message ID ---");
        // Find message 4 (It is dinner time!)
        String targetMessage = "It is dinner time!";
        String expectedRecipient = "08388884567";
        String foundRecipient = "";
        String foundMessage = "";
        
        for (int i = 0; i < serialMessages.size(); i++) {
            if (serialMessages.get(i).equals(targetMessage)) {
                foundRecipient = recipients.get(i);
                foundMessage = serialMessages.get(i);
                break;
            }
        }
        
        if (foundRecipient.equals(expectedRecipient) && foundMessage.equals(targetMessage)) {
            System.out.println("✓ TEST PASSED: Found message \"" + foundMessage + "\" for recipient " + foundRecipient);
        } else {
            System.out.println("✗ TEST FAILED: Expected message not found correctly");
        }
    }
    
    public static void testSearchByRecipient() {
        System.out.println("\n--- TEST 4: Search by Recipient (+27838884567) ---");
        String targetRecipient = "+27838884567";
        ArrayList<String> messagesForRecipient = new ArrayList<>();
        String expectedMessage1 = "Where are you? You are late! I have asked you to be on time.";
        String expectedMessage2 = "Ok, I am leaving without you.";
        
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equals(targetRecipient)) {
                messagesForRecipient.add(serialMessages.get(i));
            }
        }
        
        boolean foundBoth = messagesForRecipient.contains(expectedMessage1) && 
                           messagesForRecipient.contains(expectedMessage2);
        
        if (foundBoth) {
            System.out.println("✓ TEST PASSED: Found both expected messages for recipient " + targetRecipient);
            for (String msg : messagesForRecipient) {
                System.out.println("  - \"" + msg + "\"");
            }
        } else {
            System.out.println("✗ TEST FAILED: Expected messages not found for recipient");
        }
    }
    
    public static void testDeleteByHash() {
        System.out.println("\n--- TEST 5: Delete by Message Hash ---");
        // Store original size
        int originalSize = serialMessages.size();
        
        // Find test message 2
        String targetMessage = "Where are you? You are late! I have asked you to be on time.";
        String targetHash = "";
        
        for (int i = 0; i < serialMessages.size(); i++) {
            if (serialMessages.get(i).equals(targetMessage)) {
                targetHash = messageHash.get(i);
                break;
            }
        }
        
        if (!targetHash.isEmpty()) {
            // Delete the message
            for (int i = 0; i < messageHash.size(); i++) {
                if (messageHash.get(i).equals(targetHash)) {
                    serialMessages.remove(i);
                    messageID.remove(i);
                    recipients.remove(i);
                    messageHash.remove(i);
                    messageFlags.remove(i);
                    totalMessagesSent--;
                    System.out.println("Message: \"" + targetMessage + "\" successfully deleted.");
                    break;
                }
            }
            
            if (serialMessages.size() == originalSize - 1) {
                System.out.println("✓ TEST PASSED: Message successfully deleted");
            } else {
                System.out.println("✗ TEST FAILED: Message was not deleted properly");
            }
        } else {
            System.out.println("✗ TEST FAILED: Target message not found for deletion test");
        }
    }
    
    public static void testDisplayReport() {
        System.out.println("\n--- TEST 6: Display Report ---");
        System.out.println("Displaying all stored messages (including Message Hash, Recipient, Message):");
        
        boolean hasStoredMessages = false;
        for (int i = 0; i < messageFlags.size(); i++) {
            if (messageFlags.get(i).equals("Stored")) {
                System.out.println("Message Hash: " + messageHash.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Message: " + serialMessages.get(i));
                System.out.println("---------------------------");
                hasStoredMessages = true;
            }
        }
        
        if (hasStoredMessages) {
            System.out.println("✓ TEST PASSED: Report displayed successfully");
        } else {
            System.out.println("⚠️ TEST WARNING: No stored messages to display in report");
        }
    }
    
    public static String generateMessageID() {
        return "MSG" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    public static String calculateMessageHash(String messageID, int messageNumber, String messageContent) {
        String firstTwoNumbers = "";
        int numbersFound = 0;
        
        for (int i = 0; i < messageID.length() && numbersFound < 2; i++) {
            char c = messageID.charAt(i);
            if (c >= '0' && c <= '9') {
                firstTwoNumbers += c;
                numbersFound++;
            }
        }
        
        if (firstTwoNumbers.length() < 2) {
            while (firstTwoNumbers.length() < 2) {
                firstTwoNumbers = "0" + firstTwoNumbers;
            }
        }
        
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        firstWord = firstWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        lastWord = lastWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        
        return (firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord).toUpperCase();
    }
    
    public static void returnToMenu() {
        Scanner input = new Scanner(System.in);
        System.out.println("\nPress Enter to return to main menu...");
        input.nextLine();
        showMainMenu();
    }
    
    public static boolean checkUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }
    
    public static boolean checkPassword(String password) {
        if (password.length() < 8) return false;
        String specialChars = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`";
        for (char c : password.toCharArray()) {
            if (specialChars.contains(String.valueOf(c))) return true;
        }
        return false;
    }
    
    public static boolean checkPhone(String phone) {
        return phone.startsWith("+27") && phone.length() >= 12;
    }
    
    public static String hidePhone(String phone) {
        if (phone == null || phone.length() <= 3) return phone;
        String hidden = "";
        for (int i = 0; i < phone.length(); i++) {
            hidden += (i < 3) ? phone.charAt(i) : "*";
        }
        return hidden;
    }

    private static class JSONObject {

        public JSONObject() {
        }

        private JSONObject(String content) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private char[] toString(int i) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String messageHash, String get) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String users, JSONArray usersArray) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private Object getString(String username) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private JSONArray getJSONArray(String users) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String totalMessagesSent, int totalMessagesSent0) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private int getInt(String totalMessagesSent) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    private static class JSONArray {

        public JSONArray() {
        }

        private void put(JSONObject currentUser) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(int i, JSONObject currentUser) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private JSONObject getJSONObject(int i) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private int length() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}