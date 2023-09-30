package com.example.client_server_app.network

import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        fun GetClient(): Retrofit? {
            if (retrofit == null) {
                retrofit = Builder().baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create()).build()
            }
            return retrofit
        }
    }

}