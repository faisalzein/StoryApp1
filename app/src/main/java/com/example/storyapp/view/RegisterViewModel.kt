package com.example.storyapp.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserStorage
import com.example.storyapp.response.RegisterRespon
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepo: UserStorage) : ViewModel() {

    private val _signupResponse = MutableLiveData<RegisterRespon>()


    private val _isLoading = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> = _isLoading
    private val _isError = MutableLiveData<String>()
    val isError: LiveData<String> = _isError

    fun signup(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {

                val message = userRepo.register(name, email, password)
                _signupResponse.value = message
            } catch (e: Exception) {
                _isError.value = e.message ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
