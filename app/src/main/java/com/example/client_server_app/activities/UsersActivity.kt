package com.example.client_server_app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.example.client_server_app.adapters.UsersAdapter
import com.example.client_server_app.databinding.ActivityUsersBinding
import com.example.client_server_app.listeners.UserListener
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants
import com.example.client_server_app.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class UsersActivity : BaseActivity(), UserListener {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var users: MutableList<User>
    private lateinit var usersAdapter: UsersAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        Log.e("Hata", "Hata19")
        SetListeners()
        Log.e("Hata", "Hata20")
        GetUsers()
        Search()
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
                users = ArrayList()
                val queryDocumentSnapShots = task.result
                for (queryDocumentSnapShot in queryDocumentSnapShots) {
                    if (currentUserId.equals(queryDocumentSnapShot.id)) {
                        continue
                    }
                    val user = User()
                    Log.e("Hata", "Hata2")
                    user.name = queryDocumentSnapShot.getString(Constants.KEY_NAME).toString()
                    user.email = queryDocumentSnapShot.getString(Constants.KEY_EMAIL).toString()
                    user.image = queryDocumentSnapShot.getString(Constants.KEY_IMAGE).toString()
                    user.token = queryDocumentSnapShot.getString(Constants.KEY_FCM_TOKEN).toString()
                    user.id = queryDocumentSnapShot.id
                    users.add(user)
                    Log.e("Hata", "Hata3")
                }
                if (users.size > 0) {
                    usersAdapter = UsersAdapter(users, this)
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
        binding.textErrorMessage.visibility = View.VISIBLE
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

    fun Search() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Değişiklik öncesinde yapılacak işlemler
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Değişiklik anında yapılacak işlemler
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    fun filter(text: String) {
        var filteredList: ArrayList<User> = ArrayList()

        for (filterUser: User in users) {
            if (filterUser.email.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(filterUser)
            }
        }
        usersAdapter.filterList(filteredList)
    }

}