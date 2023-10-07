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
 * This class for user availability
 * @property BaseActivity the name of this class.
 *
 */
open class BaseActivity : AppCompatActivity() {
    //documentReference return a document in Firebase
    private lateinit var documentReference: DocumentReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var preferenceManager: PreferenceManager = PreferenceManager(applicationContext)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID).toString()
        )
    }

    /**
     * The [onPause] function starts on the app stop
     * KEY_AVAILABILITY will be zero so user will be offline
     */
    override fun onPause() {
        super.onPause()
        documentReference.update(Constants.KEY_AVAILABILITY, 0)
    }

    /**
     * The [onResume] function  starts on the app resume
     * KEY_AVAILABILITY will be 1 so user will be online
     */
    override fun onResume() {
        super.onResume()
        documentReference.update(Constants.KEY_AVAILABILITY, 1)
    }
}