package com.example.client_server_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.client_server_app.adapters.UsersAdapter
import com.example.client_server_app.databinding.ActivityUsersBinding
import com.example.client_server_app.listeners.UserListener
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersActivity : AppCompatActivity(), UserListener {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        Log.e("Hata", "Hata19")
        SetListeners()
        Log.e("Hata", "Hata20")
        GetUsers()
    }

    private fun SetListeners() {
        binding.imageBack.setOnClickListener { v -> onBackPressed() }
    }

    private fun GetUsers() {
        Loading(true)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        Log.e("Hata", "Hata18")
        database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener { task ->
            Loading(false)
            Log.e("Hata", "Hata17")
            var currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
            Log.e("Hata", "Hata16")
            if (task.isSuccessful() && task.getResult() != null) {
                Log.e("Hata", "Hata15")
                var users: MutableList<User> = ArrayList()
                Log.e("Hata", "Hata14")
                val queryDocumentSnapShots = task.result
                Log.e("Hata", "Hata13")
                for (queryDocumentSnapShot in queryDocumentSnapShots) {
                    Log.e("Hata", "Hata12")
                    if (currentUserId.equals(queryDocumentSnapShot.id)) {
                        Log.e("Hata", "Hata11")
                        continue
                        Log.e("Hata", "Hata1")
                    }
                    val user = User()
                    Log.e("Hata", "Hata2")
                    user.name = queryDocumentSnapShot.getString(Constants.KEY_NAME).toString()
                    user.email = queryDocumentSnapShot.getString(Constants.KEY_EMAIL).toString()
                    user.image = queryDocumentSnapShot.getString(Constants.KEY_IMAGE).toString()
                    user.token = queryDocumentSnapShot.getString(Constants.KEY_FCM_TOKEN).toString()
                    users.add(user)
                    Log.e("Hata", "Hata3")
                }
                if (users.size > 0) {
                    val usersAdapter = UsersAdapter(users, this)
                    binding.usersRecyclerView.adapter = usersAdapter
                    Log.e("Hata", "Hata4")
                    binding.usersRecyclerView.visibility = View.VISIBLE
                } else {
                    ShowErrorMessage()
                    Log.e("Hata", "Hata5")
                }
            } else {
                ShowErrorMessage()
                Log.e("Hata", "Hata6")
            }
        }
    }

    private fun ShowErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No User Available")
        Log.e("Hata", "Hata7")
        binding.textErrorMessage.visibility = View.VISIBLE
        Log.e("Hata", "Hata8")
    }

    private fun Loading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            Log.e("Hata", "Hata9")
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            Log.e("Hata", "Hata10")
        }
    }

    override fun OnUserClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }
}