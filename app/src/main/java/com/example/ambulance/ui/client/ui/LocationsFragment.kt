package com.example.ambulance.ui.client.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ambulance.MainActivity
import com.example.ambulance.R
import com.example.ambulance.adapter.MyListAdapter
import com.example.ambulance.databinding.FragmentClientHomeBinding
import com.example.ambulance.databinding.FragmentLocationsBinding
import com.example.ambulance.model.UserLocations
import com.example.ambulance.ui.client.ProfileViewModel
import com.example.ambulance.ui.client.ui.addLocation.AdLocationFragment


class LocationsFragment : Fragment() {
    private var _binding: FragmentLocationsBinding? = null
    val binding get() = _binding!!
    lateinit var profileViewModel: ProfileViewModel
    private lateinit var locationAdapter : MyListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener{
//            findNavController().navigate(R.id.adLocationFragment)
            val fragment5 = AdLocationFragment()

            val ft: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.secondNavHostFragment, fragment5)
                .addToBackStack(null) // Add the fragment to the back stack
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        }

        profileViewModel = (activity as MainActivity).profileViewModel
        setUpRecyclerView()
    }
    private fun setUpRecyclerView(){
        locationAdapter = MyListAdapter(requireContext())
        binding.rvLocations.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            setHasFixedSize(true)
            adapter = locationAdapter
        }
        activity?.let{
            profileViewModel.getAllLocations().observe(viewLifecycleOwner) { location ->

                locationAdapter.differ.submitList(location)
//                updateUi(location)
            }
        }
    }
//    private fun updateUi(location:List<UserLocations>){
//        if (location.isNotEmpty()){
//            binding.rvLocations.visibility = View.VISIBLE
//        }
//    }
}