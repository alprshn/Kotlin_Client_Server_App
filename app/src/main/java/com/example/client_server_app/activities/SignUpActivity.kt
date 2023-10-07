package com.example.client_server_app.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.client_server_app.databinding.ActivitySignUpBinding
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
/**
 * @author Alper Sahin
 *
 * This class for activity sign-up
 * This activity manage user sign-up logic.
 * This activity manage activity_sign_up.xml file
 * @property SignUpActivity the name of this class.
 *
 */
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var encodedImage: String = ""
    private lateinit var preferenceManager: PreferenceManager
    private var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        SetListener()
        StrengthPasswordMeter()
    }


    /**
     * The [SetListener] function listen all sign-up click event and has all click event
     * The [SetListener] function is inside the onCreate function.
     */
    private fun SetListener() {
        binding.textSignIn.setOnClickListener { v -> onBackPressed() }
        binding.buttonSignUp.setOnClickListener { v ->
            if (IsValidSignUpDetails()) {
                VerifyEmailAccount(
                    binding.inputEmail.text.toString().trim(),
                    binding.inputPassword.text.toString().trim()
                )
            }
        }
        binding.layoutImage.setOnClickListener { v ->
            var intent: Intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    /**
     * @param message the type of a String in this function.
     * [ShowToast] function for the Toasty Message
     */
    private fun ShowToast(message: String) {
        Toasty.info(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * @param email the type of a String in this function.
     * @param password the type of a String in this function.
     * [VerifyEmailAccount] has 2 parameter
     * [VerifyEmailAccount] takes password and email data. It sends they for the email verification
     */
    private fun VerifyEmailAccount(email: String, password: String) {
        Loading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                        ShowToast("Please Check Your Email For Verification")
                        SignUp()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    ShowToast("Password Too Short")
                }
            }
    }

    /**
     * The [SignUp] function for users sign-up
     * The [SignIn] function inside the [VerifyEmailAccount] function
     * SignUp function creates HashMap object and sends all data to firebase
     */
    private fun SignUp() {
        Loading(true)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var user: HashMap<String, Any> = HashMap()
        user.put(Constants.KEY_NAME, binding.inputName.text.toString())
        user.put(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
        user.put(Constants.KEY_IMAGE, encodedImage)
        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener { documentReference ->
                Loading(false)
                preferenceManager.PutBoolean(Constants.KEY_IS_SIGNED_IN, false)
                preferenceManager.PutString(Constants.KEY_USER_ID, documentReference.id)
                preferenceManager.PutString(Constants.KEY_NAME, binding.inputName.text.toString())
                preferenceManager.PutString(Constants.KEY_IMAGE, encodedImage)
            }
            .addOnFailureListener { exception ->
                Loading(false)
                exception.message?.let { ShowToast(it) }
            }
    }

    /**
     * [IsValidSignUpDetails] checks the all input event
     * If there is no data in the edit text returns false
     * If there is data in the edit text returns true
     * @return the Boolean for input event null or has a data
     */
    private fun IsValidSignUpDetails(): Boolean {
        if (encodedImage == "") {
            ShowToast("Select Profile Image")
            return false
        } else if (binding.inputName.text.toString().trim().isEmpty()) {
            ShowToast("Enter Name")
            return false
        } else if (binding.inputEmail.text.toString().trim().isEmpty()) {
            ShowToast("Enter Email")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            ShowToast("Enter Valid Image")
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            ShowToast("Enter Password")
            return false
        } else if (binding.inputPassword.text.toString().length <= 7) {
            ShowToast("Password Too Short")
            return false
        } else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()) {
            ShowToast("Confirm Your Password")
            return false
        } else if (!binding.inputPassword.text.toString()
                .equals(binding.inputConfirmPassword.text.toString())
        ) {
            ShowToast("Password & Confirm Password Must Be Same")
            return false
        } else {
            return true
        }
    }
    /**
     * This [ActivityResultLauncher] object, for the users choose the picture and [ActivityResultLauncher] saves picture data .
     * [ActivityResultContracts.StartActivityForResult] starts the activity.
     * [registerForActivityResult] starts particular activity
     * @param result returns activity result and "result" is [ActivityResult] obeject.
     */
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        //It checks selected picture success
        if (result.resultCode == Activity.RESULT_OK) {
            //It check for there is data
            if (result.data != null) {
                //It takes picture Url
                val imageUri: Uri? = result.data?.data
                //Picture from the Url converts input stream
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                try {
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = EncodeImage(bitmap)

                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * This function for the password strength
     * You can use to set the strength password meter in this function
     */
    private fun StrengthPasswordMeter() {
        var meter: PasswordStrengthMeter = binding.passwordInputMeter
        meter.setEditText(binding.inputPassword)
        binding.inputPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordInputMeter.visibility = View.VISIBLE
            } else {
                binding.passwordInputMeter.visibility = View.GONE
            }
        }
    }

    /**
     * This function for encode image
     * @param bitMap the type of a Bitmap in this function.
     * This function takes the bitMap parameter
     * @return the String for Base64 type view
     */
    private fun EncodeImage(bitMap: Bitmap): String {
        //This variable sets 150 pixels by default
        var previewWidth: Int = 150
        //This variable calculated by the ratio of the bitMap parameter and previewWidth
        var previewHeight: Int = bitMap.height * previewWidth / bitMap.width
        //createScaledBitmap resizes of the BitMap object for the according to small or big layouts
        var previewBitMap: Bitmap =
            Bitmap.createScaledBitmap(bitMap, previewWidth, previewHeight, false)
        //ByteArrayOutputStream uses to write type of the "byte"
        var byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        // previewBitMap will be quality %50. previewBitMap compress %50 on the JPEG format. It will be convert type of the byte array
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        // Base64 is a method
        //Base64 uses binary datas(picture, voice file) for hide on the text base
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    /**
     * This function for progressBar
     * @param isLoading the type of a Boolean in this function.
     * If isLoading equal the true starts progressBar
     */
    private fun Loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }
}