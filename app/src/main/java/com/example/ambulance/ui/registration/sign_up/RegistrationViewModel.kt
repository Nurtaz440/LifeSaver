package com.example.ambulance.ui.registration.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ambulance.model.UserDetails
import com.example.ambulance.repository.UserRepository
import kotlinx.coroutines.launch

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<Boolean>()
    val registrationResult: LiveData<Boolean> = _registrationResult

    //val allUsers: LiveData<List<UserDetails>> = userRepository.allUsers

    fun registerUser(name: String,surname:String,number:String) {
        val user = UserDetails(name = name, surName = surname, number = number)
        viewModelScope.launch {
            try {
                userRepository.insertUser(user)
                _registrationResult.postValue(true)
            } catch (e: Exception) {
                _registrationResult.postValue(false)
            }
        }
    }
    class RegistrationViewModelFactory(private val userRepository: UserRepository) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
                return RegistrationViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}