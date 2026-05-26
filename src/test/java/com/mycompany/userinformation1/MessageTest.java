/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.userinformation1;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 *
 * @author anomp
 */
public class MessageTest {
    
       private static final String VALID_RECIPIENT = "+1234567890";
    private static final String VALID_CONTENT = "Hello, this is a test message!";
    private static final int MAX_MESSAGE_LENGTH = 250;
    
    @BeforeEach
    void setUp() {
        // Clear all messages before each test
        Message.clearAllMessages();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        Message.clearAllMessages();
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    @Order(1)
    void testConstructorWithValidInput() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        
        assertNotNull(message);
        assertEquals(VALID_RECIPIENT, message.getRecipient());
        assertEquals(VALID_CONTENT, message.getContent());
        assertNotNull(message.getMessageID());
        assertTrue(message.getMessageID().startsWith("MSG"));
        assertEquals(1, message.getMessageNumber());
        assertTrue(message.getTimestamp() > 0);
        assertNotNull(message.getMessageHash());
        assertEquals(1, Message.getTotalMessagesSent());
        assertEquals(1, Message.getAllMessages().size());
    }
    
    @Test
    @Order(2)
    void testConstructorWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(VALID_RECIPIENT, null);
        });
    }
    
    @Test
    @Order(3)
    void testConstructorWithEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(VALID_RECIPIENT, "");
        });
    }
    
    @Test
    @Order(4)
    void testConstructorWithWhitespaceContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(VALID_RECIPIENT, "   ");
        });
    }
    
    @Test
    @Order(5)
    void testConstructorWithExceedingMaxLength() {
        String longContent = "A".repeat(MAX_MESSAGE_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(VALID_RECIPIENT, longContent);
        });
    }
    
    @Test
    @Order(6)
    void testConstructorWithExactMaxLength() {
        String exactContent = "A".repeat(MAX_MESSAGE_LENGTH);
        Message message = new Message(VALID_RECIPIENT, exactContent);
        assertNotNull(message);
        assertEquals(MAX_MESSAGE_LENGTH, message.getContentLength());
        assertEquals(0, message.getRemainingCharacters());
    }
    
    @Test
    @Order(7)
    void testMessageNumberIncrements() {
        Message msg1 = new Message(VALID_RECIPIENT, "First message");
        Message msg2 = new Message(VALID_RECIPIENT, "Second message");
        Message msg3 = new Message(VALID_RECIPIENT, "Third message");
        
        assertEquals(1, msg1.getMessageNumber());
        assertEquals(2, msg2.getMessageNumber());
        assertEquals(3, msg3.getMessageNumber());
        assertEquals(3, Message.getTotalMessagesSent());
    }
    
    // ==================== HASH AND INTEGRITY TESTS ====================
    
    @Test
    @Order(8)
    void testCalculateMessageHash() {
        Message message = new Message(VALID_RECIPIENT, "Hello world");
        String hash = message.calculateMessageHash();
        
        assertNotNull(hash);
        assertTrue(hash.contains(":"));
        assertTrue(hash.matches("^[0-9]+:.*"));
    }
    
    @Test
    @Order(9)
    void testVerifyIntegrityValid() {
        Message message = new Message(VALID_RECIPIENT, "Test content");
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(10)
    void testVerifyIntegrityAfterContentChange() {
        Message message = new Message(VALID_RECIPIENT, "Original content");
        String originalHash = message.getMessageHash();
        
        message.setContent("Modified content");
        assertNotEquals(originalHash, message.getMessageHash());
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(11)
    void testHashWithMultipleMessages() {
        Message msg1 = new Message(VALID_RECIPIENT, "First message");
        Message msg2 = new Message(VALID_RECIPIENT, "Second message");
        
        assertNotEquals(msg1.getMessageHash(), msg2.getMessageHash());
    }
    
    @Test
    @Order(12)
    void testHashWithSameContent() {
        Message msg1 = new Message(VALID_RECIPIENT, "Same content");
        Message msg2 = new Message("+1987654321", "Same content");
        
        assertNotEquals(msg1.getMessageHash(), msg2.getMessageHash());
    }
    
    // ==================== GETTER TESTS ====================
    
    @Test
    @Order(13)
    void testGetters() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        
        assertNotNull(message.getMessageID());
        assertEquals(VALID_RECIPIENT, message.getRecipient());
        assertEquals(VALID_CONTENT, message.getContent());
        assertNotNull(message.getMessageHash());
        assertEquals(1, message.getMessageNumber());
        assertTrue(message.getTimestamp() > 0);
        assertEquals(VALID_CONTENT.length(), message.getContentLength());
        assertEquals(MAX_MESSAGE_LENGTH - VALID_CONTENT.length(), message.getRemainingCharacters());
    }
    
    // ==================== SETTER TESTS ====================
    
    @Test
    @Order(14)
    void testSetContentValid() {
        Message message = new Message(VALID_RECIPIENT, "Original content");
        String newContent = "Updated content";
        
        message.setContent(newContent);
        assertEquals(newContent, message.getContent());
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(15)
    void testSetContentNull() {
        Message message = new Message(VALID_RECIPIENT, "Original content");
        assertThrows(IllegalArgumentException.class, () -> {
            message.setContent(null);
        });
    }
    
    @Test
    @Order(16)
    void testSetContentEmpty() {
        Message message = new Message(VALID_RECIPIENT, "Original content");
        assertThrows(IllegalArgumentException.class, () -> {
            message.setContent("");
        });
    }
    
    @Test
    @Order(17)
    void testSetContentTooLong() {
        Message message = new Message(VALID_RECIPIENT, "Original content");
        String longContent = "A".repeat(MAX_MESSAGE_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> {
            message.setContent(longContent);
        });
    }
    
    @Test
    @Order(18)
    void testSetRecipientValid() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        String newRecipient = "+19998887777";
        
        message.setRecipient(newRecipient);
        assertEquals(newRecipient, message.getRecipient());
    }
    
    @Test
    @Order(19)
    void testSetRecipientNull() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        assertThrows(IllegalArgumentException.class, () -> {
            message.setRecipient(null);
        });
    }
    
    @Test
    @Order(20)
    void testSetRecipientEmpty() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        assertThrows(IllegalArgumentException.class, () -> {
            message.setRecipient("");
        });
    }
    
    // ==================== STATIC METHOD TESTS ====================
    
    @Test
    @Order(21)
    void testGetAllMessages() {
        Message msg1 = new Message(VALID_RECIPIENT, "Message 1");
        Message msg2 = new Message(VALID_RECIPIENT, "Message 2");
        
        List<Message> allMessages = Message.getAllMessages();
        assertEquals(2, allMessages.size());
        assertTrue(allMessages.contains(msg1));
        assertTrue(allMessages.contains(msg2));
        
        // Verify it returns a copy
        allMessages.clear();
        assertEquals(2, Message.getAllMessages().size());
    }
    
    @Test
    @Order(22)
    void testGetTotalMessagesSent() {
        assertEquals(0, Message.getTotalMessagesSent());
        
        new Message(VALID_RECIPIENT, "First");
        assertEquals(1, Message.getTotalMessagesSent());
        
        new Message(VALID_RECIPIENT, "Second");
        assertEquals(2, Message.getTotalMessagesSent());
    }
    
    @Test
    @Order(23)
    void testDeleteMessageByIndex() {
        Message msg1 = new Message(VALID_RECIPIENT, "First");
        Message msg2 = new Message(VALID_RECIPIENT, "Second");
        Message msg3 = new Message(VALID_RECIPIENT, "Third");
        
        assertEquals(3, Message.getTotalMessagesSent());
        
        Message deleted = Message.deleteMessage(1);
        assertNotNull(deleted);
        assertEquals(msg2.getMessageID(), deleted.getMessageID());
        assertEquals(2, Message.getAllMessages().size());
        assertEquals(2, Message.getTotalMessagesSent());
        
        // Verify remaining messages
        List<Message> remaining = Message.getAllMessages();
        assertEquals(msg1.getMessageID(), remaining.get(0).getMessageID());
        assertEquals(msg3.getMessageID(), remaining.get(1).getMessageID());
    }
    
    @Test
    @Order(24)
    void testDeleteMessageByInvalidIndex() {
        new Message(VALID_RECIPIENT, "Test");
        
        Message deleted = Message.deleteMessage(5);
        assertNull(deleted);
        assertEquals(1, Message.getAllMessages().size());
        assertEquals(1, Message.getTotalMessagesSent());
        
        deleted = Message.deleteMessage(-1);
        assertNull(deleted);
        assertEquals(1, Message.getAllMessages().size());
    }
    
    @Test
    @Order(25)
    void testDeleteMessageByID() {
        Message msg1 = new Message(VALID_RECIPIENT, "First");
        Message msg2 = new Message(VALID_RECIPIENT, "Second");
        
        Message deleted = Message.deleteMessageByID(msg1.getMessageID());
        assertNotNull(deleted);
        assertEquals(msg1.getMessageID(), deleted.getMessageID());
        assertEquals(1, Message.getAllMessages().size());
        assertEquals(1, Message.getTotalMessagesSent());
        
        // Try to delete non-existent message
        deleted = Message.deleteMessageByID("NONEXISTENT");
        assertNull(deleted);
        assertEquals(1, Message.getAllMessages().size());
    }
    
    @Test
    @Order(26)
    void testFindMessageByID() {
        Message msg1 = new Message(VALID_RECIPIENT, "First");
        Message msg2 = new Message(VALID_RECIPIENT, "Second");
        
        Message found = Message.findMessageByID(msg1.getMessageID());
        assertNotNull(found);
        assertEquals(msg1.getMessageID(), found.getMessageID());
        
        found = Message.findMessageByID(msg2.getMessageID());
        assertNotNull(found);
        assertEquals(msg2.getMessageID(), found.getMessageID());
        
        found = Message.findMessageByID("NONEXISTENT");
        assertNull(found);
    }
    
    @Test
    @Order(27)
    void testClearAllMessages() {
        new Message(VALID_RECIPIENT, "First");
        new Message(VALID_RECIPIENT, "Second");
        new Message(VALID_RECIPIENT, "Third");
        
        assertEquals(3, Message.getTotalMessagesSent());
        assertEquals(3, Message.getAllMessages().size());
        
        Message.clearAllMessages();
        
        assertEquals(0, Message.getTotalMessagesSent());
        assertEquals(0, Message.getAllMessages().size());
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    @Order(28)
    void testSearchByRecipient() {
        new Message("+1234567890", "Message 1");
        new Message("+1234567890", "Message 2");
        new Message("+1987654321", "Message 3");
        
        List<Message> results = Message.searchByRecipient("1234567890");
        assertEquals(2, results.size());
        
        results = Message.searchByRecipient("987654321");
        assertEquals(1, results.size());
        
        results = Message.searchByRecipient("999");
        assertEquals(0, results.size());
    }
    
    @Test
    @Order(29)
    void testSearchByRecipientNull() {
        new Message(VALID_RECIPIENT, "Test");
        List<Message> results = Message.searchByRecipient(null);
        assertTrue(results.isEmpty());
    }
    
    @Test
    @Order(30)
    void testSearchByRecipientEmpty() {
        new Message(VALID_RECIPIENT, "Test");
        List<Message> results = Message.searchByRecipient("");
        assertTrue(results.isEmpty());
    }
    
    @Test
    @Order(31)
    void testSearchByKeyword() {
        new Message(VALID_RECIPIENT, "Hello world");
        new Message(VALID_RECIPIENT, "Hello there");
        new Message(VALID_RECIPIENT, "Goodbye world");
        
        List<Message> results = Message.searchByKeyword("Hello");
        assertEquals(2, results.size());
        
        results = Message.searchByKeyword("world");
        assertEquals(2, results.size());
        
        results = Message.searchByKeyword("Goodbye");
        assertEquals(1, results.size());
        
        results = Message.searchByKeyword("nonexistent");
        assertEquals(0, results.size());
    }
    
    @Test
    @Order(32)
    void testSearchByKeywordNull() {
        new Message(VALID_RECIPIENT, "Test");
        List<Message> results = Message.searchByKeyword(null);
        assertTrue(results.isEmpty());
    }
    
    @Test
    @Order(33)
    void testSearchByKeywordCaseInsensitive() {
        new Message(VALID_RECIPIENT, "Hello World");
        
        List<Message> results = Message.searchByKeyword("hello");
        assertEquals(1, results.size());
        
        results = Message.searchByKeyword("HELLO");
        assertEquals(1, results.size());
        
        results = Message.searchByKeyword("world");
        assertEquals(1, results.size());
    }
    
    // ==================== DATE RANGE TESTS ====================
    
    @Test
    @Order(34)
    void testGetMessagesByDateRange() throws InterruptedException {
        Message msg1 = new Message(VALID_RECIPIENT, "First");
        Thread.sleep(10);
        long midTime = System.currentTimeMillis();
        Thread.sleep(10);
        Message msg2 = new Message(VALID_RECIPIENT, "Second");
        Thread.sleep(10);
        Message msg3 = new Message(VALID_RECIPIENT, "Third");
        
        List<Message> results = Message.getMessagesByDateRange(0, midTime);
        assertEquals(1, results.size());
        assertEquals(msg1.getMessageID(), results.get(0).getMessageID());
        
        results = Message.getMessagesByDateRange(midTime, Long.MAX_VALUE);
        assertEquals(2, results.size());
    }
    
    // ==================== RECENT MESSAGES TESTS ====================
    
    @Test
    @Order(35)
    void testGetRecentMessages() {
        Message msg1 = new Message(VALID_RECIPIENT, "First");
        Message msg2 = new Message(VALID_RECIPIENT, "Second");
        Message msg3 = new Message(VALID_RECIPIENT, "Third");
        Message msg4 = new Message(VALID_RECIPIENT, "Fourth");
        Message msg5 = new Message(VALID_RECIPIENT, "Fifth");
        
        List<Message> recent = Message.getRecentMessages(3);
        assertEquals(3, recent.size());
        assertEquals(msg3.getMessageID(), recent.get(0).getMessageID());
        assertEquals(msg4.getMessageID(), recent.get(1).getMessageID());
        assertEquals(msg5.getMessageID(), recent.get(2).getMessageID());
        
        recent = Message.getRecentMessages(10);
        assertEquals(5, recent.size());
        
        recent = Message.getRecentMessages(0);
        assertTrue(recent.isEmpty());
        
        recent = Message.getRecentMessages(-1);
        assertTrue(recent.isEmpty());
    }
    
    // ==================== SAMPLE MESSAGES TESTS ====================
    
    @Test
    @Order(36)
    void testCreateSampleMessages() {
        Message.createSampleMessages(3);
        
        assertEquals(3, Message.getTotalMessagesSent());
        List<Message> messages = Message.getAllMessages();
        
        for (Message msg : messages) {
            assertNotNull(msg.getRecipient());
            assertNotNull(msg.getContent());
            assertTrue(msg.getContentLength() > 0);
        }
    }
    
    @Test
    @Order(37)
    void testCreateSampleMessagesZero() {
        Message.createSampleMessages(0);
        assertEquals(0, Message.getTotalMessagesSent());
    }
    
    // ==================== INTEGRITY REPORT TESTS ====================
    
    @Test
    @Order(38)
    void testVerifyAllMessagesIntegrity() {
        Message msg1 = new Message(VALID_RECIPIENT, "Valid message 1");
        Message msg2 = new Message(VALID_RECIPIENT, "Valid message 2");
        
        Message.IntegrityReport report = Message.verifyAllMessagesIntegrity();
        assertEquals(2, report.validCount);
        assertEquals(0, report.invalidCount);
        assertTrue(report.invalidMessages.isEmpty());
    }
    
    // ==================== UTILITY METHOD TESTS ====================
    
    @Test
    @Order(39)
    void testGetPreview() {
        String shortContent = "Short message";
        Message shortMsg = new Message(VALID_RECIPIENT, shortContent);
        assertEquals(shortContent, shortMsg.getPreview());
        
        String longContent = "This is a very long message that should be truncated to 30 characters";
        Message longMsg = new Message(VALID_RECIPIENT, longContent);
        String preview = longMsg.getPreview();
        assertEquals(30, preview.length());
        assertTrue(preview.endsWith("..."));
    }
    
    @Test
    @Order(40)
    void testToString() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        String toString = message.toString();
        
        assertTrue(toString.contains("Message{"));
        assertTrue(toString.contains("id='"));
        assertTrue(toString.contains("to='"));
        assertTrue(toString.contains("content='"));
        assertTrue(toString.contains("hash='"));
        assertTrue(toString.contains("#"));
    }
    
    @Test
    @Order(41)
    void testEqualsAndHashCode() {
        Message msg1 = new Message(VALID_RECIPIENT, "Content");
        Message msg2 = new Message(VALID_RECIPIENT, "Different content");
        Message msg3 = Message.findMessageByID(msg1.getMessageID());
        
        assertEquals(msg1, msg1);
        assertEquals(msg1, msg3);
        assertNotEquals(msg1, msg2);
        assertNotEquals(null, msg1);
        assertNotEquals(msg1, "String");
        
        assertEquals(msg1.hashCode(), msg3.hashCode());
        assertNotEquals(msg1.hashCode(), msg2.hashCode());
    }
    
    // ==================== LOAD/EXPORT TESTS ====================
    
    @Test
    @Order(42)
    void testExportAndLoadMessages() {
        Message msg1 = new Message(VALID_RECIPIENT, "Message 1");
        Message msg2 = new Message("+1987654321", "Message 2");
        
        List<Object[]> exportData = Message.exportMessages();
        assertEquals(2, exportData.size());
        
        Message.clearAllMessages();
        assertEquals(0, Message.getTotalMessagesSent());
        
        Message.loadMessages(exportData);
        assertEquals(2, Message.getTotalMessagesSent());
        
        List<Message> loadedMessages = Message.getAllMessages();
        assertEquals(msg1.getMessageID(), loadedMessages.get(0).getMessageID());
        assertEquals(msg2.getMessageID(), loadedMessages.get(1).getMessageID());
    }
    
    @Test
    @Order(43)
    void testLoadEmptyMessages() {
        List<Object[]> emptyData = new ArrayList<>();
        Message.loadMessages(emptyData);
        
        assertEquals(0, Message.getTotalMessagesSent());
        assertTrue(Message.getAllMessages().isEmpty());
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    @Order(44)
    void testMessageWithSpecialCharacters() {
        String specialContent = "Hello! @#$%^&*()_+{}:\"<>?";
        Message message = new Message(VALID_RECIPIENT, specialContent);
        
        assertEquals(specialContent, message.getContent());
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(45)
    void testMessageWithUnicode() {
        String unicodeContent = "Hello 世界 🌍";
        Message message = new Message(VALID_RECIPIENT, unicodeContent);
        
        assertEquals(unicodeContent, message.getContent());
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(46)
    void testMessageWithNewlines() {
        String multilineContent = "Line 1\nLine 2\nLine 3";
        Message message = new Message(VALID_RECIPIENT, multilineContent);
        
        assertEquals(multilineContent, message.getContent());
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(47)
    void testLargeNumberOfMessages() {
        int messageCount = 100;
        for (int i = 0; i < messageCount; i++) {
            new Message(VALID_RECIPIENT, "Message " + i);
        }
        
        assertEquals(messageCount, Message.getTotalMessagesSent());
        assertEquals(messageCount, Message.getAllMessages().size());
    }
    
    @Test
    @Order(48)
    void testDisplayMethodsDoNotThrowExceptions() {
        Message message = new Message(VALID_RECIPIENT, VALID_CONTENT);
        
        assertDoesNotThrow(() -> message.displayMessage());
        assertDoesNotThrow(() -> Message.displayAllMessages());
        assertDoesNotThrow(() -> Message.displayStatistics());
    }
    
    @Test
    @Order(49)
    void testIntegrityAfterMultipleModifications() {
        Message message = new Message(VALID_RECIPIENT, "Original");
        
        message.setContent("Modified 1");
        assertTrue(message.verifyIntegrity());
        
        message.setContent("Modified 2");
        assertTrue(message.verifyIntegrity());
        
        message.setContent("Modified 3");
        assertTrue(message.verifyIntegrity());
        
        message.setRecipient("+19998887777");
        assertTrue(message.verifyIntegrity());
    }
    
    @Test
    @Order(50)
    void testConcurrentMessageCreation() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                new Message(VALID_RECIPIENT, "Thread 1 message " + i);
            }
        });
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                new Message(VALID_RECIPIENT, "Thread 2 message " + i);
            }
        });
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        assertEquals(100, Message.getTotalMessagesSent());
    }
}