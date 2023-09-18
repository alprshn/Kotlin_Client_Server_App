package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.databinding.ItemContainerUserBinding
import com.example.client_server_app.models.User
import com.example.client_server_app.utilities.Constants

class UsersAdapter {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ItemContainerUserBinding
        lateinit var adapter: UsersAdapter
        constructor(itemContainerUserBinding: ItemContainerUserBinding) : this(
            itemContainerUserBinding.root
        ) {
            binding = itemContainerUserBinding
        }

        fun SetUserData(user: User) {
            binding.textName.text = user.name
            binding.textEmail.text = user.email
            binding.imageProfile.setImageBitmap(adapter.GetUserImage(user.image))

        }
    }

    private fun GetUserImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}