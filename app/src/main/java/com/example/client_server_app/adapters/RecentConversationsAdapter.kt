package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerRecentConversionBinding
import com.example.client_server_app.listeners.ConversionListener
import com.example.client_server_app.models.ChatMessage
import com.example.client_server_app.models.User


class RecentConversationsAdapter :
    RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private val chatMessages: List<ChatMessage>
    private val conversionListener: ConversionListener

    constructor(chatMessages: List<ChatMessage>, conversionListener: ConversionListener) {
        this.chatMessages = chatMessages
        this.conversionListener = conversionListener
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
            binding.root.setOnClickListener { v ->
                var user: User = User()
                user.id = chatMessage.conversionId
                user.name = chatMessage.conversionName
                user.image = chatMessage.conversionImage
                conversionListener.OnConversionClicked(user)
            }
        }
    }

    private fun GetConversionImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.SetData(chatMessages.get(position))
    }
}