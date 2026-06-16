/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.userinformation1;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;


/**
 *
 * @author anomp
 */
public class MessageTest {
    
    public MessageTest() {
    }
    private Message testMessage;
    private Message testMessage2;
    
  
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Starting Message Class Tests...");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Completed Message Class Tests.");
    }
    
    @BeforeEach
    public void setUp() {
        // Create test messages before each test
        testMessage = new Message(
            "Hello, this is a test message",
            "+27721234567",
            Message.FLAG_SENT,
            "+27821234567"
        );
        
        testMessage2 = new Message(
            "Another test message content here",
            "+27729876543",
            Message.FLAG_STORED,
            "+27829876543"
        );
    }
    
    @AfterEach
    public void tearDown() {
        testMessage = null;
        testMessage2 = null;
    }
    
    // ========== CONSTRUCTOR TESTS ==========
    
    @Test
    public void testConstructorWithMinimumParameters() {
        Message msg = new Message(
            "Test content",
            "+27720000000",
            Message.FLAG_DISPATCHED,
            "+27820000000"
        );
        
        assertNotNull(msg.getMessageID(), "Message ID should be generated");
        assertNotNull(msg.getMessageHash(), "Message hash should be generated");
        assertNotNull(msg.getTimestamp(), "Timestamp should be set");
        assertFalse(msg.isRead(), "Message should not be read by default");
        assertEquals(0, msg.getMessageNumber(), "Message number should be 0 by default");
    }
    
    @Test
    public void testConstructorWithAllParameters() {
        Date customDate = new Date();
        Message msg = new Message(
            "MSG123", "HASH123", "Content", "+27720000000",
            Message.FLAG_SENT, "+27820000000", customDate, 42, true
        );
        
        assertEquals("MSG123", msg.getMessageID(), "Message ID should match");
        assertEquals("HASH123", msg.getMessageHash(), "Message hash should match");
        assertEquals("Content", msg.getContent(), "Content should match");
        assertEquals("+27720000000", msg.getRecipient(), "Recipient should match");
        assertEquals(Message.FLAG_SENT, msg.getFlag(), "Flag should match");
        assertEquals("+27820000000", msg.getSender(), "Sender should match");
        assertEquals(customDate, msg.getTimestamp(), "Timestamp should match");
        assertEquals(42, msg.getMessageNumber(), "Message number should match");
        assertTrue(msg.isRead(), "Read status should match");
    }
    
    // ========== VALIDATION TESTS ==========
    
    @Test
    public void testValidMessage() {
        assertTrue(testMessage.isValid(), "Valid message should pass validation");
    }
    
    @Test
    public void testInvalidMessageWithNullContent() {
        Message invalidMsg = new Message(
            null, "+27721234567", Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message with null content should be invalid");
    }
    
    @Test
    public void testInvalidMessageWithEmptyContent() {
        Message invalidMsg = new Message(
            "", "+27721234567", Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message with empty content should be invalid");
    }
    
    @Test
    public void testInvalidMessageWithWhitespaceContent() {
        Message invalidMsg = new Message(
            "   ", "+27721234567", Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message with whitespace only should be invalid");
    }
    
    @Test
    public void testInvalidMessageWithExceedingMaxLength() {
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i <= Message.MAX_MESSAGE_LENGTH; i++) {
            longContent.append("a");
        }
        Message invalidMsg = new Message(
            longContent.toString(), "+27721234567", Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message exceeding max length should be invalid");
    }
    
    @Test
    public void testInvalidMessageWithWrongPhonePrefix() {
        Message invalidMsg = new Message(
            "Test content", "0712345678", Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message without +27 prefix should be invalid");
    }
    
    @Test
    public void testInvalidMessageWithNullRecipient() {
        Message invalidMsg = new Message(
            "Test content", null, Message.FLAG_SENT, "+27821234567"
        );
        assertFalse(invalidMsg.isValid(), "Message with null recipient should be invalid");
    }
    
    @Test
    public void testInvalidFlag() {
        assertFalse(Message.isValidFlag("InvalidFlag"), "Invalid flag should return false");
        assertFalse(Message.isValidFlag(""), "Empty flag should return false");
        assertFalse(Message.isValidFlag(null), "Null flag should return false");
    }
    
    @Test
    public void testValidFlags() {
        assertTrue(Message.isValidFlag(Message.FLAG_SENT), "FLAG_SENT should be valid");
        assertTrue(Message.isValidFlag(Message.FLAG_STORED), "FLAG_STORED should be valid");
        assertTrue(Message.isValidFlag(Message.FLAG_DISPATCHED), "FLAG_DISPATCHED should be valid");
        assertTrue(Message.isValidFlag(Message.FLAG_DISREGARD), "FLAG_DISREGARD should be valid");
    }
    
    // ========== MESSAGE HASH TESTS ==========
    
    @Test
    public void testMessageHashFormat() {
        testMessage.setMessageNumber(5);
        String hash = testMessage.getMessageHash();
        
        // Check format: "XX:Y:WORD:WORD"
        String[] parts = hash.split(":");
        assertEquals(4, parts.length, "Hash should have 4 parts");
        assertEquals(2, parts[0].length(), "First part should be 2 digits");
        
        // Verify numbers are digits
        assertTrue(parts[0].matches("\\d{2}"), "First part should be digits");
        assertTrue(parts[1].matches("\\d+"), "Second part should be a number");
    }
    
    @Test
    public void testMessageHashChangesWithContent() {
        testMessage.setMessageNumber(1);
        String hash1 = testMessage.getMessageHash();
        testMessage.setContent("Different content");
        String hash2 = testMessage.getMessageHash();
        
        assertNotEquals(hash1, hash2, "Changing content should change hash");
    }
    
    @Test
    public void testMessageHashChangesWithMessageNumber() {
        String hash1 = testMessage.getMessageHash();
        testMessage.setMessageNumber(100);
        String hash2 = testMessage.getMessageHash();
        
        assertNotEquals(hash1, hash2, "Changing message number should change hash");
    }
    
    @Test
    public void testMessageHashWithSingleWord() {
        Message singleWordMsg = new Message(
            "Hello", "+27721234567", Message.FLAG_SENT, "+27821234567"
        );
        singleWordMsg.setMessageNumber(1);
        String hash = singleWordMsg.getMessageHash();
        String[] parts = hash.split(":");
        
        assertEquals(parts[2], parts[3], "First and last word should be same for single word");
    }
    
    // ========== FLAG MANAGEMENT TESTS ==========
    
    @Test
    public void testUpdateFlagToValidFlag() {
        testMessage.updateFlag(Message.FLAG_STORED);
        assertEquals(Message.FLAG_STORED, testMessage.getFlag(), "Flag should be updated to STORED");
        
        testMessage.updateFlag(Message.FLAG_DISPATCHED);
        assertEquals(Message.FLAG_DISPATCHED, testMessage.getFlag(), "Flag should be updated to DISPATCHED");
        
        testMessage.updateFlag(Message.FLAG_DISREGARD);
        assertEquals(Message.FLAG_DISREGARD, testMessage.getFlag(), "Flag should be updated to DISREGARD");
    }
    
    @Test
    public void testUpdateFlagToInvalidFlag() {
        String originalFlag = testMessage.getFlag();
        testMessage.updateFlag("INVALID_FLAG");
        assertEquals(originalFlag, testMessage.getFlag(), "Flag should not change when updating to invalid flag");
    }
    
    @Test
    public void testStatusChecks() {
        Message sentMsg = new Message("Test", "+27721234567", Message.FLAG_SENT, "+27821234567");
        assertTrue(sentMsg.isSent(), "Sent message should return true for isSent()");
        assertFalse(sentMsg.isStored(), "Sent message should return false for isStored()");
        assertFalse(sentMsg.isDispatched(), "Sent message should return false for isDispatched()");
        assertFalse(sentMsg.isDisregarded(), "Sent message should return false for isDisregarded()");
        
        Message storedMsg = new Message("Test", "+27721234567", Message.FLAG_STORED, "+27821234567");
        assertTrue(storedMsg.isStored(), "Stored message should return true for isStored()");
        
        Message dispatchedMsg = new Message("Test", "+27721234567", Message.FLAG_DISPATCHED, "+27821234567");
        assertTrue(dispatchedMsg.isDispatched(), "Dispatched message should return true for isDispatched()");
        
        Message disregardedMsg = new Message("Test", "+27721234567", Message.FLAG_DISREGARD, "+27821234567");
        assertTrue(disregardedMsg.isDisregarded(), "Disregarded message should return true for isDisregarded()");
    }
    
    // ========== READ STATUS TESTS ==========
    
    @Test
    public void testMarkAsRead() {
        assertFalse(testMessage.isRead(), "Message should not be read initially");
        testMessage.markAsRead();
        assertTrue(testMessage.isRead(), "Message should be read after marking");
    }
    
    // ========== GETTER AND SETTER TESTS ==========
    
    @Test
    public void testSetContent() {
        String newContent = "Updated content for testing";
        testMessage.setContent(newContent);
        assertEquals(newContent, testMessage.getContent(), "Content should be updated");
        // Hash should be recalculated
        assertNotNull(testMessage.getMessageHash(), "Hash should be recalculated");
    }
    
    @Test
    public void testSetRecipient() {
        String newRecipient = "+27729999999";
        testMessage.setRecipient(newRecipient);
        assertEquals(newRecipient, testMessage.getRecipient(), "Recipient should be updated");
    }
    
    @Test
    public void testSetSender() {
        String newSender = "+27829999999";
        testMessage.setSender(newSender);
        assertEquals(newSender, testMessage.getSender(), "Sender should be updated");
    }
    
    @Test
    public void testSetMessageNumber() {
        testMessage.setMessageNumber(99);
        assertEquals(99, testMessage.getMessageNumber(), "Message number should be updated");
        // Hash should be recalculated
        assertNotNull(testMessage.getMessageHash(), "Hash should be recalculated");
    }
    
    // ========== UTILITY METHOD TESTS ==========
    
    @Test
    public void testGetFormattedTimestamp() {
        String formatted = testMessage.getFormattedTimestamp();
        assertNotNull(formatted, "Formatted timestamp should not be null");
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), 
                   "Formatted timestamp should match pattern");
    }
    
    @Test
    public void testGetSummaryShortMessage() {
        String shortContent = "Short message";
        Message shortMsg = new Message(shortContent, "+27721234567", Message.FLAG_SENT, "+27821234567");
        assertEquals(shortContent, shortMsg.getSummary(), 
                    "Summary should return full content for short messages");
    }
    
    @Test
    public void testGetSummaryLongMessage() {
        String longContent = "This is a very long message that exceeds the fifty character limit for the summary method testing";
        Message longMsg = new Message(longContent, "+27721234567", Message.FLAG_SENT, "+27821234567");
        String summary = longMsg.getSummary();
        
        assertTrue(summary.length() <= 50, "Summary should be shortened");
        assertTrue(summary.endsWith("..."), "Summary should end with ellipsis");
        assertEquals(50, summary.length(), "Summary should be 50 characters including ellipsis");
    }
    
    @Test
    public void testGetLength() {
        String content = "Hello World";
        Message msg = new Message(content, "+27721234567", Message.FLAG_SENT, "+27821234567");
        assertEquals(content.length(), msg.getLength(), "Message length should match content length");
    }
    
    @Test
    public void testCompareLength() {
        Message shorterMsg = new Message("Short", "+27721234567", Message.FLAG_SENT, "+27821234567");
        Message longerMsg = new Message("This is a longer message", "+27721234567", Message.FLAG_SENT, "+27821234567");
        
        assertTrue(shorterMsg.compareLength(longerMsg) < 0, 
                   "Shorter message should be less than longer message");
        assertTrue(longerMsg.compareLength(shorterMsg) > 0, 
                   "Longer message should be greater than shorter message");
        assertEquals(0, testMessage.compareLength(testMessage), 
                    "Same length messages should be equal");
    }
    
    // ========== DISPLAY METHOD TESTS ==========
    
    @Test
    public void testDisplayFullDetails() {
        // Just verify it doesn't throw any exceptions
        assertDoesNotThrow(() -> testMessage.displayFullDetails(), 
                          "displayFullDetails() should not throw exceptions");
    }
    
    // ========== STRING REPRESENTATION TESTS ==========
    
    @Test
    public void testToString() {
        String toStringResult = testMessage.toString();
        assertTrue(toStringResult.contains(testMessage.getFlag()), 
                  "toString should contain flag");
        assertTrue(toStringResult.contains(testMessage.getSender()), 
                  "toString should contain sender");
        assertTrue(toStringResult.contains(testMessage.getRecipient()), 
                  "toString should contain recipient");
        assertTrue(toStringResult.contains(testMessage.getSummary()), 
                  "toString should contain summary");
    }
    
    // ========== EQUALITY TESTS ==========
    
    @Test
    public void testEqualsSameObject() {
        assertEquals(testMessage, testMessage, "Message should equal itself");
    }
    
    @Test
    public void testEqualsDifferentMessage() {
        assertNotEquals(testMessage, testMessage2, "Different messages should not be equal");
    }
    
    @Test
    public void testEqualsNull() {
        assertNotEquals(testMessage, null, "Message should not equal null");
    }
    
    @Test
    public void testHashCode() {
        Message sameMessage = testMessage;
        assertEquals(testMessage.hashCode(), sameMessage.hashCode(), 
                    "Same messages should have same hashcode");
    }
    
    // ========== CSV CONVERSION TESTS ==========
    
    @Test
    public void testToCSV() {
        String csv = testMessage.toCSV();
        assertNotNull(csv, "CSV output should not be null");
        assertTrue(csv.contains(testMessage.getMessageID()), "CSV should contain message ID");
        assertTrue(csv.contains(testMessage.getMessageHash()), "CSV should contain message hash");
        assertTrue(csv.contains(testMessage.getRecipient()), "CSV should contain recipient");
    }
    
    @Test
    public void testFromCSV() {
        // Create a message and convert to CSV
        Message originalMsg = new Message(
            "Test CSV content", "+27721112222", Message.FLAG_DISPATCHED, "+27821112222"
        );
        originalMsg.setMessageNumber(42);
        originalMsg.markAsRead();
        
        String csv = originalMsg.toCSV();
        Message reconstructedMsg = Message.fromCSV(csv);
        
        assertNotNull(reconstructedMsg, "Message should be reconstructed from CSV");
        assertEquals(originalMsg.getMessageID(), reconstructedMsg.getMessageID(), "Message ID should match");
        assertEquals(originalMsg.getMessageHash(), reconstructedMsg.getMessageHash(), "Message hash should match");
        assertEquals(originalMsg.getContent(), reconstructedMsg.getContent(), "Content should match");
        assertEquals(originalMsg.getRecipient(), reconstructedMsg.getRecipient(), "Recipient should match");
        assertEquals(originalMsg.getFlag(), reconstructedMsg.getFlag(), "Flag should match");
        assertEquals(originalMsg.getSender(), reconstructedMsg.getSender(), "Sender should match");
        assertEquals(originalMsg.getMessageNumber(), reconstructedMsg.getMessageNumber(), "Message number should match");
        assertEquals(originalMsg.isRead(), reconstructedMsg.isRead(), "Read status should match");
    }
    
    @Test
    public void testFromCSVWithCommaInContent() {
        Message msgWithComma = new Message(
            "Hello, world! This has a comma", "+27721112222", Message.FLAG_SENT, "+27821112222"
        );
        String csv = msgWithComma.toCSV();
        Message reconstructed = Message.fromCSV(csv);
        
        assertNotNull(reconstructed, "Message with comma should be reconstructed");
        assertEquals(msgWithComma.getContent(), reconstructed.getContent(), 
                    "Content with comma should be preserved");
    }
    
    @Test
    public void testFromCSVWithMalformedInput() {
        Message result = Message.fromCSV("malformed,csv,line");
        assertNull(result, "Malformed CSV should return null");
        
        result = Message.fromCSV("");
        assertNull(result, "Empty CSV should return null");
        
        result = Message.fromCSV(null);
        assertNull(result, "Null CSV should return null");
    }
    
    // ========== UNIQUE ID GENERATION TESTS ==========
    
    @Test
    public void testUniqueMessageIDs() {
        Message msg1 = new Message("Content1", "+27721234567", Message.FLAG_SENT, "+27821234567");
        Message msg2 = new Message("Content2", "+27721234567", Message.FLAG_SENT, "+27821234567");
        
        assertNotEquals(msg1.getMessageID(), msg2.getMessageID(), "Message IDs should be unique");
    }
    
    // ========== TIMESTAMP TESTS ==========
    
    @Test
    public void testTimestampIsSetOnCreation() {
        Date beforeCreation = new Date();
        Message msg = new Message("Test", "+27721234567", Message.FLAG_SENT, "+27821234567");
        Date afterCreation = new Date();
        
        assertNotNull(msg.getTimestamp(), "Timestamp should not be null");
        assertFalse(msg.getTimestamp().before(beforeCreation), 
                   "Timestamp should be after creation start");
        assertFalse(msg.getTimestamp().after(afterCreation), 
                   "Timestamp should be before creation end");
    }
}
   