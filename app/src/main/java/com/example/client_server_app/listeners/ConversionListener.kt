package com.example.client_server_app.listeners

import com.example.client_server_app.firebase.MessagingService
import com.example.client_server_app.models.User
/**
 * Listener interface for handling click events on conversation items within a user list.
 * @property ConversionListener the name of this interface
 */
interface ConversionListener {
    /**
     * Called when a conversation item is clicked.
     *
     * @param user The user associated with the clicked conversation.
     */
    fun OnConversionClicked(user: User)
}