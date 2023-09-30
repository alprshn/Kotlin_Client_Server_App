package com.example.client_server_app.network

import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ApiService {

    @POST("send")
    fun SendMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body messageBody: String
    ): retrofit2.Call<String>
}