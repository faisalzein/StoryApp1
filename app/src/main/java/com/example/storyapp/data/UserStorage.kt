package com.example.storyapp.data

import com.example.storyapp.response.RegisterRespon
import com.example.storyapp.response.SigninRespon
import com.example.storyapp.response.GetStoryRespon
import com.example.storyapp.api.ServisAPI
import com.example.storyapp.user.ModelUser
import com.example.storyapp.user.PreferensiUser
import kotlinx.coroutines.flow.Flow


class UserStorage private constructor(
    private val preferensiUser: PreferensiUser,
    private val servisAPI: ServisAPI
) {
    suspend fun saveSession(user: ModelUser) {
        preferensiUser.saveSession(user)
    }

    fun getSession(): Flow<ModelUser> {
        return preferensiUser.getSession()
    }


    suspend fun register(name: String, email: String, password: String): RegisterRespon {
        return servisAPI.register(name, email, password)
    }


    suspend fun enter(email: String, password: String): SigninRespon {
        return servisAPI.enter(email, password)
    }


    suspend fun getStory(token: String): GetStoryRespon {
        return servisAPI.getStory(token)
    }


    suspend fun out() {
        preferensiUser.out()
    }


    suspend fun settingAuth(user: ModelUser) = preferensiUser.saveSession(user)


    companion object {
        @Volatile
        private var instance: UserStorage? = null

        fun getInstance(
            preferensiUser: PreferensiUser,
            servisAPI: ServisAPI
        ): UserStorage =
            instance ?: synchronized(this) {
                instance ?: UserStorage(preferensiUser, servisAPI)
            }.also { instance = it }
    }
}
