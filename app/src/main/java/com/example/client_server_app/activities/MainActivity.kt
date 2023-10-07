package com.example.client_server_app.activities

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.client_server_app.adapters.RecentConversationsAdapter
import com.example.client_server_app.databinding.ActivityMainBinding
import com.example.client_server_app.listeners.ConversionListener
import com.example.client_server_app.models.ChatMessage
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import es.dmoral.toasty.Toasty

/**
 * @author Alper Sahin
 *
 * This activity manage user [MainActivity] logic.
 * This activity manage activity_main.xml file
 * This class for [MainActivity] page
 * @property MainActivity the name of this class.
 *
 */
class MainActivity : BaseActivity(), ConversionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var conversations: ArrayList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        init()
        LoadUserDetails()
        GetToken()
        NotificationPermission()
        SetListener()
        ListenConversations()
    }

    private fun init() {
        conversations = ArrayList()
        conversationsAdapter = RecentConversationsAdapter(conversations, this)
        binding.conversationsRecyclerView.adapter = conversationsAdapter
        database = FirebaseFirestore.getInstance()
    }

    /**
     * The [SetListener] function listen all [MainActivity] click event and has all click event
     * The [SetListener] function is inside the onCreate function.
     */
    private fun SetListener() {
        binding.imageSignOut.setOnClickListener { v -> SignOut() }
        binding.fabNewChat.setOnClickListener { v ->
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error !== null) {
            return@EventListener
        }

        if (value != null) {
            var documentChange: DocumentChange
            for (documentChange in value.getDocumentChanges()) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    var senderId: String =
                        documentChange.document.getString(Constants.KEY_SENDER_ID).toString()
                    var receiverId: String =
                        documentChange.document.getString(Constants.KEY_RECEIVER_ID).toString()
                    var chatMessage: ChatMessage = ChatMessage()
                    chatMessage.senderId = senderId
                    chatMessage.receiverId = receiverId
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage =
                            documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                                .toString()
                        chatMessage.conversionName =
                            documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                                .toString()
                        chatMessage.conversionId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID).toString()
                    } else {
                        chatMessage.conversionImage =
                            documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                                .toString()
                        chatMessage.conversionName =
                            documentChange.document.getString(Constants.KEY_SENDER_NAME)
                                .toString()
                        chatMessage.conversionId =
                            documentChange.document.getString(Constants.KEY_SENDER_ID).toString()
                    }
                    chatMessage.message =
                        documentChange.document.getString(Constants.KEY_LAST_MESSAGE).toString()
                    chatMessage.dateObject =
                        documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                    conversations.add(chatMessage)
                } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                    for (i in 0 until conversations.size) {
                        var senderId: String =
                            documentChange.document.getString(Constants.KEY_SENDER_ID).toString()
                        var receiverId: String =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID).toString()
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(
                                receiverId
                            )
                        ) {
                            conversations.get(i).message =
                                documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                    .toString()
                            conversations.get(i).dateObject =
                                documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                            break
                        }
                    }
                }
            }
            conversations.sortWith(compareBy { it.dateObject })
            conversationsAdapter.notifyDataSetChanged()
            binding.conversationsRecyclerView.smoothScrollToPosition(0)
            binding.conversationsRecyclerView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun ListenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).whereEqualTo(
            Constants.KEY_SENDER_ID,
            preferenceManager.getString(Constants.KEY_USER_ID)
        ).addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).whereEqualTo(
            Constants.KEY_RECEIVER_ID,
            preferenceManager.getString(Constants.KEY_USER_ID)
        ).addSnapshotListener(eventListener)
    }

    /**
     * @param message the type of a String in this function.
     * [ShowToast] function for the Toasty Message
     */
    private fun ShowToast(message: String) {
        Toasty.info(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun UpdateToken(token: String) {
        preferenceManager.PutString(Constants.KEY_FCM_TOKEN, token)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID)!!)
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener { e -> ShowToast("Unable To Updated Token") }
    }

    private fun GetToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::UpdateToken)
    }

    private fun LoadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes: ByteArray =
            Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        var bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun SignOut() {
        ShowToast("Signing Out....")
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)!!
            )
        var updates: HashMap<String, Any> = HashMap()
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete())
        documentReference.update(updates)
            .addOnSuccessListener { unused ->
                preferenceManager.Clear()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }.addOnFailureListener { e -> ShowToast("Unable To Sign Out") }
    }

    override fun OnConversionClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
    }

    fun NotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.POST_NOTIFICATIONS"),
                1
            )
        }
    }
}