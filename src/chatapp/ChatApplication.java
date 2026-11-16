package chatapp;

import java.util.Scanner;

/**
 * Runs registration, login and the QuickChat messaging menu,
 * including Part 3 Stored Messages & Reporting options.
 */
public class ChatApplication {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Java Chat Application ===");

        System.out.print("Enter your first name: ");
        String firstName = SCANNER.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = SCANNER.nextLine();

        Login login = new Login(firstName, lastName);
        Message.setCurrentDeveloper(firstName + " " + lastName);

        System.out.println("\n=== Registration ===");

        System.out.print("Enter a username: ");
        String username = SCANNER.nextLine();

        System.out.print("Enter a password: ");
        String password = SCANNER.nextLine();

        System.out.print("Enter a South African cell-phone number "
                + "(include international code): ");
        String registrationCell = SCANNER.nextLine();

        String registrationMessage = login.registerUser(
                username,
                password,
                registrationCell
        );
        System.out.println("\n" + registrationMessage);

        if (login.getUsername() == null) {
            System.out.println(
                    "\nRegistration was unsuccessful. "
                    + "Please correct the supplied details."
            );
            SCANNER.close();
            return;
        }

        System.out.println("\n=== Login ===");

        System.out.print("Enter your username: ");
        String loginUsername = SCANNER.nextLine();

        System.out.print("Enter your password: ");
        String loginPassword = SCANNER.nextLine();

        boolean loggedIn = login.loginUser(loginUsername, loginPassword);
        System.out.println(
                login.returnLoginStatus(loginUsername, loginPassword)
        );

        if (!loggedIn) {
            // The user cannot access messaging unless authentication succeeds.
            SCANNER.close();
            return;
        }

        System.out.println("\nWelcome to QuickChat.");

        // Load any previously stored JSON messages into the storedMessages array
        Message.readJsonIntoStoredArray();

        int messageLimit = readPositiveInt(
                "How many messages would you like to enter? "
        );

        runMenu(messageLimit);
        SCANNER.close();
    }

    private static void runMenu(int messageLimit) {
        int messagesEntered = 0;
        int nextMessageNumber = 1;
        boolean running = true;

        while (running) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Enter your choice: ");

            String choice = SCANNER.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesEntered >= messageLimit) {
                        System.out.println(
                                "You have entered the maximum number of "
                                + "messages selected at the start."
                        );
                        break;
                    }

                    MessageResult result = composeMessage(nextMessageNumber);
                    if (result.messageWasEntered) {
                        messagesEntered++;
                    }
                    if (result.messageWasSent) {
                        nextMessageNumber++;
                    }
                    break;

                case "2":
                    Message helper = new Message(0, "+2700000000", "Placeholder");
                    String recent = helper.printMessages();
                    if (recent.isEmpty()) {
                        System.out.println("No messages have been sent yet.");
                    } else {
                        System.out.println("\n=== Recently Sent Messages ===");
                        System.out.print(recent);
                    }
                    break;

                case "3":
                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;

                case "4":
                    runStoredMessagesSubmenu();
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please enter 1, 2, 3 or 4."
                    );
            }
        }
    }

    /**
     * Runs the fourth main menu option ("Stored Messages") with sub-options a to f.
     */
    private static void runStoredMessagesSubmenu() {
        boolean inSubmenu = true;

        while (inSubmenu) {
            System.out.println("\n=== Stored Messages & Reports ===");
            System.out.println("a) Display the sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID and display the corresponding recipient and message");
            System.out.println("d) Search for all the messages stored for a particular recipient");
            System.out.println("e) Delete a message using the message hash");
            System.out.println("f) Display a report that lists the full details of all the stored messages");
            System.out.println("g) Read JSON file into Stored Messages array and display");
            System.out.println("x) Return to main menu");
            System.out.print("Enter your choice: ");

            String subChoice = SCANNER.nextLine().trim().toLowerCase();

            switch (subChoice) {
                case "a":
                    System.out.println("\n" + Message.displaySenderAndRecipientOfStoredMessages());
                    break;

                case "b":
                    String longest = Message.displayLongestMessage();
                    if (longest.isEmpty()) {
                        System.out.println("\nNo messages available.");
                    } else {
                        System.out.println("\nLongest message: " + longest);
                    }
                    break;

                case "c":
                    System.out.print("Enter the Message ID to search: ");
                    String searchID = SCANNER.nextLine().trim();
                    System.out.println("\n" + Message.searchByMessageID(searchID));
                    break;

                case "d":
                    System.out.print("Enter the recipient phone number to search: ");
                    String searchRecipient = SCANNER.nextLine().trim();
                    System.out.println("\n" + Message.searchMessagesByRecipient(searchRecipient));
                    break;

                case "e":
                    System.out.print("Enter the Message Hash to delete: ");
                    String hashToDelete = SCANNER.nextLine().trim();
                    System.out.println("\n" + Message.deleteMessageByHash(hashToDelete));
                    break;

                case "f":
                    System.out.println("\n=== Full Message Report ===");
                    System.out.println(Message.displayMessageReport());
                    break;

                case "g":
                    System.out.println("\n=== Stored Messages Loaded from JSON ===");
                    System.out.println(Message.readJsonIntoStoredArray());
                    break;

                case "x":
                    inSubmenu = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please select a-g or x.");
            }
        }
    }

    private static MessageResult composeMessage(int messageNumber) {
        String recipient;

        while (true) {
            System.out.print("Enter the recipient's international number: ");
            recipient = SCANNER.nextLine().trim();

            String validationMessage =
                    Message.checkRecipientCell(recipient);
            System.out.println(validationMessage);

            if (Message.isRecipientValid(recipient)) {
                break;
            }
        }

        String messageText;
        while (true) {
            System.out.print("Enter your message (maximum 250 characters): ");
            messageText = SCANNER.nextLine();

            if (messageText.length() <= 250 && !messageText.trim().isEmpty()) {
                System.out.println("Message ready to send.");
                break;
            }

            int excess = Math.max(0, messageText.length() - 250);
            System.out.println(
                    "Message exceeds 250 characters by "
                    + excess
                    + "; please reduce the size."
            );
        }

        Message message = new Message(
                messageNumber,
                recipient,
                messageText
        );

        System.out.println("Message ID generated: "
                + message.getMessageID());
        System.out.println("Message Hash: "
                + message.getMessageHash());

        System.out.println("\nChoose what to do with this message:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Enter your choice: ");

        int action = readIntInRange(1, 3);
        String status = message.sentMessage(action);
        System.out.println(status);

        if (action == 1) {
            System.out.println("\nMessage details:");
            System.out.println("Message ID: " + message.getMessageID());
            System.out.println("Message Hash: " + message.getMessageHash());
            System.out.println("Recipient: " + message.getRecipient());
            System.out.println("Message: " + message.getMessagePayload());
            System.out.println("Total messages sent: "
                    + message.returnTotalMessages());
        } else if (action == 2) {
            System.out.print("Enter 0 to delete this message: ");
            String deleteChoice = SCANNER.nextLine().trim();
            if ("0".equals(deleteChoice)) {
                System.out.println("Message deleted.");
            }
        }

        return new MessageResult(true, action == 1);
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // The message below asks the user to enter a valid number.
            }

            System.out.println("Please enter a whole number greater than 0.");
        }
    }

    private static int readIntInRange(int minimum, int maximum) {
        while (true) {
            String input = SCANNER.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException exception) {
                // The message below prompts again.
            }

            System.out.print("Please enter a number from "
                    + minimum + " to " + maximum + ": ");
        }
    }

    private static class MessageResult {
        private final boolean messageWasEntered;
        private final boolean messageWasSent;

        MessageResult(boolean messageWasEntered, boolean messageWasSent) {
            this.messageWasEntered = messageWasEntered;
            this.messageWasSent = messageWasSent;
        }
    }
}