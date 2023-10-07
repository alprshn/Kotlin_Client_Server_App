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
/**
 * @author Alper Sahin
 *
 * This class for users page
 * @property UsersActivity the name of this class.
 *
 */
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
        SetListeners()
        GetUsers()
        Search()
    }

    /**
     * The [SetListener] function listen all users page click event and has all click event
     * The [SetListener] function is inside the onCreate function.
     */
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
                    user.name = queryDocumentSnapShot.getString(Constants.KEY_NAME).toString()
                    user.email = queryDocumentSnapShot.getString(Constants.KEY_EMAIL).toString()
                    user.image = queryDocumentSnapShot.getString(Constants.KEY_IMAGE).toString()
                    user.token = queryDocumentSnapShot.getString(Constants.KEY_FCM_TOKEN).toString()
                    user.id = queryDocumentSnapShot.id
                    users.add(user)
                }
                if (users.size > 0) {
                    usersAdapter = UsersAdapter(users, this)
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

    /**
     * This function for [ShowErrorMessage]
     * If there in no users. [ShowErrorMessage] will be active
     */
    private fun ShowErrorMessage() {
        binding.textErrorMessage.text = String.format("%s", "No User Available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    /**
     * This function for progressBar
     * @param isLoading the type of a Boolean in this function.
     * If isLoading equal the true starts progressBar
     */
    private fun Loading(isLoading: Boolean) {

        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    override fun OnUserClicked(user: User) {
        val intent = Intent(applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER, user)
        startActivity(intent)
        finish()
    }

    /**
     * This function for search user with email
     */
    fun Search() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    /**
     * @param text the type of a String in this function.
     * It send the searchBar's text in ArrayList
     * It filter user with email
     */
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