/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chatapp;

/**
 *
 * @author 27723
 */
/**
 * Represents a message in the chat application.
 */
public class Message {

    private String messagePayload;
    private boolean messageSent;
    private boolean messageReceived;
    private boolean messageRead;

    /**
     * Constructs a message with a payload.
     *
     * @param messagePayload the actual message text
     */
    public Message(String messagePayload) {
        this.messagePayload = messagePayload;
        this.messageSent = false;
        this.messageReceived = false;
        this.messageRead = false;
    }

    public String getMessagePayload() {
        return messagePayload;
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

    public void setMessageSent(boolean messageSent) {
        this.messageSent = messageSent;
    }

    public void setMessageReceived(boolean messageReceived) {
        this.messageReceived = messageReceived;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }
}
