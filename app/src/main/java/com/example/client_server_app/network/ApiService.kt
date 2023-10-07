package com.example.client_server_app.network

import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
/**
 * @author Alper Sahin
 * Retrofit API interface for defining network endpoints and requests.
 * @property ApiService the name of this interface
 */
interface ApiService {
    /**
     * Send a message to a specified endpoint.
     *
     * @param headers The headers to include in the request.
     * @param messageBody The message content to send in the request body.
     * @return A Retrofit Call representing the network request.
     */
    @POST("send")
    fun SendMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body messageBody: String
    ): retrofit2.Call<String>
}