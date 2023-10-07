package com.example.client_server_app.listeners

import com.example.client_server_app.models.User
/**
 * @author Alper Sahin
 * Listener interface for handling click events on user items within a user list.
 * @property UserListener the name of this interface
 */
interface UserListener {
    /**
     * Called when a user item is clicked.
     *
     * @param user The user associated with the clicked item.
     */
    fun OnUserClicked(user:User)
}