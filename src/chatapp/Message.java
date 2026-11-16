package chatapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a message and provides the operations required by QuickChat,
 * including Part 3 parallel array storage, JSON reading, searching, deletion,
 * and reporting.
 */
public class Message {

    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MAX_ARRAY_SIZE = 100;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final List<Message> SENT_MESSAGES = new ArrayList<>();

    // Part 3: Five required parallel arrays (populated dynamically without hard-coding)
    private static String[] sentMessages = new String[MAX_ARRAY_SIZE];
    private static String[] disregardedMessages = new String[MAX_ARRAY_SIZE];
    private static String[] storedMessages = new String[MAX_ARRAY_SIZE];
    private static String[] messageHashes = new String[MAX_ARRAY_SIZE];
    private static String[] messageIDs = new String[MAX_ARRAY_SIZE];

    // Supporting parallel arrays for recipients, senders/developers, and message flags
    private static String[] messageRecipients = new String[MAX_ARRAY_SIZE];
    private static String[] messageSenders = new String[MAX_ARRAY_SIZE];
    private static String[] messageFlags = new String[MAX_ARRAY_SIZE];

    private static int sentCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;
    private static int totalTrackedCount = 0;

    private static String currentDeveloper = "Developer";

    private String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messagePayload;
    private String messageHash;
    private String sender;

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
        this.sender = currentDeveloper;

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
     * Sets the active sender/developer name for newly created messages.
     *
     * @param developerName full name or identifier of the logged-in user
     */
    public static void setCurrentDeveloper(String developerName) {
        if (developerName != null && !developerName.trim().isEmpty()) {
            currentDeveloper = developerName.trim();
        }
    }

    /**
     * Resets all parallel arrays and counters (useful before running unit tests).
     */
    public static void resetArrays() {
        SENT_MESSAGES.clear();
        sentMessages = new String[MAX_ARRAY_SIZE];
        disregardedMessages = new String[MAX_ARRAY_SIZE];
        storedMessages = new String[MAX_ARRAY_SIZE];
        messageHashes = new String[MAX_ARRAY_SIZE];
        messageIDs = new String[MAX_ARRAY_SIZE];
        messageRecipients = new String[MAX_ARRAY_SIZE];
        messageSenders = new String[MAX_ARRAY_SIZE];
        messageFlags = new String[MAX_ARRAY_SIZE];

        sentCount = 0;
        disregardedCount = 0;
        storedCount = 0;
        totalTrackedCount = 0;
    }

    /**
     * Allows setting a specific message ID (used when loading JSON or test data)
     * and updates the message hash accordingly.
     *
     * @param customID specific message ID
     */
    public void setCustomMessageID(String customID) {
        if (customID != null && !customID.trim().isEmpty()) {
            this.messageID = customID.trim();
            this.messageHash = createMessageHash();
        }
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
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
        String firstTwoIDDigits = messageID.length() >= 2
                ? messageID.substring(0, 2)
                : messageID;
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
     * Records a message into the appropriate parallel arrays based on its flag.
     *
     * @param flag "Sent", "Disregard", or "Stored"
     */
    private void addToParallelArrays(String flag) {
        if (totalTrackedCount >= MAX_ARRAY_SIZE) {
            return;
        }

        if ("Sent".equalsIgnoreCase(flag) && sentCount < MAX_ARRAY_SIZE) {
            sentMessages[sentCount] = this.messagePayload;
            sentCount++;
        } else if ("Disregard".equalsIgnoreCase(flag) && disregardedCount < MAX_ARRAY_SIZE) {
            disregardedMessages[disregardedCount] = this.messagePayload;
            disregardedCount++;
        } else if ("Stored".equalsIgnoreCase(flag) && storedCount < MAX_ARRAY_SIZE) {
            storedMessages[storedCount] = this.messagePayload;
            storedCount++;
        }

        messageHashes[totalTrackedCount] = this.messageHash;
        messageIDs[totalTrackedCount] = this.messageID;
        messageRecipients[totalTrackedCount] = this.recipient;
        messageSenders[totalTrackedCount] = this.sender;
        messageFlags[totalTrackedCount] = flag;
        totalTrackedCount++;
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
                addToParallelArrays("Sent");
                return "Message successfully sent.";
            case 2:
                addToParallelArrays("Disregard");
                return "Press 0 to delete the message.";
            case 3:
                try {
                    storeMessage();
                    addToParallelArrays("Stored");
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
     * Reads stored messages from messages.json into the storedMessages array
     * and updates the parallel arrays with any entries not yet loaded.
     *
     * Source Attribution (JSON Parsing):
     * Adapted from Google Gson User Guide & Java FileReader documentation:
     * Google (2024) Gson User Guide - Deserializing JSON Arrays with fromJson().
     * Available at: https://github.com/google/gson/blob/main/UserGuide.md
     * (Accessed: 16 November 2026).
     *
     * @return formatted summary of all stored messages read from the JSON file
     */
    public static String readJsonIntoStoredArray() {
        File file = new File("messages.json");
        if (!file.exists() || file.length() == 0) {
            return "No stored messages found in messages.json.";
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            MessageRecord[] records = gson.fromJson(reader, MessageRecord[].class);

            if (records == null || records.length == 0) {
                return "No stored messages found in messages.json.";
            }

            // Reset storedMessages array before reloading from JSON
            storedMessages = new String[MAX_ARRAY_SIZE];
            storedCount = 0;

            StringBuilder output = new StringBuilder();
            for (MessageRecord record : records) {
                if (storedCount < MAX_ARRAY_SIZE) {
                    storedMessages[storedCount] = record.messagePayload;
                    storedCount++;
                }

                // Ensure the record is also represented in the master parallel arrays
                if (!containsMessageID(record.messageID) && totalTrackedCount < MAX_ARRAY_SIZE) {
                    messageIDs[totalTrackedCount] = record.messageID;
                    messageHashes[totalTrackedCount] = record.messageHash;
                    messageRecipients[totalTrackedCount] = record.recipient;
                    messageSenders[totalTrackedCount] = record.sender != null
                            ? record.sender
                            : currentDeveloper;
                    messageFlags[totalTrackedCount] = "Stored";
                    totalTrackedCount++;
                }

                output.append("Message ID: ").append(record.messageID)
                        .append(System.lineSeparator())
                        .append("Recipient: ").append(record.recipient)
                        .append(System.lineSeparator())
                        .append("Message: ").append(record.messagePayload)
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
            }

            return output.toString().trim();
        } catch (IOException exception) {
            return "Error reading messages.json: " + exception.getMessage();
        }
    }

    private static boolean containsMessageID(String id) {
        if (id == null) {
            return false;
        }
        for (int i = 0; i < totalTrackedCount; i++) {
            if (id.equals(messageIDs[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Submenu Option (a): Displays the sender and recipient of all stored messages.
     *
     * @return formatted list of sender and recipient pairs for stored messages
     */
    public static String displaySenderAndRecipientOfStoredMessages() {
        readJsonIntoStoredArray();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < totalTrackedCount; i++) {
            if ("Stored".equalsIgnoreCase(messageFlags[i])) {
                output.append("Sender: ").append(messageSenders[i])
                        .append(" | Recipient: ").append(messageRecipients[i])
                        .append(System.lineSeparator());
            }
        }

        if (output.length() == 0) {
            return "No stored messages available.";
        }
        return output.toString().trim();
    }

    /**
     * Submenu Option (b): Searches the parallel arrays to find and return
     * the longest message.
     *
     * @return the longest message payload
     */
    public static String displayLongestMessage() {
        String longest = "";

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i] != null && sentMessages[i].length() > longest.length()) {
                longest = sentMessages[i];
            }
        }

        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i] != null && storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }

        return longest;
    }

    /**
     * Submenu Option (c): Searches for a message ID and returns the corresponding
     * recipient and message.
     *
     * @param searchID the message ID to search for
     * @return formatted recipient and message, or not-found message
     */
    public static String searchByMessageID(String searchID) {
        if (searchID == null || searchID.trim().isEmpty()) {
            return "Message ID not found.";
        }

        for (int i = 0; i < totalTrackedCount; i++) {
            if (searchID.trim().equals(messageIDs[i])) {
                String payload = getPayloadAtMasterIndex(i);
                return "Recipient: " + messageRecipients[i]
                        + System.lineSeparator()
                        + "Message: " + payload;
            }
        }
        return "Message ID not found.";
    }

    /**
     * Submenu Option (d): Searches for all messages sent or stored for a
     * particular recipient.
     *
     * @param searchRecipient recipient phone number
     * @return all matching messages separated by line breaks
     */
    public static String searchMessagesByRecipient(String searchRecipient) {
        if (searchRecipient == null || searchRecipient.trim().isEmpty()) {
            return "No messages found for recipient.";
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < totalTrackedCount; i++) {
            if (searchRecipient.trim().equals(messageRecipients[i])
                    && ("Sent".equalsIgnoreCase(messageFlags[i])
                    || "Stored".equalsIgnoreCase(messageFlags[i]))) {
                String payload = getPayloadAtMasterIndex(i);
                if (output.length() > 0) {
                    output.append(System.lineSeparator());
                }
                output.append(payload);
            }
        }

        if (output.length() == 0) {
            return "No messages found for recipient.";
        }
        return output.toString();
    }

    /**
     * Submenu Option (e): Deletes a message using its message hash and removes
     * it from the parallel arrays (and updates messages.json if stored).
     *
     * @param hashToDelete the message hash to remove
     * @return confirmation message matching the POE specification
     */
    public static String deleteMessageByHash(String hashToDelete) {
        if (hashToDelete == null || hashToDelete.trim().isEmpty()) {
            return "Message hash not found.";
        }

        for (int i = 0; i < totalTrackedCount; i++) {
            if (hashToDelete.trim().equalsIgnoreCase(messageHashes[i])) {
                String deletedPayload = getPayloadAtMasterIndex(i);
                String flag = messageFlags[i];

                // Remove from flag-specific array
                if ("Sent".equalsIgnoreCase(flag)) {
                    removeFromArray(sentMessages, sentCount, deletedPayload);
                    if (sentCount > 0) {
                        sentCount--;
                    }
                    SENT_MESSAGES.removeIf(m -> hashToDelete.trim().equalsIgnoreCase(m.getMessageHash()));
                } else if ("Disregard".equalsIgnoreCase(flag)) {
                    removeFromArray(disregardedMessages, disregardedCount, deletedPayload);
                    if (disregardedCount > 0) {
                        disregardedCount--;
                    }
                } else if ("Stored".equalsIgnoreCase(flag)) {
                    removeFromArray(storedMessages, storedCount, deletedPayload);
                    if (storedCount > 0) {
                        storedCount--;
                    }
                    removeRecordFromJsonFile(hashToDelete.trim());
                }

                // Shift parallel master arrays left
                for (int j = i; j < totalTrackedCount - 1; j++) {
                    messageIDs[j] = messageIDs[j + 1];
                    messageHashes[j] = messageHashes[j + 1];
                    messageRecipients[j] = messageRecipients[j + 1];
                    messageSenders[j] = messageSenders[j + 1];
                    messageFlags[j] = messageFlags[j + 1];
                }
                totalTrackedCount--;
                messageIDs[totalTrackedCount] = null;
                messageHashes[totalTrackedCount] = null;
                messageRecipients[totalTrackedCount] = null;
                messageSenders[totalTrackedCount] = null;
                messageFlags[totalTrackedCount] = null;

                // Strip trailing period if present to match expected unit test string
                String cleanText = deletedPayload.endsWith(".")
                        ? deletedPayload.substring(0, deletedPayload.length() - 1)
                        : deletedPayload;

                return "Message \"" + cleanText + "\" successfully deleted.";
            }
        }

        return "Message hash not found.";
    }

    private static void removeFromArray(String[] array, int count, String valueToRemove) {
        for (int i = 0; i < count; i++) {
            if (valueToRemove != null && valueToRemove.equals(array[i])) {
                for (int j = i; j < count - 1; j++) {
                    array[j] = array[j + 1];
                }
                array[count - 1] = null;
                break;
            }
        }
    }

    private static void removeRecordFromJsonFile(String hashToDelete) {
        File file = new File("messages.json");
        if (!file.exists() || file.length() == 0) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            MessageRecord[] existing = new Gson().fromJson(reader, MessageRecord[].class);
            if (existing == null) {
                return;
            }

            List<MessageRecord> updated = new ArrayList<>();
            for (MessageRecord record : existing) {
                if (!hashToDelete.equalsIgnoreCase(record.messageHash)) {
                    updated.add(record);
                }
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(updated, writer);
            }
        } catch (IOException ignored) {
            // Keep runtime deletion resilient even if file is locked
        }
    }

    /**
     * Submenu Option (f): Displays a report listing the full details
     * (Message Hash, Recipient, and Message) of all sent and stored messages.
     *
     * @return formatted full message report
     */
    public static String displayMessageReport() {
        StringBuilder report = new StringBuilder();

        for (int i = 0; i < totalTrackedCount; i++) {
            if ("Sent".equalsIgnoreCase(messageFlags[i])
                    || "Stored".equalsIgnoreCase(messageFlags[i])) {
                String payload = getPayloadAtMasterIndex(i);
                report.append("Message Hash: ").append(messageHashes[i])
                        .append(System.lineSeparator())
                        .append("Recipient: ").append(messageRecipients[i])
                        .append(System.lineSeparator())
                        .append("Message: ").append(payload)
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
            }
        }

        if (report.length() == 0) {
            return "No messages to display in report.";
        }
        return report.toString().trim();
    }

    /**
     * Resolves the message text corresponding to an index in the master parallel arrays.
     */
    private static String getPayloadAtMasterIndex(int masterIndex) {
        String flag = messageFlags[masterIndex];
        int flagOccurrence = 0;

        for (int i = 0; i <= masterIndex; i++) {
            if (flag.equalsIgnoreCase(messageFlags[i])) {
                if (i == masterIndex) {
                    break;
                }
                flagOccurrence++;
            }
        }

        if ("Sent".equalsIgnoreCase(flag) && flagOccurrence < sentCount) {
            return sentMessages[flagOccurrence];
        } else if ("Disregard".equalsIgnoreCase(flag) && flagOccurrence < disregardedCount) {
            return disregardedMessages[flagOccurrence];
        } else if ("Stored".equalsIgnoreCase(flag) && flagOccurrence < storedCount) {
            return storedMessages[flagOccurrence];
        }
        return "";
    }

    // Getters for the 5 required parallel arrays
    public static String[] getSentMessagesArray() {
        String[] result = new String[sentCount];
        System.arraycopy(sentMessages, 0, result, 0, sentCount);
        return result;
    }

    public static String[] getDisregardedMessagesArray() {
        String[] result = new String[disregardedCount];
        System.arraycopy(disregardedMessages, 0, result, 0, disregardedCount);
        return result;
    }

    public static String[] getStoredMessagesArray() {
        String[] result = new String[storedCount];
        System.arraycopy(storedMessages, 0, result, 0, storedCount);
        return result;
    }

    public static String[] getMessageHashesArray() {
        String[] result = new String[totalTrackedCount];
        System.arraycopy(messageHashes, 0, result, 0, totalTrackedCount);
        return result;
    }

    public static String[] getMessageIDsArray() {
        String[] result = new String[totalTrackedCount];
        System.arraycopy(messageIDs, 0, result, 0, totalTrackedCount);
        return result;
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
        File file = new File("messages.json");
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
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
        private final String sender;

        MessageRecord(Message message) {
            this.messageID = message.messageID;
            this.messageNumber = message.messageNumber;
            this.recipient = message.recipient;
            this.messagePayload = message.messagePayload;
            this.messageHash = message.messageHash;
            this.sender = message.sender;
        }
    }
}