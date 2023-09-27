package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerReceivedMessageBinding
import com.example.client_server_app.databinding.ItemContainerSentMessageBinding
import com.example.client_server_app.databinding.ItemContainerUserBinding
import com.example.client_server_app.models.ChatMessage

class ChatAdapter {
    private lateinit var receiverProfileImage: Bitmap

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerSentMessageBinding

        constructor(itemContainerSentMessageBinding: ItemContainerSentMessageBinding) : this(
            itemContainerSentMessageBinding.root
        ) {
            binding = itemContainerSentMessageBinding
        }

        fun SetData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }
    }


    inner class ReceiverMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerReceivedMessageBinding

        constructor(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding) : this(
            itemContainerReceivedMessageBinding.root
        ) {
            binding = itemContainerReceivedMessageBinding
        }

        fun SetData(chatMessage: ChatMessage, receiverProfileImage: Bitmap) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            binding.imageProfile.setImageBitmap(receiverProfileImage)
        }
    }
}