package com.example.client_server_app.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.example.client_server_app.adapters.ChatAdapter
import com.example.client_server_app.databinding.ActivityChatBinding
import com.example.client_server_app.models.ChatMessage
import com.example.client_server_app.models.User
import com.example.client_server_app.network.ApiClient
import com.example.client_server_app.network.ApiService
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

/**
 * @author Alper Sahin
 *
 * This activity manage user [ChatActivity] logic.
 * This activity manage activity_chat.xml file
 * This class for [ChatActivity] page
 * @property ChatActivity the name of this class.
 *
 */
class ChatActivity : BaseActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private var conversionId: String? = null
    private var isReceiverAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SetListeners()
        LoadReceiverDetails()
        init()
        ListenMessages()
    }

    /**
     * [SendMessage] function for the send message
     * It created HashMap object and put in the user information
     */
    private fun SendMessage() {
        var message: HashMap<String, Any> = HashMap()
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID)!!)
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id)
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.text.toString())
        message.put(Constants.KEY_TIMESTAMP, Date())
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        if (conversionId != null) {
            UpdateConversion(binding.inputMessage.text.toString())
        } else {
            var conversion: HashMap<String, Any> = HashMap()
            conversion.put(
                Constants.KEY_SENDER_ID,
                preferenceManager.getString(Constants.KEY_USER_ID).toString()
            )
            conversion.put(
                Constants.KEY_SENDER_NAME,
                preferenceManager.getString(Constants.KEY_NAME).toString()
            )
            conversion.put(
                Constants.KEY_SENDER_IMAGE,
                preferenceManager.getString(Constants.KEY_IMAGE).toString()
            )
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id)
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name)
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image)
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.text.toString())
            conversion.put(Constants.KEY_TIMESTAMP, Date())
            AddConversion(conversion)
        }
        if (!isReceiverAvailable) {
            try {
                var tokens: JSONArray = JSONArray()
                tokens.put(receiverUser.token)

                var data: JSONObject = JSONObject()
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME))
                data.put(
                    Constants.KEY_FCM_TOKEN,
                    preferenceManager.getString(Constants.KEY_FCM_TOKEN)
                )
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.text.toString())
                var body: JSONObject = JSONObject()
                body.put(Constants.REMOTE_MSG_DATA, data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens)

                SendNotification(body.toString())
            } catch (exception: Exception) {
                ShowToast(exception.message.toString())
            }
        }
        binding.inputMessage.text = null
    }

    /**
     * The [init] function makes the preset for the all function
     */
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

    /**
     * The [ListenAvailabiltyOfReceiver] function checks receiver for the availability
     */
    fun ListenAvailabiltyOfReceiver() {
        database.collection(Constants.KEY_COLLECTION_USERS).document(receiverUser.id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                        //requireNonNull checks null or not
                        var availability: Int =
                            Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY))!!
                                .toInt()
                        isReceiverAvailable = availability == 1
                    }
                    receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN).toString()
                    if (receiverUser.image == null) {
                        receiverUser.image = value.getString(Constants.KEY_IMAGE).toString()
                        chatAdapter.SetReceiverProfileImage(GetBitmapFromEncodedString(receiverUser.image)!!)
                        chatAdapter.notifyItemRangeChanged(0, chatMessages.size)
                    }
                }
                if (isReceiverAvailable) {
                    binding.textAvailability.visibility = View.VISIBLE
                } else {
                    binding.textAvailability.visibility = View.GONE
                }
            }
    }

    /**
     * @param messageBody the type of a String in this function.
     * [SendNotification] function for the send notification
     * It uses the Api
     */
    private fun SendNotification(messageBody: String) {
        val apiService = ApiClient.GetClient()?.create(ApiService::class.java)
        val headers = Constants.getRemoteMsgHeaders()

        apiService?.SendMessage(headers, messageBody)?.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    try {
                        if (response.body() != null) {
                            var responseJson: JSONObject = JSONObject(response.body())
                            var results: JSONArray = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1) {
                                var error: JSONObject = results.get(0) as JSONObject
                                ShowToast(error.getString("error"))
                                return
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    ShowToast("Notification Sent Successfully")
                } else {
                    ShowToast("Error: " + response.code())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                ShowToast(t.message.toString())
            }
        })
    }

    /**
     * @param message the type of a String in this function.
     * [ShowToast] function for the Toasty Message
     */
    private fun ShowToast(message: String) {
        Toasty.info(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This function for encode image
     * @param encodedImage the type of a String in this function.
     * This function takes the bitMap parameter
     * @return the Bitmap for Base64 type view
     */
    private fun GetBitmapFromEncodedString(encodedImage: String): Bitmap? {
        if (encodedImage != null) {
            val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            return null
        }
    }

    /**
     * This function for encode image
     * @param bitMap the type of a Bitmap in this function.
     * This function takes the bitMap parameter
     * @return the String for Base64 type view
     */
    private fun LoadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    /**
     * The [SetListeners] function listen all [ChatActivity] click event and has all click event
     * The [SetListeners] function is inside the onCreate function.
     */
    private fun SetListeners() {
        binding.imageBack.setOnClickListener { v -> onBackPressed() }
        binding.layoutSend.setOnClickListener { v -> SendMessage() }
    }

    /**
     * The [GetReadableDateTime] function for the instant date time
     * @param date the type of a Date in this function.
     * @return the String for dateFormat
     */
    private fun GetReadableDateTime(date: Date?): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault())
        return dateFormat.format(date)
    }

    /**
     * [eventListener] listens the changes  Firestore's collections
     * @param value the type of a QuerySnapshot in this function.
     * @param error the type of a QuerySnapshot in this function.
     */
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

    /**
     * [AddConversion] function for add conversion
     * @param conversion the type of a HashMap<String, Any> in this function.
     *
     */
    private fun AddConversion(conversion: HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).add(conversion)
            .addOnSuccessListener { documentReference -> conversionId = documentReference.id }
    }

    /**
     * [UpdateConversion] function for updates last conversion
     * @param message the type of String in this function.
     *
     */
    private fun UpdateConversion(message: String) {
        var documentReference: DocumentReference =
            database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .document(conversionId.toString())
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, Date()
        )
    }

    /**
     * [ListenMessages] function listen to changing collection in firebase
     * It uses addSnapshotListener for the listen
     */
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

    /**
     * [CheckForConversionRemotly] function for check conversation
     * @param senderId the type of String in this function.
     * @param receiverId the type of String in this function.
     */
    private fun CheckForConversionRemotly(senderId: String, receiverId: String) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId).get().addOnCompleteListener(
                conversionOnCompleteListener
            )
    }

    /**
     * [CheckForConversion] function for check conversation
     */
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

    /**
     * [CheckForConversionRemotly] function for check conversation
     * If cycle completes. (documentSnapshot.id) assigns conversionId
     */
    val conversionOnCompleteListener = OnCompleteListener<QuerySnapshot> { task ->
        if (task.isSuccessful && task.result != null && task.result.documents.size > 0) {
            var documentSnapshot: DocumentSnapshot = task.result.documents.get(0)
            conversionId = documentSnapshot.id
        }
    }

    /**
     * The [onResume] function  starts on the app resume
     * It checks user availability status
     */
    override fun onResume() {
        super.onResume()
        ListenAvailabiltyOfReceiver()
    }


}

