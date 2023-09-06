package com.example.client_server_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivitySignInBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.lang.ref.Reference

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
    }


    //Giriş Yapma Sayfasına geçiş fonksiyonu
    private fun setListener() {
        binding.textCreateNewAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            binding.buttonSignIn.setOnClickListener { v-> addDataToFireStore() }
        }
    }

    private fun addDataToFireStore() {
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        var data: HashMap<String, Any> = HashMap()
        data["first_name"] = "Chirag"
        data["last_name"] = "Kacchadiva"
        database.collection("users").add(data).addOnSuccessListener { documentReference ->
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{exception ->
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }
}