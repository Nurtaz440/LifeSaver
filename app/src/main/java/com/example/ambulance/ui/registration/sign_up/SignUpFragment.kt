package com.example.ambulance.ui.registration.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.database.AppDatabase
import com.example.ambulance.databinding.FragmentSignUpBinding
import com.example.ambulance.repository.UserRepository
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel : AuthViewModel
    private lateinit var registrationViewModel: RegistrationViewModel

    private lateinit var prefs: DataStore<Preferences>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = context?.createDataStore(name = "onBoard")!!

        viewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)).get(AuthViewModel::class.java)

        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        registrationViewModel = ViewModelProvider(this,
            RegistrationViewModel.RegistrationViewModelFactory(userRepository)
        )
            .get(RegistrationViewModel::class.java)
//            ViewModelProvider.AndroidViewModelFactory
//            .getInstance(requireActivity().application)).get(RegistrationViewModel::class.java)



        viewModel.getUserData().observe(this) {
            if (it != null) {
           //     findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.button.setOnClickListener{
            val name = binding.evName.text.toString()
            val surname = binding.evSurename.text.toString()
            val number = binding.evNumber.text.toString()
            val email = binding.editTextTextEmailAddress.text.toString()
            val pass = binding.editTextTextPassword.text.toString()

            if (!email.isEmpty() && !pass.isEmpty()){
                viewModel.register(email,pass,onSuccess = {
                    binding.progressBar.visibility = View.VISIBLE
                    with(viewModel) {
                        isLoading.observe(viewLifecycleOwner) {
                            if (!it) binding.progressBar.visibility = View.GONE
                        }
                    }
                    registrationViewModel.registerUser(name,surname,number)

                    // Admin login successful, navigate to admin page
                    binding.tvError.text = ""

                    findNavController().navigate(R.id.action_signUpFragment_to_clientHomeFragment)
                    lifecycleScope.launch {
                        saveOnboarding()
                    }
                },
                    onError = { errorMessage ->
                        // Show error message
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.text = errorMessage
                       // Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    })
            }else{
                binding.tvError.apply {
                    visibility = View.VISIBLE
                    text = "Enter your email and password!!"
                }
            }

        }
        registrationViewModel.registrationResult.observe(viewLifecycleOwner, { success ->
            if (success) {

                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
    //suspend function to save the onboarding to datastore
    suspend fun saveOnboarding() {
        prefs.edit {
            val oneTime = true
            it[preferencesKey<Boolean>("onBoardClient")] = oneTime

        }
    }
}