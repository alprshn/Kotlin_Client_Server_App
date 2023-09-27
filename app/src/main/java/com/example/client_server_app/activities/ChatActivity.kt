package com.example.client_server_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.client_server_app.R
import com.example.client_server_app.databinding.ActivityChatBinding
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    private fun LoadReceiverDetails() {
        receiverUser = (intent.getSerializableExtra(Constants.KEY_USER) as? User)!!
        binding.textName.text = receiverUser.name
    }
}