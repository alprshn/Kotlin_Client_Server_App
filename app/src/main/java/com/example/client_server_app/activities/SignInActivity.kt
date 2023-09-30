package com.example.client_server_app.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.client_server_app.databinding.ActivitySignInBinding
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager.GetBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setListener()
    }


    //Giriş Yapma Sayfasına geçiş fonksiyonu
    private fun setListener() {
        binding.textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSignIn.setOnClickListener { v ->
            if (IsValidSignInDetails()) {
                SignIn()
            }
        }


    }

    fun SignIn() {
        Loading(true)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var user: HashMap<String, Any> = HashMap()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful() && task.getResult() != null && task.getResult().documents.size > 0) {
                    var documentSnapShot: DocumentSnapshot = task.getResult().documents.get(0)
                    preferenceManager.PutBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.PutString(Constants.KEY_USER_ID, documentSnapShot.id)
                    preferenceManager.PutString(
                        Constants.KEY_NAME,
                        documentSnapShot.getString(Constants.KEY_NAME)
                    )
                    preferenceManager.PutString(
                        Constants.KEY_IMAGE,
                        documentSnapShot.getString(Constants.KEY_IMAGE)
                    )
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    Loading(false)
                    ShowToast("Unable To Sign In")
                }

            }

    }

    private fun Loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun ShowToast(message: String) {
        Toasty.info(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun IsValidSignInDetails(): Boolean {
        if (binding.inputEmail.text.toString().trim().isEmpty()) {
            ShowToast("Enter Email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            ShowToast("Enter Valid Email")
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            ShowToast("Enter Password")
            return false
        } else {
            return true
        }
    }
}