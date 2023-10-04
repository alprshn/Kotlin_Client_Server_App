package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerReceivedMessageBinding
import com.example.client_server_app.databinding.ItemContainerSentMessageBinding

import com.example.client_server_app.models.ChatMessage


class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var receiverProfileImage: Bitmap? = null
    private lateinit var chatMessage: List<ChatMessage>
    private lateinit var senderId: String

    private val VIEW_TYPE_SENT: Int = 1
    private val VIEW_TYPE_RECEIVED: Int = 2

    fun SetReceiverProfileImage(bitmap: Bitmap) {
        receiverProfileImage = bitmap
    }

    constructor(receiverProfileImage: Bitmap?, chatMessage: List<ChatMessage>, senderId: String) {
        this.receiverProfileImage = receiverProfileImage
        this.chatMessage = chatMessage
        this.senderId = senderId
    }


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

        fun SetData(chatMessage: ChatMessage, receiverProfileImage: Bitmap?) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT
        } else {
            return VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SENT) {
            return SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        } else {
            return ReceiverMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessage.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as? SentMessageViewHolder)?.SetData(chatMessage[position])
        } else {
            (holder as? ReceiverMessageViewHolder)?.SetData(
                chatMessage[position],
                receiverProfileImage
            )
        }
    }


}