package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.activities.ChatActivity
import com.example.client_server_app.databinding.ItemContainerReceivedMessageBinding
import com.example.client_server_app.databinding.ItemContainerSentMessageBinding

import com.example.client_server_app.models.ChatMessage
/**
 * An adapter class for populating a RecyclerView with chat messages in a chat interface.
 *
 * @param receiverProfileImage The profile image of the receiver (can be null if not available).
 * @param chatMessage The list of chat messages to display.
 * @param senderId The unique identifier of the message sender.
 * @property ChatAdapter the name of this class.
 */
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

    /**
     * View holder for sent chat messages.
     *
     * @param itemView The view representing a sent message.
     */
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerSentMessageBinding
        /**
         * @constructor that takes a binding object for the sent message view.
         *
         * @param itemContainerSentMessageBinding The binding object for the sent message view.
         */
        constructor(itemContainerSentMessageBinding: ItemContainerSentMessageBinding) : this(
            itemContainerSentMessageBinding.root
        ) {
            binding = itemContainerSentMessageBinding
        }

        /**
         * Sets the data for a sent chat message in the ViewHolder.
         *
         * @param chatMessage The chat message to display.
         */
        fun SetData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }
    }

    /**
     * View holder for received chat messages.
     *
     * @param itemView The view representing a received message.
     */
    inner class ReceiverMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerReceivedMessageBinding
        /**
         * @constructor that takes a binding object for the received message view.
         *
             * @param itemContainerReceivedMessageBinding The binding object for the received message view.
         */
        constructor(itemContainerReceivedMessageBinding: ItemContainerReceivedMessageBinding) : this(
            itemContainerReceivedMessageBinding.root
        ) {
            binding = itemContainerReceivedMessageBinding
        }

        /**
         * Sets the data for a received chat message in the ViewHolder.
         *
         * @param chatMessage The chat message to display.
         * @param receiverProfileImage The profile image of the receiver.
         */
        fun SetData(chatMessage: ChatMessage, receiverProfileImage: Bitmap?) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
            if (receiverProfileImage != null) {
                binding.imageProfile.setImageBitmap(receiverProfileImage)
            }
        }
    }

    /**
     * Determines the view type for an item in the RecyclerView.
     *
     * @param position The position of the item in the data set.
     * @return The view type for the item (either VIEW_TYPE_SENT or VIEW_TYPE_RECEIVED).
     */
    override fun getItemViewType(position: Int): Int {
        if (chatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT
        } else {
            return VIEW_TYPE_RECEIVED
        }
    }

    /**
     * Creates and returns a new ViewHolder for an item in the RecyclerView.
     *
     * @param parent The parent ViewGroup in which the ViewHolder will be displayed.
     * @param viewType The view type of the item.
     * @return A new ViewHolder instance.
     */
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
    /**
     * Returns the total number of items in the data set.
     *
     * @return The total number of chat messages.
     */
    override fun getItemCount(): Int {
        return chatMessage.size
    }

    /**
     * Binds data to a ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
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