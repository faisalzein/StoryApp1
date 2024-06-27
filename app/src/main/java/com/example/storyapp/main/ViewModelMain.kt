package com.example.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserStorage
import com.example.storyapp.response.ListStoryItem
import com.example.storyapp.response.AddStoryGuestRespon
import com.example.storyapp.user.ModelUser
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ViewModelMain(private val repository: UserStorage) : ViewModel() {

    private val _storyList = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _storyList

    private val _isLoading = MutableLiveData<Boolean>()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun getSession(): LiveData<ModelUser> {
        return repository.getSession().asLiveData()
    }

    fun out() {
        viewModelScope.launch {
            repository.out()
        }
    }

    fun getAllStory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = repository.getSession().first().token
                val storyResponse = repository.getStory("Bearer $token")
                val message = storyResponse.message

                val nonNullStoryList = storyResponse.listStory?.filterNotNull() ?: emptyList()

                _storyList.value = nonNullStoryList
                _message.value = message ?: "Unknown Message"
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, AddStoryGuestRespon::class.java)
                val errorMessage = errorBody.message
                _message.value = errorMessage ?: "Unknown Error"
            } catch (e: Exception) {
                _message.value = e.message ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
