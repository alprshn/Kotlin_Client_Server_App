package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.adapters.UsersAdapter.UserViewHolder
import com.example.client_server_app.databinding.ItemContainerRecentConversionBinding
import com.example.client_server_app.listeners.ConversionListener
import com.example.client_server_app.models.ChatMessage
import com.example.client_server_app.models.User

/**
 * An adapter class for populating a RecyclerView with a list of recent chat conversations.
 *
 * @param chatMessages The list of chat messages representing recent conversations.
 * @param conversionListener The listener to handle conversation item click events.
 * @property RecentConversationsAdapter the name of this class.
 * @constructor creates an parameter
 */
class RecentConversationsAdapter :
    RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private val chatMessages: List<ChatMessage>
    private val conversionListener: ConversionListener

    constructor(chatMessages: List<ChatMessage>, conversionListener: ConversionListener) {
        this.chatMessages = chatMessages
        this.conversionListener = conversionListener
    }
    /**
     * Inner class representing a ViewHolder for a recent conversation item in the RecyclerView.
     *
     * @param itemView The view representing a recent conversation item.
     */
    inner class ConversionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerRecentConversionBinding
        /**
         * @constructor that takes a binding object for the recent conversation item view.
         *
         * @param itemContainerRecentConversionBinding The binding object for the recent conversation item view.
         */
        constructor(itemContainerRecentConversionBinding: ItemContainerRecentConversionBinding) : this(
            itemContainerRecentConversionBinding.root
        ) {
            binding = itemContainerRecentConversionBinding
        }
        /**
         * Sets the data for a recent conversation item in the ViewHolder.
         *
         * @param chatMessage The chat message representing the recent conversation.
         */
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

    /**
     * Decodes a Base64-encoded image string into a Bitmap.
     *
     * @param encodedImage The Base64-encoded image string.
     * @return A Bitmap representation of the decoded image.
     */
    private fun GetConversionImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    /**
     * Creates and returns a new [UserViewHolder] instance for the RecyclerView.
     *
     * @param parent The parent ViewGroup in which the ViewHolder will be displayed.
     * @param viewType The type of view to be created (not used in this implementation).
     * @return A new [ConversionViewHolder] instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionViewHolder {
        return ConversionViewHolder(
            ItemContainerRecentConversionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }
    /**
     * Returns the number of items in the adapter's data set.
     *
     * @return The total number of items in the data set.
     */
    override fun getItemCount(): Int {
        return chatMessages.size
    }

    /**
     * Binds data to a [ConversionViewHolder] at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: ConversionViewHolder, position: Int) {
        holder.SetData(chatMessages.get(position))
    }
}