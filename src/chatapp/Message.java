package chatapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message and provides the operations required by QuickChat.
 */
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messagePayload;
    private final String messageHash;

    private boolean messageSent;
    private boolean messageReceived;
    private boolean messageRead;

    /**
     * Creates a message and assigns a random ten-digit ID.
     *
     * @param messageNumber sequence number for the sent message
     * @param recipient international recipient phone number
     * @param messagePayload message text, up to 250 characters
     */
    public Message(int messageNumber, String recipient, String messagePayload) {
        if (messageNumber < 0) {
            throw new IllegalArgumentException(
                    "The message number cannot be negative."
            );
        }
        if (recipient == null || !isRecipientValid(recipient)) {
            throw new IllegalArgumentException(
                    "Recipient number is incorrectly formatted."
            );
        }
        if (messagePayload == null || messagePayload.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Message text cannot be empty."
            );
        }
        if (messagePayload.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                    "Message text cannot exceed 250 characters."
            );
        }

        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messagePayload = messagePayload;

        // Generate exactly ten digits, allowing a leading zero.
        this.messageID = String.format(
                "%010d",
                RANDOM.nextInt(1_000_000_000)
        );
        this.messageHash = createMessageHash();

        this.messageSent = false;
        this.messageReceived = false;
        this.messageRead = false;
    }

    /**
     * Checks that the ID contains no more than ten characters.
     *
     * @return true if the ID is present and at most ten characters long
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Checks that the recipient starts with '+' and has no more than ten
     * digits after the plus sign.
     *
     * @param cellNumber phone number to validate
     * @return true if correctly formatted
     */
    public static boolean isRecipientValid(String cellNumber) {
        return cellNumber != null
                && cellNumber.matches("^\\+\\d{1,10}$");
    }

    /**
     * Returns the task's required recipient-validation message.
     *
     * @param cellNumber phone number to validate
     * @return success or failure message
     */
    public static String checkRecipientCell(String cellNumber) {
        if (isRecipientValid(cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number "
                + "and try again.";
    }

    /**
     * Creates a hash from the first two ID digits, sequence number,
     * and the first two words and last word of the message.
     *
     * @return hash in uppercase
     */
    public String createMessageHash() {
        String firstTwoIDDigits = messageID.substring(0, 2);
        String[] words = messagePayload.trim().split("\\s+");

        String firstWord = cleanWord(words[0]);
        String secondWord = words.length > 1 ? cleanWord(words[1]) : "";
        String lastWord = cleanWord(words[words.length - 1]);

        return (firstTwoIDDigits + ":"
                + messageNumber + ":"
                + firstWord + secondWord + lastWord).toUpperCase();
    }

    private String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Sends, discards or stores this message according to the selected option.
     *
     * @param option 1 to send, 2 to discard, or 3 to store
     * @return task-required status message
     */
    public String sentMessage(int option) {
        switch (option) {
            case 1:
                messageSent = true;
                SENT_MESSAGES.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                try {
                    storeMessage();
                    return "Message successfully stored.";
                } catch (IOException exception) {
                    return "Message could not be stored: "
                            + exception.getMessage();
                }
            default:
                return "Invalid message option.";
        }
    }

    /**
     * Returns a display of all messages sent during this run.
     *
     * @return formatted sent-message details
     */
    public String printMessages() {
        StringBuilder output = new StringBuilder();

        for (Message message : SENT_MESSAGES) {
            output.append("Message ID: ")
                    .append(message.getMessageID())
                    .append(System.lineSeparator())
                    .append("Message Hash: ")
                    .append(message.getMessageHash())
                    .append(System.lineSeparator())
                    .append("Recipient: ")
                    .append(message.getRecipient())
                    .append(System.lineSeparator())
                    .append("Message: ")
                    .append(message.getMessagePayload())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }

        return output.toString();
    }

    /**
     * Returns the number of messages sent in the current run.
     *
     * @return total sent-message count
     */
    public int returnTotalMessages() {
        return SENT_MESSAGES.size();
    }

    /**
     * Appends this message to messages.json as a JSON object.
     *
     * @throws IOException if the file cannot be written
     */
    public void storeMessage() throws IOException {
        List<MessageRecord> records = new ArrayList<>();

        // Read existing JSON first so storing another message preserves it.
        java.io.File file = new java.io.File("messages.json");
        if (file.exists() && file.length() > 0) {
            try (java.io.FileReader reader = new java.io.FileReader(file)) {
                MessageRecord[] existing = new Gson().fromJson(
                        reader, MessageRecord[].class
                );
                if (existing != null) {
                    for (MessageRecord record : existing) {
                        records.add(record);
                    }
                }
            }
        }

        records.add(new MessageRecord(this));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(records, writer);
        }
    }

    public String getMessageID() {
        return messageID;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public boolean isMessageSent() {
        return messageSent;
    }

    public boolean isMessageReceived() {
        return messageReceived;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageReceived(boolean messageReceived) {
        this.messageReceived = messageReceived;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    /**
     * A simple data-transfer class for JSON serialisation.
     */
    private static class MessageRecord {
        private final String messageID;
        private final int messageNumber;
        private final String recipient;
        private final String messagePayload;
        private final String messageHash;

        MessageRecord(Message message) {
            this.messageID = message.messageID;
            this.messageNumber = message.messageNumber;
            this.recipient = message.recipient;
            this.messagePayload = message.messagePayload;
            this.messageHash = message.messageHash;
        }
    }
}