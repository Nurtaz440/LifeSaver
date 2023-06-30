package com.example.ambulance.ui.registration.sign_in

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentSignInBinding
import com.example.ambulance.databinding.FragmentSignUpBinding
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel : AuthViewModel
    private lateinit var prefs: DataStore<Preferences>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = context?.createDataStore(name = "onBoard")!!

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)).get(AuthViewModel::class.java)

        viewModel.getUserData().observe(this) {
            if (it != null) {
              //  findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.tvError.apply {
            visibility = View.GONE
        }

        binding.button.setOnClickListener{
            val email = binding.editTextTextEmailAddress.text.toString()
            val pass = binding.editTextTextPassword.text.toString()

            if (!email.isEmpty() && !pass.isEmpty()){
                binding.progressBar.visibility = View.VISIBLE
                with(viewModel) {
                    isLoading.observe(viewLifecycleOwner) {
                        if (!it) binding.progressBar.visibility = View.GONE
                    }
                }
                viewModel.signIn(email,pass, onSuccess = {
                    binding.tvError.text = ""
                    val currentUser = viewModel.firebaseUser()
                    val role = viewModel.getRole()
                    if (currentUser != null && role == "admin") {
                        // Admin login, navigate to admin page
                        navigateToAdminPage()

                    } else if (currentUser != null && role == "client"){
                        // Client login, navigate to client page
                        navigateToClientPage()
                    }
                },
                onError = { errorMessage ->
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.apply {
                        visibility = View.VISIBLE
                        text = errorMessage
                    }
                   // Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                })
            }else{
                binding.tvError.apply {
                    visibility = View.VISIBLE
                    text = "Enter your email and password!!"
                }
            }

        }
    }
    private fun navigateToAdminPage() {
        // TODO: Implement admin page navigation
        findNavController().navigate(R.id.action_signInFragment_to_homeAdminFragment)

        lifecycleScope.launch {
            saveOnboardingAdmin()
        }
    }

    private fun navigateToClientPage() {
        // TODO: Implement client page navigation
        findNavController().navigate(R.id.action_signInFragment_to_clientHomeFragment)
        lifecycleScope.launch {
            saveOnboardingClient()
        }
    }
    //suspend function to save the onboarding to datastore
    suspend fun saveOnboardingAdmin() {
        prefs.edit {
            val oneTime = true
            it[preferencesKey<Boolean>("onBoardAdmin")] = oneTime

        }
    }
    //suspend function to save the onboarding to datastore
    suspend fun saveOnboardingClient() {
        prefs.edit {
            val oneTime = true
            it[preferencesKey<Boolean>("onBoardClient")] = oneTime

        }
    }
}