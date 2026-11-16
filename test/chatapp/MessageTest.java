package chatapp;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for Part 2 and Part 3 message functionality.
 */
public class MessageTest {

    private Message msg1;
    private Message msg2;
    private Message msg3;
    private Message msg4;
    private Message msg5;

    @Before
    public void setUp() {
        Message.resetArrays();
        File jsonFile = new File("messages.json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }

        // Test Data Message 1
        msg1 = new Message(1, "+2783455789", "Did you get the cake?");
        msg1.sentMessage(1); // Flag: Sent

        // Test Data Message 2
        msg2 = new Message(2, "+2783888456", "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3); // Flag: Stored

        // Test Data Message 3
        msg3 = new Message(3, "+2783448456", "Yohoooo, I am at your gate.");
        msg3.sentMessage(2); // Flag: Disregard

        // Test Data Message 4
        msg4 = new Message(4, "+2783888456", "It is dinner time !");
        msg4.setCustomMessageID("0838884567");
        msg4.sentMessage(1); // Flag: Sent

        // Test Data Message 5
        msg5 = new Message(5, "+2783888456", "Ok, I am leaving without you.");
        msg5.sentMessage(3); // Flag: Stored
    }

    @After
    public void tearDown() {
        File jsonFile = new File("messages.json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    // ==================== PART 3 UNIT TESTS ====================

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        String[] expectedSent = {
            "Did you get the cake?",
            "It is dinner time !"
        };
        assertArrayEquals(expectedSent, Message.getSentMessagesArray());
    }

    @Test
    public void testDisplayLongestMessage() {
        assertEquals(
                "Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage()
        );
    }

    @Test
    public void testSearchForMessageID() {
        String expected = "Recipient: +2783888456"
                + System.lineSeparator()
                + "Message: It is dinner time !";
        assertEquals(expected, Message.searchByMessageID("0838884567"));
    }

    @Test
    public void testSearchMessagesByRecipient() {
        String expected = "Where are you? You are late! I have asked you to be on time."
                + System.lineSeparator()
                + "It is dinner time !"
                + System.lineSeparator()
                + "Ok, I am leaving without you.";
        assertEquals(expected, Message.searchMessagesByRecipient("+2783888456"));
    }

    @Test
    public void testDeleteMessageUsingHash() {
        String hashToDelete = msg2.getMessageHash();
        String expected = "Message \"Where are you? You are late! I have asked you to be on time\" successfully deleted.";
        assertEquals(expected, Message.deleteMessageByHash(hashToDelete));
    }

    @Test
    public void testDisplayReport() {
        String report = Message.displayMessageReport();
        assertTrue(report.contains(msg1.getMessageHash()));
        assertTrue(report.contains(msg1.getRecipient()));
        assertTrue(report.contains("Did you get the cake?"));

        assertTrue(report.contains(msg4.getMessageHash()));
        assertTrue(report.contains("It is dinner time !"));
    }

    @Test
    public void testReadJsonIntoStoredArray() {
        String jsonOutput = Message.readJsonIntoStoredArray();
        assertTrue(jsonOutput.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(jsonOutput.contains("Ok, I am leaving without you."));
        assertEquals(2, Message.getStoredMessagesArray().length);
    }

    // ==================== PART 2 UNIT TESTS ====================

    @Test
    public void testMessageIDIsTenDigitsOrFewer() {
        Message message = new Message(
                1,
                "+2783896897",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertTrue(message.checkMessageID());
        assertEquals(10, message.getMessageID().length());
    }

    @Test
    public void testMessageAt250CharactersIsAccepted() {
        String text = repeat('a', 250);
        Message message = new Message(1, "+2783896897", text);

        assertEquals(250, message.getMessagePayload().length());
    }

    @Test
    public void testMessageOver250CharactersIsRejected() {
        String text = repeat('a', 251);

        try {
            new Message(1, "+2783896897", text);
        } catch (IllegalArgumentException exception) {
            assertEquals(
                    "Message text cannot exceed 250 characters.",
                    exception.getMessage()
            );
            return;
        }

        throw new AssertionError(
                "Expected a message longer than 250 characters to be rejected."
        );
    }

    @Test
    public void testCorrectlyFormattedRecipient() {
        assertEquals(
                "Cell phone number successfully captured.",
                Message.checkRecipientCell("+2783896897")
        );
    }

    @Test
    public void testIncorrectlyFormattedRecipient() {
        assertEquals(
                "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number "
                + "and try again.",
                Message.checkRecipientCell("08966553")
        );
    }

    @Test
    public void testHashContainsRequiredComponents() {
        Message message = new Message(
                1,
                "+2783896897",
                "Hi Mike, can you join us for dinner tonight?"
        );

        String hash = message.getMessageHash();

        assertTrue(hash.matches(
                "^\\d{2}:1:HIMIKE.*TONIGHT$"
        ));
    }

    @Test
    public void testSendMessage() {
        Message message = new Message(
                1,
                "+2783896897",
                "Hi Mike, can you join us for dinner tonight?"
        );

        assertEquals(
                "Message successfully sent.",
                message.sentMessage(1)
        );
        assertTrue(message.isMessageSent());
        assertTrue(message.returnTotalMessages() >= 1);
    }

    @Test
    public void testDiscardMessage() {
        Message message = new Message(
                1,
                "+2783896897",
                "Hi Keegan, did you receive the payment?"
        );

        assertEquals(
                "Press 0 to delete the message.",
                message.sentMessage(2)
        );
        assertFalse(message.isMessageSent());
    }

    @Test
    public void testStoreMessageStatus() {
        Message message = new Message(
                1,
                "+2783896897",
                "Hi Keegan, did you receive the payment?"
        );

        assertEquals(
                "Message successfully stored.",
                message.sentMessage(3)
        );
        assertFalse(message.isMessageSent());
    }

    private String repeat(char character, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(character);
        }
        return result.toString();
    }
}