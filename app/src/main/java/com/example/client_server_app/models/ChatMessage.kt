package com.example.client_server_app.models

import com.example.client_server_app.listeners.UserListener
import java.util.Date
/**
 * A class representing a chat message in the application.
 * @property ChatMessage the name of this class.
 */
class ChatMessage {
    /**
     * The unique identifier of the message sender.
     */
    lateinit var senderId: String
    /**
     * The unique identifier of the message receiver.
     */
    lateinit var receiverId: String
    /**
     * The content of the chat message.
     */
    lateinit var message: String
    /**
     * The date and time when the message was sent.
     */
    lateinit var dateTime: String
    /**
     * The date and time of the message as a Date object.
     */
    lateinit var dateObject: Date
    /**
     * The unique identifier of the conversation associated with the message.
     */
    lateinit var conversionId: String
    /**
     * The name of the conversation associated with the message.
     */
    lateinit var conversionName: String
    /**
     * The image associated with the conversation.
     */
    lateinit var conversionImage: String
}