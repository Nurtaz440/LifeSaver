package com.example.ambulance.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthRepository(application: Application) {
    private var application: Application
    private var firebaseUserMutableLiveData: MutableLiveData<FirebaseUser>
    private var userLogged: MutableLiveData<Boolean>
    private var auth: FirebaseAuth
    var role = ""
    var isLoading = MutableLiveData<Boolean>(false)
    init {
        this.application = application
        firebaseUserMutableLiveData = MutableLiveData()
        userLogged = MutableLiveData()
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            firebaseUserMutableLiveData.postValue(auth.currentUser)
        }
    }

    fun getFireBaseUser(): MutableLiveData<FirebaseUser> {
        return firebaseUserMutableLiveData
    }

    fun getUserLogged(): MutableLiveData<Boolean> {
        return userLogged
    }

    fun register(email: String, pass: String,onSuccess: () -> Unit,onError: (String) -> Unit) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
            object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        isLoading.value = false
                        // firebaseUserMutableLiveData.postValue(auth.currentUser)
                        val user = auth.currentUser
                        onSuccess.invoke()
                        val userRef =
                            FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
                        val userData = HashMap<String, Any>()
                        userData["name"] = email
                        userData["role"] = "client" // Set the role as client for user registration

                        userRef.setValue(userData).addOnSuccessListener {
                          //  Toast.makeText(application.applicationContext, "Data inserted successfully", Toast.LENGTH_SHORT).show()
                        }

                    } else {
//                        Toast.makeText(application, p0.exception!!.message, Toast.LENGTH_LONG)
//                            .show()
                        onError.invoke(p0.exception?.message ?: "Unknown error occurred")
                        isLoading.value = false
                    }
                }

            }
        )
    }

    fun login(email: String, pass: String,onSuccess: () -> Unit, onError: (String) -> Unit) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {

                    if (p0.isSuccessful) {
                        isLoading.value = false
                        val user = auth.currentUser
                        val userRef =
                            FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                 role = dataSnapshot.child("role").getValue(String::class.java)!!
                                Log.d("TAG"+ "role",role )
                                onSuccess.invoke()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                    }else{
                        onError.invoke(p0.exception?.message ?: "Unknown error occurred")
                        isLoading.value = false
                    }
                }
            })
    }

    fun sigOut() {
        auth.signOut()
        userLogged.postValue(true)
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}