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
import com.example.ambulance.ui.registration.sign_up.SignUpFragmentDirections

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    private lateinit var _dataStore: DataStore<Preferences>
   // private lateinit var viewModel : AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
//        viewModel = ViewModelProvider(this, AndroidViewModelFactory.getInstance(application))
//            .get(AuthViewModel::class.java)
//
//            val getUserDetails = viewModel.getUserDetails()

        _dataStore = createDataStore(name = "onBoard")
        lifecycleScope.launch {
            _dataStore.data.collectLatest {
                // Check if user is registered
                val isRegistered = SharedPreferencesManager.isRegistered(this@MainActivity)

                if (!isRegistered) {
                    // User is registered, navigate to HomeFragment
                  navController.navigate(R.id.signUpFragment)
                } else {
                    // User is not registered, stay on HomeFragment and show appropriate content
                    // ..
                    if (it[preferencesKey<Boolean>("onBoardAdmin")] == true) {
                        navController.navigate(R.id.homeAdminFragment)
                    } else if (it[preferencesKey<Boolean>("onBoardClient")] == true) {
                           navController.navigate(R.id.clientHomeFragment)
                    } else {
                        navController.navigate(R.id.signUpFragment)
                      //  navController.navigate(R.id.clientHomeFragment)
                    }
                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}