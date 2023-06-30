package com.example.ambulance.ui.registration.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ambulance.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private var repository : AuthRepository
    private var userData : MutableLiveData<FirebaseUser>
    private var loggedStatus : MutableLiveData<Boolean>
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    fun getUserData() : MutableLiveData<FirebaseUser>{
        return userData
    }

    fun getUserLogged() : MutableLiveData<Boolean>{
        return loggedStatus
    }
    init {
        repository = AuthRepository(application)
        userData = repository.getFireBaseUser()
        loggedStatus = repository.getUserLogged()
    }

    fun register(email : String, pass: String,onSuccess: () -> Unit,onError: (String) -> Unit){
        repository.register(email,pass,onSuccess,onError)
        _isLoading = repository.isLoading
    }
    fun signIn(email: String,pass: String,onSuccess: () -> Unit,onError: (String) -> Unit){
        repository.login(email,pass,onSuccess,onError)
        _isLoading = repository.isLoading
    }
    fun signOut(){
        repository.sigOut()
    }
    fun firebaseUser():FirebaseUser?{
       return repository.getCurrentUser()
    }
    fun getRole():String{
        return repository.role
    }

}