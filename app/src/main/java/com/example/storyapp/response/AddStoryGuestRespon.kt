package com.example.storyapp.response

import com.google.gson.annotations.SerializedName

data class AddStoryGuestRespon(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)