package com.example.client_server_app.firebase

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.client_server_app.R
import com.example.client_server_app.activities.ChatActivity
import com.example.client_server_app.activities.MainActivity
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var user: User = User()
        user.id = remoteMessage.data.get(Constants.KEY_USER_ID).toString()
        user.token = remoteMessage.data.get(Constants.KEY_FCM_TOKEN).toString()
        user.name = remoteMessage.data.get(Constants.KEY_NAME).toString()

        var notificationId: Int = java.util.Random().nextInt()
        var channelId: String = "chat_message"

        val intent = Intent(this, ChatActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(Constants.KEY_USER, user)
        var pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.drawable.ic_notification)
        builder.setContentTitle(user.name)
        builder.setContentText(remoteMessage.data.get(Constants.KEY_MESSAGE))
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(remoteMessage.data.get(Constants.KEY_MESSAGE))
        )
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        }

    }
}