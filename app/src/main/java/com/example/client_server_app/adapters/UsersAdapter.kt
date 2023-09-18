package com.example.client_server_app.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.recyclerview.widget.RecyclerView
import com.example.client_server_app.utilities.Constants

class UsersAdapter {

    class UserViewHolder : RecyclerView.ViewHolder() {

    }
    private fun GetUserImage(encodedImage: String): Bitmap {
        val bytes: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}