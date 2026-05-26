/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.userinformation1;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
/**
 *
 * @author anomp
 */
public class Userinformation1 {
// Store data in memory
    static ArrayList<String> messages = new ArrayList<String>();
    static ArrayList<String> messageIDs = new ArrayList<String>();
    static ArrayList<String> recipients = new ArrayList<String>();
    static ArrayList<String> messageHashes = new ArrayList<String>();
    static ArrayList<Integer> messageNumbers = new ArrayList<Integer>();
    static int totalMessagesSent = 0;
    static String loggedInUser = "";
    static String loggedInUsername = "";
    static String currentUserFirstName = "";
    static String currentUserLastName = "";
    static String currentUserPhone = "";
    
    private static final String DATA_FILE = "userdata.json";

    public static void main(String[] args) {
        // Load existing data from JSON file
        loadDataFromJSON();
        
        Scanner input = new Scanner(System.in);

        // Ask name
        System.out.print("Enter your first name: ");
        String fullName = input.nextLine();

        // Ask last name
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        // Ask username
        System.out.print("Enter username (<=5 characters and must contain _): ");
        String username = input.nextLine();
        boolean isValidUsername = checkUsername(username);
        if (!isValidUsername) {
            System.out.println("Username unsuccessful: must be <=5 characters AND contain '_'");
            return;
        }
        System.out.println("Username successfully captured");

        // Ask password
        System.out.print("Enter password (>=8 characters and must contain special character): ");
        String password = input.nextLine();
        boolean isValidPassword = checkPassword(password);
        if (!isValidPassword) {
            System.out.println("Password unsuccessful: must be >=8 characters AND contain at least one special character (!@#$%^&*()_+ etc.)");
            return;
        }
        System.out.println("Password successfully captured");

        // Ask phone number
        System.out.print("Enter phone number (+27...): ");
        String phone = input.nextLine();
        boolean isValidPhone = checkPhone(phone);
        if (!isValidPhone) {
            System.out.println("Phone number unsuccessful: must start with +27");
            return;
        }
        System.out.println("Phone number successfully captured");

        // Ask user to login
        System.out.println("------------- LOGIN -------------");
        System.out.print("Enter username: ");
        String loginName = input.nextLine();
        System.out.print("Enter password: ");
        String loginPass = input.nextLine();

        // Check if login matches stored details
        if (loginName.equals(username) && loginPass.equals(password)) {
            System.out.println("Login successful, welcome back " + fullName + " " + lastName + "!");
            System.out.println("Your phone number is: " + hidePhone(phone));
            loggedInUser = fullName + " " + lastName;
            loggedInUsername = username;
            currentUserFirstName = fullName;
            currentUserLastName = lastName;
            currentUserPhone = phone;
            
            // Load user's existing messages if any
            loadUserMessages(username);
            
            // Show welcome message and main menu
            showWelcomeScreen();
            
        } else {
            System.out.println("Login failed, please try again.");
        }
        
        // Save data before exiting
        saveDataToJSON();
    }
    
    // Method to load user messages from JSON
    public static void loadUserMessages(String username) {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                return;
            }
            
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONObject jsonData = new JSONObject(content);
            JSONArray usersArray = jsonData.getJSONArray("users");
            
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("username").equals(username)) {
                    // Clear current lists
                    messages.clear();
                    messageIDs.clear();
                    recipients.clear();
                    messageHashes.clear();
                    messageNumbers.clear();
                    
                    // Load user's messages
                    totalMessagesSent = user.getInt("totalMessagesSent");
                    JSONArray userMessages = user.getJSONArray("messages");
                    
                    for (int j = 0; j < userMessages.length(); j++) {
                        JSONObject msg = userMessages.getJSONObject(j);
                        messages.add((String) msg.getString("content"));
                        messageIDs.add((String) msg.getString("messageID"));
                        recipients.add((String) msg.getString("recipient"));
                        messageHashes.add((String) msg.getString("messageHash"));
                        messageNumbers.add(msg.getInt("messageNumber"));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Note: No existing data found. Starting fresh.");
        }
    }
    
    // Load all data from JSON file
    public static void loadDataFromJSON() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                return;
            }
            
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONObject jsonData = new JSONObject(content);
            // Data will be loaded per user during login
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
    
    // Save all data to JSON file
    public static void saveDataToJSON() {
        try {
            JSONObject jsonData = new JSONObject();
            JSONArray usersArray = new JSONArray();
            
            // Create or update current user
            JSONObject currentUser = new JSONObject();
            currentUser.put("firstName", currentUserFirstName);
            currentUser.put("lastName", currentUserLastName);
            currentUser.put("username", loggedInUsername);
            currentUser.put("password", ""); // In real app, store hashed password
            currentUser.put("phoneNumber", currentUserPhone);
            currentUser.put("totalMessagesSent", totalMessagesSent);
            
            // Add messages
            JSONArray userMessages = new JSONArray();
            for (int i = 0; i < messages.size(); i++) {
                JSONObject msg = new JSONObject();
                msg.put("messageID", messageIDs.get(i));
                msg.put("recipient", recipients.get(i));
                msg.put("content", messages.get(i));
                msg.put("messageHash", messageHashes.get(i));
                msg.put("messageNumber", messageNumbers.get(i));
                msg.put("timestamp", (int) System.currentTimeMillis());
                userMessages.put(msg);
            }
            currentUser.put("messages", userMessages);
            
            // Check if user already exists
            File file = new File(DATA_FILE);
            boolean userExists = false;
            
            if (file.exists()) {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                JSONObject existingData = new JSONObject(content);
                JSONArray existingUsers = existingData.getJSONArray("users");
                
                for (int i = 0; i < existingUsers.length(); i++) {
                    JSONObject user = existingUsers.getJSONObject(i);
                    if (user.getString("username").equals(loggedInUsername)) {
                        // Update existing user
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
            
            // Write to file
            try (FileWriter fileWriter = new FileWriter(DATA_FILE)) {
                fileWriter.write(jsonData.toString(4)); // Pretty print with 4 spaces
                fileWriter.flush();
            }
            
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
    
    // Method to show welcome screen and main menu
    public static void showWelcomeScreen() {
        System.out.println("\n=====================================");
        System.out.println("     WELCOME TO QUICKCHAT");
        System.out.println("=====================================");
        System.out.println("Hello " + loggedInUser + "!");
        showMainMenu();
    }
    
    // Main menu method
    public static void showMainMenu() {
        Scanner input = new Scanner(System.in);
        int choice;
        
        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send New Message");
            System.out.println("2. View All Messages");
            System.out.println("3. Delete Message");
            System.out.println("4. Exit");
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
                    saveDataToJSON(); // Save before exit
                    System.out.println("Thank you for using QuickChat! Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while(choice != 4);
    }
    
    // Method to send messages
    public static void sendMessage() {
        Scanner input = new Scanner(System.in);
        
        System.out.print("\nHow many messages do you want to send? ");
        int numMessages = input.nextInt();
        input.nextLine();
        
        System.out.println("\nYou want to send " + numMessages + " message(s)");
        System.out.println("Total messages sent so far: " + totalMessagesSent);
        System.out.println("Messages you will send now: " + numMessages);
        System.out.println("----------------------------------------");
        
        for(int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
            
            System.out.print("Enter recipient cell number: ");
            String recipient = input.nextLine();
            
            System.out.print("Enter your message (max 250 characters): ");
            String messageContent = input.nextLine();
            
            if(messageContent.length() > 250) {
                System.out.println("Message exceeds 250 characters! Message not sent.");
                System.out.println("Your message had " + messageContent.length() + " characters.");
                continue;
            }
            
            if(messageContent.trim().isEmpty()) {
                System.out.println("Cannot send empty message! Message not sent.");
                continue;
            }
            
            String messageID = generateMessageID();
            int messageNum = totalMessagesSent + 1;
            String messageHash = calculateMessageHash(messageID, messageNum, messageContent);
            
            messages.add(messageContent);
            messageIDs.add(messageID);
            recipients.add(recipient);
            messageHashes.add(messageHash);
            messageNumbers.add(messageNum);
            totalMessagesSent++;
            
            System.out.println("\n✓ Message sent successfully!");
            System.out.println("Message ID: " + messageID);
            System.out.println("Recipient: " + recipient);
            System.out.println("Message hash: " + messageHash);
            System.out.println("Message length: " + messageContent.length() + "/250 characters");
            System.out.println("Total messages sent overall: " + totalMessagesSent);
            System.out.println("Message " + i + " of " + numMessages + " sent");
            
            // Save after each message
            saveDataToJSON();
        }
        
        System.out.println("\n=====================================");
        System.out.println("SUMMARY: You sent " + numMessages + " message(s)");
        System.out.println("Total messages in system: " + totalMessagesSent);
        System.out.println("=====================================");
        
        returnToMenu();
    }
    
    // Method to view all messages
    public static void viewMessages() {
        System.out.println("\n========== ALL MESSAGES ==========");
        System.out.println("Total messages stored: " + totalMessagesSent);
        System.out.println("===================================");
        
        if(messages.isEmpty()) {
            System.out.println("No messages to display!");
        } else {
            for(int i = 0; i < messages.size(); i++) {
                System.out.println("\nMessage #" + (i+1));
                System.out.println("Message ID: " + messageIDs.get(i));
                System.out.println("Recipient: " + recipients.get(i));
                System.out.println("Content: " + messages.get(i));
                System.out.println("Hash: " + messageHashes.get(i));
                System.out.println("Length: " + messages.get(i).length() + "/250");
                System.out.println("---------------------------");
            }
        }
        
        returnToMenu();
    }
    
    // Method to delete a message
    public static void deleteMessage() {
        Scanner input = new Scanner(System.in);
        
        System.out.println("\n========== DELETE MESSAGE ==========");
        System.out.println("Total messages available: " + totalMessagesSent);
        System.out.println("====================================");
        
        if(messages.isEmpty()) {
            System.out.println("No messages to delete!");
            returnToMenu();
            return;
        }
        
        for(int i = 0; i < messages.size(); i++) {
            System.out.println((i+1) + ". Message ID: " + messageIDs.get(i) + " - To: " + recipients.get(i));
            String preview = messages.get(i).length() > 30 ? 
                messages.get(i).substring(0, 30) + "..." : 
                messages.get(i);
            System.out.println("   Content: " + preview);
        }
        
        System.out.print("\nEnter message number to delete (or press 0 to cancel): ");
        int deleteChoice = input.nextInt();
        
        if(deleteChoice == 0) {
            System.out.println("Delete cancelled. Returning to menu...");
        } else if(deleteChoice > 0 && deleteChoice <= messages.size()) {
            String removedMessage = messages.get(deleteChoice - 1);
            String removedID = messageIDs.get(deleteChoice - 1);
            messages.remove(deleteChoice - 1);
            messageIDs.remove(deleteChoice - 1);
            recipients.remove(deleteChoice - 1);
            messageHashes.remove(deleteChoice - 1);
            messageNumbers.remove(deleteChoice - 1);
            totalMessagesSent--;
            
            System.out.println("\n✓ Message deleted successfully!");
            System.out.println("Deleted message ID: " + removedID);
            System.out.println("Deleted message content: " + removedMessage);
            System.out.println("Remaining messages: " + totalMessagesSent);
            
            // Save after deletion
            saveDataToJSON();
        } else {
            System.out.println("Invalid choice! No message deleted.");
        }
        
        returnToMenu();
    }
    
    // Method to generate unique message ID
    public static String generateMessageID() {
        String id = "MSG";
        id += System.currentTimeMillis();
        id += (int)(Math.random() * 1000);
        return id;
    }
    
    // Calculate message hash
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
        
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord;
        
        return hash.toUpperCase();
    }
    
    // Method to return to menu
    public static void returnToMenu() {
        Scanner input = new Scanner(System.in);
        System.out.println("\nPress Enter to return to main menu...");
        input.nextLine();
        showMainMenu();
    }
    
    // Username validation method
    public static boolean checkUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }
    
    // Password validation method
    public static boolean checkPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        String specialChars = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/~`";
        
        boolean hasSpecialChar = false;
        for (int i = 0; i < password.length(); i++) {
            if (specialChars.contains(String.valueOf(password.charAt(i)))) {
                hasSpecialChar = true;
                break;
            }
        }
        
        return hasSpecialChar;
    }
    
    // Phone validation method
    public static boolean checkPhone(String phone) {
        return phone.startsWith("+27") && phone.length() >= 12;
    }
    
    // Hide phone number for privacy
    public static String hidePhone(String phone) {
        if (phone == null || phone.length() <= 3) {
            return phone;
        }
        String hidden = "";
        for (int i = 0; i < phone.length(); i++) {
            if (i < 3) {
                hidden += phone.charAt(i);
            } else {
                hidden += "*";
            }
        }
        return hidden;
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

    private static class JSONObject {

        public JSONObject() {
        }

        private JSONObject(String content) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private JSONArray getJSONArray(String users) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private Object getString(String username) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private int getInt(String totalMessagesSent) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String firstName, String currentUserFirstName) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String totalMessagesSent, int totalMessagesSent0) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void put(String messages, JSONArray userMessages) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private char[] toString(int i) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}