package com.example.storyapp.Injection

import android.content.Context
import com.example.storyapp.data.UserStorage
import com.example.storyapp.api.APISettings
import com.example.storyapp.user.PreferensiUser
import com.example.storyapp.user.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injeksi {
    fun providerRepository(context: Context): UserStorage {
        val pref = PreferensiUser.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val servisAPI = APISettings.getApiService(user.token)
        return UserStorage.getInstance(pref, servisAPI)
    }
}
