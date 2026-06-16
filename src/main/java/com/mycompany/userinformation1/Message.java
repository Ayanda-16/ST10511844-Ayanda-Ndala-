/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.userinformation1;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;
/**
 *
 * @author anomp
 */
public class Message {
  // Message properties
    private String messageID;
    private String messageHash;
    private String content;
    private String recipient;
    private String flag; // Sent, Stored, Dispatched, Disregard
    private String sender;
    private Date timestamp;
    private int messageNumber;
    private boolean isRead;
    
    // Constants for message flags
    public static final String FLAG_SENT = "Sent";
    public static final String FLAG_STORED = "Stored";
    public static final String FLAG_DISPATCHED = "Dispatched";
    public static final String FLAG_DISREGARD = "Disregard";
    
    // Constants for validation
    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final String PHONE_PREFIX = "+27";
    
    /**
     * Constructor for creating a new message
     */
    public Message(String content, String recipient, String flag, String sender) {
        this.messageID = generateMessageID();
        this.content = content;
        this.recipient = recipient;
        this.flag = flag;
        this.sender = sender;
        this.timestamp = new Date();
        this.messageNumber = 0; // Will be set by the system
        this.isRead = false;
        this.messageHash = calculateMessageHash();
    }
    
    /**
     * Constructor for loading existing message from storage
     */
    public Message(String messageID, String messageHash, String content, String recipient, 
                   String flag, String sender, Date timestamp, int messageNumber, boolean isRead) {
        this.messageID = messageID;
        this.messageHash = messageHash;
        this.content = content;
        this.recipient = recipient;
        this.flag = flag;
        this.sender = sender;
        this.timestamp = timestamp;
        this.messageNumber = messageNumber;
        this.isRead = isRead;
    }
    
    /**
     * Generates a unique message ID
     */
    private String generateMessageID() {
        return "MSG" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 5);
    }
    
    /**
     * Calculates the message hash based on requirements
     */
    private String calculateMessageHash() {
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
        
        String[] words = content.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        firstWord = firstWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        lastWord = lastWord.replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");
        
        return (firstTwoNumbers + ":" + messageNumber + ":" + firstWord + ":" + lastWord).toUpperCase();
    }
    
    /**
     * Validates message content
     */
    public boolean isValid() {
        return content != null && 
               content.length() <= MAX_MESSAGE_LENGTH && 
               content.trim().length() > 0 &&
               recipient != null && 
               recipient.startsWith(PHONE_PREFIX) &&
               isValidFlag(flag);
    }
    
    /**
     * Checks if flag is valid
     */
    public static boolean isValidFlag(String flag) {
        return flag.equals(FLAG_SENT) || 
               flag.equals(FLAG_STORED) || 
               flag.equals(FLAG_DISPATCHED) || 
               flag.equals(FLAG_DISREGARD);
    }
    
    /**
     * Marks message as read
     */
    public void markAsRead() {
        this.isRead = true;
    }
    
    /**
     * Updates the message flag
     */
    public void updateFlag(String newFlag) {
        if (isValidFlag(newFlag)) {
            this.flag = newFlag;
        }
    }
    
    /**
     * Gets formatted timestamp
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    /**
     * Gets message summary (shortened version)
     */
    public String getSummary() {
        if (content.length() <= 50) {
            return content;
        }
        return content.substring(0, 47) + "...";
    }
    
    /**
     * Checks if message is stored
     */
    public boolean isStored() {
        return flag.equals(FLAG_STORED);
    }
    
    /**
     * Checks if message is dispatched
     */
    public boolean isDispatched() {
        return flag.equals(FLAG_DISPATCHED);
    }
    
    /**
     * Checks if message was disregarded
     */
    public boolean isDisregarded() {
        return flag.equals(FLAG_DISREGARD);
    }
    
    /**
     * Checks if message is sent
     */
    public boolean isSent() {
        return flag.equals(FLAG_SENT);
    }
    
    /**
     * Displays full message details
     */
    public void displayFullDetails() {
        System.out.println("========== MESSAGE DETAILS ==========");
        System.out.println("Message ID: " + messageID);
        System.out.println("Message Hash: " + messageHash);
        System.out.println("From: " + sender);
        System.out.println("To: " + recipient);
        System.out.println("Content: " + content);
        System.out.println("Status: " + flag);
        System.out.println("Timestamp: " + getFormattedTimestamp());
        System.out.println("Read: " + (isRead ? "Yes" : "No"));
        System.out.println("Message #: " + messageNumber);
        System.out.println("=====================================");
    }
    
    /**
     * Converts message to CSV format for storage
     */
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%d,%b",
            messageID, messageHash, content.replace(",", "\\,"), 
            recipient, flag, sender, messageNumber, isRead);
    }
    
    /**
     * Creates message from CSV string
     */
    public static Message fromCSV(String csvLine) {
        try {
            String[] parts = csvLine.split("(?<!\\\\),");
            return new Message(
                parts[0], parts[1], parts[2].replace("\\,", ","),
                parts[3], parts[4], parts[5], new Date(),
                Integer.parseInt(parts[6]), Boolean.parseBoolean(parts[7])
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets the length of the message content
     */
    public int getLength() {
        return content.length();
    }
    
    /**
     * Compares two messages by content length
     */
    public int compareLength(Message other) {
        return Integer.compare(this.getLength(), other.getLength());
    }
    
    // Getters and Setters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; this.messageHash = calculateMessageHash(); }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getFlag() { return flag; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public Date getTimestamp() { return timestamp; }
    public int getMessageNumber() { return messageNumber; }
    public void setMessageNumber(int messageNumber) { this.messageNumber = messageNumber; this.messageHash = calculateMessageHash(); }
    public boolean isRead() { return isRead; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s -> %s: %s", flag, sender, recipient, getSummary());
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

 