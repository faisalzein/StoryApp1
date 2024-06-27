package com.example.storyapp.response

import com.google.gson.annotations.SerializedName

data class AddStoryRespon(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)