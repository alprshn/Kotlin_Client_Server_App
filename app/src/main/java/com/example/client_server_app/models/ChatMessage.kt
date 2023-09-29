package com.example.client_server_app.models

import java.util.Date

class ChatMessage {
    lateinit var senderId: String
    lateinit var receiverId: String
    lateinit var message: String
    lateinit var dateTime: String
    lateinit var dateObject: Date
    lateinit var conversionId: String
    lateinit var conversionName: String
    lateinit var conversionImage: String
}