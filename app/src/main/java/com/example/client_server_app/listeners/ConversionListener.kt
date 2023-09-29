package com.example.client_server_app.listeners

import com.example.client_server_app.models.User

interface ConversionListener {
    fun OnConversionClicked(user: User)
}