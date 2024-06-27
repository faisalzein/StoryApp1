package com.example.storyapp.user

data class ModelUser(
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean
)