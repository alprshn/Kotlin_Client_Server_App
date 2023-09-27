package com.example.client_server_app.listeners

import com.example.client_server_app.models.User

interface UserListener {
    fun OnUserClicked(user:User)
}