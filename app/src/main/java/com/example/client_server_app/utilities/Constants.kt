package com.example.client_server_app.utilities

import com.example.client_server_app.firebase.MessagingService

/**
 * A utility class that contains constant values used throughout the application.
 * @property Constants the name of this class
 */
class Constants {
    companion object {
        // Collection names and keys for Firestore
        const val KEY_COLLECTION_USERS: String = "users"
        const val KEY_NAME: String = "name"
        const val KEY_EMAIL: String = "email"
        const val KEY_PASSWORD: String = "password"
        const val KEY_PREFERENCE_NAME: String = "chatAppPreference"
        const val KEY_IS_SIGNED_IN: String = "isSignedIn"
        const val KEY_USER_ID: String = "userId"
        const val KEY_IMAGE: String = "image"
        const val KEY_FCM_TOKEN: String = "fcmToken"
        const val KEY_USER: String = "user"
        const val KEY_COLLECTION_CHAT: String = "chat"
        const val KEY_SENDER_ID: String = "senderId"
        const val KEY_RECEIVER_ID: String = "receiverId"
        const val KEY_MESSAGE: String = "message"
        const val KEY_TIMESTAMP: String = "timestamp"
        const val KEY_COLLECTION_CONVERSATIONS = "conversations"
        const val KEY_SENDER_NAME = "senderName"
        const val KEY_RECEIVER_NAME = "receiverName"
        const val KEY_SENDER_IMAGE = "senderImage"
        const val KEY_RECEIVER_IMAGE = "receiverImage"
        const val KEY_LAST_MESSAGE = "lastMessage"
        const val KEY_AVAILABILITY = "availability"

        // SharedPreferences and user authentication keys
        const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

        // Remote message headers and values
        var remoteMessageHeaders: HashMap<String, String>? = null

        /**
         * Get the headers for remote messages.
         *
         * @return The headers for remote messages.
         */
        fun getRemoteMsgHeaders(): HashMap<String, String> {
            if (remoteMessageHeaders == null) {
                remoteMessageHeaders = HashMap()
                remoteMessageHeaders!!.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA_q5UAYA:APA91bHLkYrQaY29Y3pX_hfb8qFN3CRu6aE3YEO547MhzIhpYpjjU4byGnmwFhjVDwscdccUWfTObdcgjvmDRK-nQETjx6o09xCvUV7F1ZSX58M9PSlMMTAMH2lG6ez4Yhcz3WFAmfwy"
                )
                remoteMessageHeaders!!.put(REMOTE_MSG_CONTENT_TYPE, "application/json")
            }
            return remoteMessageHeaders!!
        }

    }

}