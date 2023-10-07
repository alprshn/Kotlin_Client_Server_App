package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerUserBinding
import com.example.client_server_app.listeners.UserListener
import com.example.client_server_app.models.User
/**
 * An adapter class for populating a RecyclerView with a list of [User] objects.
 *
 * @param users The list of users to display in the RecyclerView.
 * @param userListener The listener to handle user item click events.
 * @property UsersAdapter the name of this class.
 * @constructor creates an parameter
 */
class UsersAdapter(private var users: List<User>, private var userListener: UserListener) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var binding: ItemContainerUserBinding

        /**
         * Constructor that takes a binding object for the user item view.
         *
         * @param itemContainerUserBinding The binding object for the user item view.
         */
        constructor(itemContainerUserBinding: ItemContainerUserBinding) : this(
            itemContainerUserBinding.root
        ) {
            binding = itemContainerUserBinding
        }

        /**
         * Sets the data for a user item in the ViewHolder.
         *
         * @param user The user object to display.
         */
        fun SetUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(GetUserImage(user.image))
            binding.root.setOnClickListener { _ -> userListener.OnUserClicked(user) }
        }
    }

    /**
     * Decodes a Base64-encoded image string into a Bitmap.
     *
     * @param encodedImage The Base64-encoded image string.
     * @return A Bitmap representation of the decoded image.
     */
    private fun GetUserImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    /**
     * Creates and returns a new [UserViewHolder] instance for the RecyclerView.
     *
     * @param parent The parent ViewGroup in which the ViewHolder will be displayed.
     * @param viewType The type of view to be created (not used in this implementation).
     * @return A new [UserViewHolder] instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var itemContainerUserBinding: ItemContainerUserBinding = ItemContainerUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(itemContainerUserBinding)
    }

    /**
     * Returns the number of items in the adapter's data set.
     *
     * @return The total number of items in the data set.
     */
    override fun getItemCount(): Int {
        return users.size
    }

    /**
     * Binds data to a [UserViewHolder] at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.SetUserData(users.get(position))
    }

    /**
     * Updates the adapter's data with a filtered list of users and notifies the RecyclerView of the change.
     *
     * @param filteredList The filtered list of users to display.
     */
    fun filterList(filteredList: ArrayList<User>) {
        users = filteredList;
        notifyDataSetChanged();
    }

}