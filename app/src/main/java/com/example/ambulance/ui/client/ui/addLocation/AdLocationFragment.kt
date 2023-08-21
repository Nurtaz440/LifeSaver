package com.example.ambulance.ui.client.ui.addLocation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ambulance.MainActivity
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentAdLocationBinding
import com.example.ambulance.databinding.FragmentLocationsBinding
import com.example.ambulance.model.UserLocations
import com.example.ambulance.ui.client.ProfileViewModel
import com.example.ambulance.ui.client.ui.ComplainFragment
import com.example.ambulance.ui.client.ui.LocationsFragment
import com.google.android.material.snackbar.Snackbar

class AdLocationFragment : Fragment() {
    private var _binding: FragmentAdLocationBinding? = null
    val binding get() = _binding!!
    lateinit var profileViewModel: ProfileViewModel
 //   private lateinit var mView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdLocationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        profileViewModel = (activity as MainActivity).profileViewModel
       // mView = view

        binding.button.setOnClickListener {
            saveLocation()
        }
    }
    private fun saveLocation(){
        val cityName = binding.evCity.text.toString().trim()
        val streetName = binding.evStreet.text.toString().trim()
        val villageName = binding.evVillage.text.toString().trim()
        val homeName = binding.evHome.text.toString().trim()

        if (cityName.isNotEmpty()){
            val location = UserLocations(0,cityName,streetName,villageName,homeName)
            profileViewModel.addNote(location)


           // view.findNavController().navigate(R.id.locationsFragment)
            val fragment5 = LocationsFragment()

            val ft: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.secondNavHostFragment, fragment5)
                .addToBackStack(null) // Add the fragment to the back stack
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

        }else{
            Toast.makeText(context,"Please enter values",Toast.LENGTH_SHORT).show()
        }
    }
}