package com.example.client_server_app.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
/**
 * @author Alper Sahin
 *
 * This class for activity sign-up
 * This activity manage user sign-up logic.
 * This activity manage activity_sign_up.xml file
 * @property BaseActivity the name of this class.
 *
 */
open class BaseActivity : AppCompatActivity() {

    private lateinit var documentReference: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var preferenceManager: PreferenceManager = PreferenceManager(applicationContext)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
    }

    override fun onPause() {
        super.onPause()
        documentReference.update(Constants.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        documentReference.update(Constants.KEY_AVAILABILITY, 1)
    }
}