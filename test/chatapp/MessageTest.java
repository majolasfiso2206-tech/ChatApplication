package chatapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for Part 2 message functionality.
 */
public class MessageTest {

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