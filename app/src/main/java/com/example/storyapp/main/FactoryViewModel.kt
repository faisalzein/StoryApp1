package com.example.storyapp.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Injection.Injeksi
import com.example.storyapp.data.UserStorage
import com.example.storyapp.view.RegisterViewModel
import com.example.storyapp.view.SigninViewModel

class FactoryViewModel(private val repository: UserStorage) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <i : ViewModel> create(modelClass: Class<i>): i {
        return when {
            modelClass.isAssignableFrom(ViewModelMain::class.java) -> {
                ViewModelMain(repository) as i
            }
            modelClass.isAssignableFrom(SigninViewModel::class.java) -> {
                SigninViewModel(repository) as i
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as i
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FactoryViewModel? = null
        @JvmStatic
        fun getInstance(context: Context): FactoryViewModel {
            if (INSTANCE == null) {
                synchronized(FactoryViewModel::class.java) {
                    INSTANCE = FactoryViewModel(Injeksi.providerRepository(context))
                }
            }
            return INSTANCE as FactoryViewModel
        }
    }
}