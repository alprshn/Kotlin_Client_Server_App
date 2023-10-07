package com.example.client_server_app.models

import java.io.Serializable
/**
 * A class representing a user in the application.
 *
 * @property name The name of the user.
 * @property image The profile image of the user (default empty string).
 * @property email The email address of the user.
 * @property token The Firebase Cloud Messaging (FCM) token associated with the user.
 * @property id The unique identifier of the user.
 */
class User : Serializable {
    lateinit var name: String
    var image: String =""
    lateinit var email: String
    lateinit var token: String
    lateinit var id: String
}