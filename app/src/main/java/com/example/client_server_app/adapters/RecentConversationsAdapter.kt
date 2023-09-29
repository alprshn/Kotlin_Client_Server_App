package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerRecentConversionBinding
import com.example.client_server_app.models.ChatMessage


class RecentConversationsAdapter: RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private val chatMessages: List<ChatMessage>

    constructor(chatMessages: List<ChatMessage>) {
        this.chatMessages = chatMessages
    }

    inner class ConversionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerRecentConversionBinding

        constructor(itemContainerRecentConversionBinding: ItemContainerRecentConversionBinding) : this(
            itemContainerRecentConversionBinding.root
        ) {
            binding = itemContainerRecentConversionBinding
        }

        fun SetData(chatMessage: ChatMessage) {
            binding.imageProfile.setImageBitmap(GetConversionImage(chatMessage.conversionImage))
            binding.textName.text = chatMessage.conversionName
            binding.textRecentMessage.text = chatMessage.message
        }
    }

    private fun GetConversionImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}