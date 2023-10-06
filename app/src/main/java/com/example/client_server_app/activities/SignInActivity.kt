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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
/**
 *
 * This class for activity sign-in
 *
 * This activity manage user sign-in logic.
 * This activity manage activity_sign_in.xml file
 * @property SignInActivity the name of this class.
 *
 */
class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager
    private var mAuth = FirebaseAuth.getInstance()

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
        SetListener()
    }

    /**
     *
     * The SetListener function listen all sign-in click event and has all click event
     *
     * The SetListener function is inside the onCreate function.
     */
    private fun SetListener() {
        //binding.textCreateNewAccount is if you have not Account you can click and you can sign up
        binding.textCreateNewAccount.setOnClickListener {
            // Creating new Intent object.
            //Intent object for communication witch each other different activity
            val intent = Intent(this, SignUpActivity::class.java)
            //startActivity starts the target activity
            startActivity(intent)
        }
        //binding.buttonSignIn.setOnClickListener listens click event and start sign-in
        binding.buttonSignIn.setOnClickListener { v ->
            // If does IsValidSignInDetails() function equals true starts email verification function
            if (IsValidSignInDetails()) {
                EmailVerification(
                    //Takes the email and password datas from edit text. EmailVerification() function use the email and password datas
                    binding.inputEmail.text.toString().trim(),
                    binding.inputPassword.text.toString().trim()
                )
            }
        }
    }

    /**
     *
     * The SignIn function for users sign-in
     * The SignIn function inside the IsVerified() function
     * If there is not matching user in the database. It return Toasty Message
     *
     */
    fun SignIn() {
        //Loading function for progress bar and if loading function has a true parameter progress bar starts
        Loading(true)
        // Creates FireStore connection.
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        //Hides email and password in the HashMap object for FireStore query
        var user: HashMap<String, Any> = HashMap()
        //It starts to query on FireStore Collections
        database.collection(Constants.KEY_COLLECTION_USERS)
            //whereEqualTo queries to matching datas(email and password)
            //The get() call performs the query
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful() && task.getResult() != null && task.getResult().documents.size > 0) {
                    // documentSnapShot for access documents
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
                    //if success the sign-in It redirect the MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    //This flag starts the new task and delete old task
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    Loading(false)
                    ShowToast("Unable To Sign In")
                }

            }
    }

    /**
     * This function for email verification
     *
     * @param email the type of a String in this function.
     *
     * @param password the type of a String in this function.
     */
    fun EmailVerification(email: String, password: String) {
        //Tries for sign-in with email and password with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //We take current user
                var user: FirebaseUser = mAuth.currentUser!!
                //Starts IsVerified() function
                IsVerified()
            } else {
                ShowToast("Invalid Email Or Password")
            }
        }
    }

    /**
     * This function checks the isEmailVerified
     * It starts Firebase user and assign currentUser
     * If email is verified ıt starts SignIn() function
     */
    fun IsVerified() {
        var firebaseUser: FirebaseUser? = mAuth.currentUser
        if (firebaseUser?.isEmailVerified == true) {
            SignIn()
        } else {
            ShowToast("Your Account Is Not Verified")
        }
    }

    /**
     * This function for progressBar
     * @param isLoading the type of a Boolean in this function.
     * If isLoading equal the true starts progressBar
     */
    private fun Loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    /**
     * @param message the type of a String in this function.
     * ShowToast function for the Toasty Message
     */
    private fun ShowToast(message: String) {
        Toasty.info(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * IsValidSignInDetails() checks the all input event
     * If there is no data in the edit text returns false
     * If there is data in the edit text returns true
     * @return the Boolean for input event null or has a data
     */
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