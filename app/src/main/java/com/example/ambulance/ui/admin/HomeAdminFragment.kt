package com.example.ambulance.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentClientHomeBinding
import com.example.ambulance.databinding.FragmentHomeAdminBinding
import com.example.ambulance.ui.registration.viewModel.AuthViewModel

class HomeAdminFragment : Fragment() {
    private var _binding: FragmentHomeAdminBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel : AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(requireActivity().application)).get(AuthViewModel::class.java)

        viewModel.getUserLogged().observe(this) {
            if (it) {
                findNavController().navigate(R.id.action_homeAdminFragment_to_signInFragment)
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener{
            viewModel.signOut()
        }
    }
}