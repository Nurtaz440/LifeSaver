package com.example.ambulance.ui.client.ui.addLocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ambulance.MainActivity
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentLocationsBinding
import com.example.ambulance.databinding.FragmentUpdateLocationBinding
import com.example.ambulance.model.UserLocations
import com.example.ambulance.ui.client.ProfileViewModel

class UpdateLocationFragment : Fragment() {
    private var _binding: FragmentUpdateLocationBinding? = null
    val binding get() = _binding!!

    private val args : UpdateLocationFragmentArgs by navArgs()
    lateinit var locations : UserLocations
    lateinit var profileViewModel: ProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locations = args.locations!!
        binding.evNameUpdate.setText(locations.city)
        binding.evStreetUpdate.setText(locations.street)
        binding.evVillageUpdate.setText(locations.village)
        binding.evHomeUpdate.setText(locations.home)

        profileViewModel = (activity as MainActivity).profileViewModel

        binding.button.setOnClickListener {
            val cityName = binding.evNameUpdate.text.toString().trim()
            val streetName = binding.evStreetUpdate.text.toString().trim()
            val villageName = binding.evVillageUpdate.text.toString().trim()
            val homeName = binding.evHomeUpdate.text.toString().trim()
            if (cityName.isNotEmpty()){
                val note = UserLocations(locations.id,cityName,streetName,villageName,homeName)
                profileViewModel.update(note)
                
                it.findNavController().navigate(R.id.locationsFragment)
            }else{
                Toast.makeText(context,"Please enter message",Toast.LENGTH_SHORT).show()
            }
        }
    }
}