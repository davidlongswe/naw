package com.umu.se.dalo0013.naw.data;

import java.util.Date;
/**
 * ChatMessage - a chat message object used for displaying chat messages
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;

    /**
     * ChatMessage constructor - including time message was sent
     * @param messageText the message contents
     * @param messageUser the message sender name
     */
    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}

