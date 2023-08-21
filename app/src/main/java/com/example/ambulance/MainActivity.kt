package com.example.ambulance

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.preferencesKey

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ambulance.databinding.ActivityMainBinding
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import com.example.ambulance.util.SharedPreferencesManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.navigation.NavController
import com.example.ambulance.database.AppDatabase
import com.example.ambulance.repository.UserRepository
import com.example.ambulance.ui.client.ProfileViewModel
import com.example.ambulance.ui.registration.sign_up.SignUpFragmentDirections
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    private lateinit var _dataStore: DataStore<Preferences>
    lateinit var profileViewModel: ProfileViewModel
    var navController : NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
         navController = navHostFragment.navController

        setUpViewModel()

        _dataStore = createDataStore(name = "onBoard")
        lifecycleScope.launch {
            _dataStore.data.collectLatest {
                // Check if user is registered
                val isRegistered = SharedPreferencesManager.isRegistered(this@MainActivity)

                if (!isRegistered) {
                    // User is registered, navigate to HomeFragment
                  navController!!.navigate(R.id.signUpFragment)
                } else {
                    // User is not registered, stay on HomeFragment and show appropriate content
                    // ..
                    if (it[preferencesKey<Boolean>("onBoardAdmin")] == true) {
                        navController!!.navigate(R.id.homeAdminFragment)
                    } else if (it[preferencesKey<Boolean>("onBoardClient")] == true) {
                           navController!!.navigate(R.id.clientHomeFragment)
                    } else {
                        navController!!.navigate(R.id.signUpFragment)
                      //  navController.navigate(R.id.clientHomeFragment)
                    }
                }

            }
        }
    }

    override fun onBackPressed() {
            super.onBackPressed()

    }

    fun setUpViewModel(){
        val noteUserRepository = UserRepository(
            AppDatabase.getInstance(this)
        )
        val viewModelProvider = ProfileViewModel.ProfileViewModelFactory(
            application,
            noteUserRepository
        )
        profileViewModel = ViewModelProvider(
            this,
            viewModelProvider
        ).get(ProfileViewModel::class.java)
    }
}