package com.example.ambulance.ui.client

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ambulance.model.UserLocations
import com.example.ambulance.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    val app: Application,
    private val userRepository: UserRepository
) : ViewModel() {
    //    val allUsers: LiveData<UserDetails> = userRepository.allUsers
    fun addNote(locations: UserLocations) = viewModelScope.launch {
        userRepository.addNote(locations)
    }

    fun update(locations: UserLocations) = viewModelScope.launch {
        userRepository.update(locations)
    }

    fun delete(locations: UserLocations)  = viewModelScope.launch {
        userRepository.delete(locations)
    }

    fun getAllLocations() = userRepository.getAllLocations()



    class ProfileViewModelFactory(val app: Application,private val userRepository: UserRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(app,userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}