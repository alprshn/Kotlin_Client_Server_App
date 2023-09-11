package com.example.client_server_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivitySignUpBinding
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
        binding.buttonSignUp.setOnClickListener { v->
            if (IsValidSignUpDetails()){
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
            ShowToast("Selet Profile Image")
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
}