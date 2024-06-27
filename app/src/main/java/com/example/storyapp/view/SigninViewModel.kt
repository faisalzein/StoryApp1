package com.example.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserStorage
import com.example.storyapp.response.SigninRespon
import com.example.storyapp.user.ModelUser
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SigninViewModel(private val repository: UserStorage) : ViewModel() {

    fun saveSession(user: ModelUser) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<String?>()
    val isError: MutableLiveData<String?> get() = _isError

    private val _loginResponse = MutableLiveData<SigninRespon>()
    val enterRespon: LiveData<SigninRespon> = _loginResponse

    fun enter(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.enter(email, password)
                val loginResult = response.loginResult

                val userId = loginResult?.userId ?: ""
                val name = loginResult?.name ?: ""
                val token = loginResult?.token ?: ""

                val user = ModelUser(
                    userId = userId,
                    name = name,
                    email = email,
                    token = token,
                    isLogin = true
                )
                settingAuth(user)
                _loginResponse.postValue(response)
            } catch (e: HttpException) {
                handleHttpException(e)
            } catch (e: Exception) {
                handleGeneralException(e)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    private fun handleHttpException(e: HttpException) {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, SigninRespon::class.java)
        _isError.postValue(errorBody.message)
    }

    private fun handleGeneralException(e: Exception) {
        _isError.postValue(e.message ?: "An unexpected error occurred")
    }

    private fun settingAuth(userModel: ModelUser) {
        viewModelScope.launch {
            repository.settingAuth(userModel)
        }
    }

}