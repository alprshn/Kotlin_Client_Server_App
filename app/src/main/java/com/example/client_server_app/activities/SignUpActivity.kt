package com.example.client_server_app.activities

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivitySignUpBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var encodedImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
    }

    private fun ShowToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun SignUp() {

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