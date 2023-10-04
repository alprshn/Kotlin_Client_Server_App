package com.example.client_server_app.firebase

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var user: User = User()
        user.id = remoteMessage.data.get(Constants.KEY_USER_ID).toString()
        user.name = remoteMessage.data.get(Constants.KEY_NAME).toString()
        user.token = remoteMessage.data.get(Constants.KEY_FCM_TOKEN).toString()

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
        Log.e("Image", user.image)
        builder.setContentText(remoteMessage.data.get(Constants.KEY_MESSAGE))
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(remoteMessage.data.get(Constants.KEY_MESSAGE))
        )
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channelName: CharSequence = "Chat Message"
            var channelDescription: String =
                "This notification channel is used for chat message notification"
            var importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            var channel: NotificationChannel =
                NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            var notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        var notificationManagerCompat: NotificationManagerCompat =
            NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManagerCompat.notify(notificationId, builder.build())

    }
}