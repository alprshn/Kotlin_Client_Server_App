package com.example.client_server_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.client_server_app.adapters.UsersAdapter
import com.example.client_server_app.databinding.ActivityUsersBinding
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        GetUsers()
    }

    private fun SetListeners() {
        binding.imageBack.setOnClickListener { v -> onBackPressed() }
    }

    private fun GetUsers() {
        Loading(true)
        var database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS).get().addOnCompleteListener { task ->
            Loading(false)
            var currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
            if (task.isSuccessful() && task.getResult() != null) {
                var users: MutableList<User> = ArrayList()
                val queryDocumentSnapShots = task.result
                for (queryDocumentSnapShot in queryDocumentSnapShots) {
                    if (currentUserId.equals(queryDocumentSnapShot.id)) {
                        continue
                    }
                    var user: User = User()
                    user.name = queryDocumentSnapShot.getString(Constants.KEY_NAME)!!
                    user.email = queryDocumentSnapShot.getString(Constants.KEY_EMAIL)!!
                    user.image = queryDocumentSnapShot.getString(Constants.KEY_IMAGE)!!
                    user.token = queryDocumentSnapShot.getString(Constants.KEY_FCM_TOKEN)!!
                    users.add(user)
                }
                if (users.size > 0) {
                    var usersAdapter: UsersAdapter = UsersAdapter(users)
                    binding.usersRecyclerView.adapter = usersAdapter
                    binding.usersRecyclerView.visibility = View.VISIBLE
                } else {
                    ShowErrorMessage()
                }
            } else {
                ShowErrorMessage()
            }
        }
    }

    private fun ShowErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No User Available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun Loading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}