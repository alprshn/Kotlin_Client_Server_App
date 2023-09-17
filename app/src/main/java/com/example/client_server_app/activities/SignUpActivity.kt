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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivitySignUpBinding
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var encodedImage: String
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        SetListener()
    }

    //Yeni hesap oluşturmaya geçiş fonksiyonu
    private fun SetListener() {
        binding.textSignIn.setOnClickListener { v -> onBackPressed() }
        binding.buttonSignUp.setOnClickListener { v ->
            if (IsValidSignUpDetails()) {
                SignUp()
            }
        }
        binding.layoutImage.setOnClickListener { v ->
            var intent: Intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            pickImage.launch(intent)
        }
    }

    private fun ShowToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun SignUp() {
        Loading(true)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var user: HashMap<String, Any> = HashMap()
        user.put(Constants.KEY_NAME, binding.inputName.text.toString())
        user.put(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
        user.put(Constants.KEY_IMAGE, encodedImage)
        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener { documentReference -> }
            .addOnFailureListener { exception -> }


    }

    private fun IsValidSignUpDetails(): Boolean {
        if (encodedImage == null) {
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
        } else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()) {
            ShowToast("Confirm Your Password")
            return false
        } else if (binding.inputPassword.text.toString()
                .equals(binding.inputConfirmPassword.text.toString())
        ) {
            ShowToast("Password & Confirm Password Must Be Same")
            return false
        } else {
            return true
        }
    }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                val imageUri: Uri? = result.data?.data
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

    private fun EncodeImage(bitMap: Bitmap): String {
        var previewWidth: Int = 150
        var previewHeight: Int = bitMap.height * previewWidth / bitMap.width
        var previewBitMap: Bitmap =
            Bitmap.createScaledBitmap(bitMap, previewWidth, previewHeight, false)
        var byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

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