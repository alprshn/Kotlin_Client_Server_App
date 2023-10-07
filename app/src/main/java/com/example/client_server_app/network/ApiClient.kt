package com.example.client_server_app.network

import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.scalars.ScalarsConverterFactory
/**
 * @author Alper Sahin
 * Singleton class responsible for creating and providing an instance of Retrofit for making network requests.
 * @property ApiClient the name of this class
 */
class ApiClient {
    companion object {
        private var retrofit: Retrofit? = null
        /**
         * Get the Retrofit client instance.
         *
         * @return The Retrofit client instance.
         */
        fun GetClient(): Retrofit? {
            if (retrofit == null) {
                retrofit = Builder().baseUrl("https://fcm.googleapis.com/fcm/")
                    .addConverterFactory(ScalarsConverterFactory.create()).build()
            }
            return retrofit
        }
    }

}