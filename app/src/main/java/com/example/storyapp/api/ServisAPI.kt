package com.example.storyapp.api

import com.example.storyapp.response.RegisterRespon
import com.example.storyapp.response.SigninRespon
import com.example.storyapp.response.AddStoryRespon
import com.example.storyapp.response.GetStoryRespon
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ServisAPI {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterRespon

    @FormUrlEncoded
    @POST("login")
    suspend fun enter(
        @Field("email") email: String,
        @Field("password") password: String
    ): SigninRespon

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddStoryRespon

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") token: String,
    ) : GetStoryRespon
}