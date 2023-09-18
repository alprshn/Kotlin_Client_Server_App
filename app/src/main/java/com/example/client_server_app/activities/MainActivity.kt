package com.example.client_server_app.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.example.client_server_app.databinding.ActivityMainBinding
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        LoadUserDetails()
        GetToken()
        SetListener()
    }

    private fun SetListener() {
        binding.imageSignOut.setOnClickListener { v -> SignOut() }
        binding.fabNewChat.setOnClickListener { v ->
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    private fun ShowToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun UpdateToken(token: String) {
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
}