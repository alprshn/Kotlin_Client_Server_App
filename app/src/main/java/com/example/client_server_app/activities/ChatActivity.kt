package com.example.client_server_app.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.example.client_server_app.adapters.ChatAdapter
import com.example.client_server_app.databinding.ActivityChatBinding
import com.example.client_server_app.models.ChatMessage
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private lateinit var conversionId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetListeners()
        LoadReceiverDetails()
        init()
        ListenMessages()
    }

    private fun SendMessage() {
        var message: HashMap<String, Any> = HashMap()
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID)!!)
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id)
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.text.toString())
        message.put(Constants.KEY_TIMESTAMP, Date())
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding.inputMessage.text = null
        if (conversionId != null) {
            UpdateConversion(binding.inputMessage.text.toString())
        } else {
            var conversion: HashMap<String, Any> = HashMap()
            conversion.put(
                Constants.KEY_SENDER_ID,
                preferenceManager.getString(Constants.KEY_USER_ID)!!
            )
            conversion.put(
                Constants.KEY_SENDER_NAME,
                preferenceManager.getString(Constants.KEY_NAME)!!
            )
            conversion.put(
                Constants.KEY_SENDER_IMAGE,
                preferenceManager.getString(Constants.KEY_IMAGE)!!
            )
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id)
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name)
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image)
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.text.toString())
            conversion.put(Constants.KEY_TIMESTAMP, Date())

        }
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            GetBitmapFromEncodedString(receiverUser.image),
            chatMessages,
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun GetBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun LoadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun SetListeners() {
        binding.imageBack.setOnClickListener { v -> onBackPressed() }
        binding.layoutSend.setOnClickListener { v -> SendMessage() }
    }

    private fun GetReadableDateTime(date: Date?): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }

    val eventListener = EventListener<QuerySnapshot> { value, error ->
        if (error !== null) {
            return@EventListener
        }

        if (value != null) {
            var count: Int = chatMessages.size
            var documentChange: DocumentChange
            for (documentChange in value.getDocumentChanges()) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    var chatMessage: ChatMessage = ChatMessage()
                    chatMessage.senderId =
                        documentChange.document.getString(Constants.KEY_SENDER_ID).toString()
                    chatMessage.receiverId =
                        documentChange.document.getString(Constants.KEY_RECEIVER_ID).toString()
                    chatMessage.message =
                        documentChange.document.getString(Constants.KEY_MESSAGE).toString()
                    chatMessage.dateTime =
                        GetReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP))
                    chatMessage.dateObject =
                        documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                    chatMessages.add(chatMessage)
                }
            }
            chatMessages.sortWith(compareBy { it.dateObject })

            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
            }
            binding.chatRecyclerView.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE

        if (conversionId == null) {
            CheckForConversion()
        }
    }

    private fun AddConversion(conversion: HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }

    private fun UpdateConversion(message: String) {
        var documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId)
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, Date()
        )
    }

    private fun ListenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT).whereEqualTo(
            Constants.KEY_SENDER_ID,
            preferenceManager.getString(Constants.KEY_USER_ID)
        ).whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)

        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id).whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                preferenceManager.getString(Constants.KEY_USER_ID)
            ).addSnapshotListener(eventListener)
    }


    private fun CheckForConversionRemotly(senderId: String, receiverId: String) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId).get().addOnCompleteListener(
                conversionOnCompleteListener
            )
    }

    private fun CheckForConversion() {
        if (chatMessages.size != 0) {
            CheckForConversionRemotly(
                preferenceManager.getString(Constants.KEY_USER_ID).toString(),
                receiverUser.id
            )
            CheckForConversionRemotly(
                receiverUser.id,
                preferenceManager.getString(Constants.KEY_USER_ID).toString()
            )
        }
    }

    val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isSuccessful && task.result != null && task.result.documents.size > 0) {
            var documentSnapshot: DocumentSnapshot = task.result.documents.get(0)
            conversionId = documentSnapshot.id
        }

    }
}

