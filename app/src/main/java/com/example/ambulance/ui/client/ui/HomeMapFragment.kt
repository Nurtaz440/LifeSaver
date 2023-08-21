package com.example.ambulance.ui.client.ui

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentHomeMapBinding
import com.example.ambulance.ui.client.ui.addLocation.AddOtherFragment
import com.example.ambulance.util.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.user_location.UserLocationLayer


class HomeMapFragment : Fragment() {
    private var _binding: FragmentHomeMapBinding? = null
    val binding get() = _binding!!
    lateinit var locationMapKit: UserLocationLayer


    private var mapObjects: MapObjectCollection? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        val mapKit: MapKit = MapKitFactory.getInstance()
        var probki = mapKit.createTrafficLayer(binding.mapView.mapWindow)
        probki.isTrafficVisible = true

        requestLocation()
        locationMapKit = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        locationMapKit.isVisible = true
        binding.mapView.map.move(
            CameraPosition(Point(41.315355, 69.252374), 14f, 0.0f, 0.0f)
        )


        mapObjects = binding.mapView.map.mapObjects.addCollection()
        binding.cvCurrentLocation.setOnClickListener {
            requestLocation()
        }

        binding.cvBottomSheet.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_card))
        clickImages()

    }

    private fun clickSendMessage(title: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_dialog)
        val yesBtn = dialog.findViewById(R.id.btn) as MaterialButton
        val exit = dialog.findViewById(R.id.btn_cansel) as MaterialButton
        val addLocation = dialog.findViewById(R.id.btn_add) as MaterialButton
        val myLocation = dialog.findViewById(R.id.tv_my_current_loc) as TextView
        val otherLocation = dialog.findViewById(R.id.tv_my_home) as TextView

        val firebase by lazy { FirebaseFirestore.getInstance() }

        val query3 = firebase.collection("otherLocation").document("first")
        query3.get().addOnSuccessListener { p0 ->
            if (p0.exists()) {
                otherLocation.visibility = View.VISIBLE
                val document = p0.data!!

                    otherLocation.text = document["title"]!! as String
                    val otherLocLat = document["lat"]!! as String
                    val otherLocLong = document["long"]!! as String


                    myLocation.setOnClickListener {
                        yesBtn.setOnClickListener {
                            val location = SharedPreferencesManager.getLocation(requireContext())
                            val lat = location.first
                            val long = location.second
                            Log.d("get value lat", "$lat")
                            Log.d("get value long", "$long")
                            val details = binding.etComment.text.trim().toString()
                            sendLocationToAdmin(lat!!, long!!, title, details)
                            dialog.dismiss()
                        }
                        myLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.baseline_radio_button_checked_24, 0, 0, 0
                        )
                        otherLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.baseline_radio_button_unchecked_24, 0, 0, 0
                        )

                    }
                    otherLocation.setOnClickListener {
                        yesBtn.setOnClickListener {

                            Log.d("get value lat", "$otherLocLat")
                            Log.d("get value long", "$otherLocLong")
                            val details = binding.etComment.text.trim().toString()
                            sendLocationToAdmin(otherLocLat!!, otherLocLong!!, title, details)
                            dialog.dismiss()
                        }
                        otherLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.baseline_radio_button_checked_24, 0, 0, 0
                        )
                        myLocation.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.baseline_radio_button_unchecked_24, 0, 0, 0
                        )
                    }


                }

        }
        yesBtn.setOnClickListener {
            val location = SharedPreferencesManager.getLocation(requireContext())
            val lat = location.first
            val long = location.second
            Log.d("get value lat", "$lat")
            Log.d("get value long", "$long")
            val details = binding.etComment.text.trim().toString()
            sendLocationToAdmin(lat!!, long!!, title, details)
            dialog.dismiss()
        }
        exit.setOnClickListener {
            //exitGame()
            dialog.dismiss()
        }
        addLocation.setOnClickListener {
            dialog.dismiss()
//            findNavController().navigate(R.id.addOtherFragment)
            val fragment5 = AddOtherFragment()

            val ft: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.secondNavHostFragment, fragment5)
                .addToBackStack(null) // Add the fragment to the back stack
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.create()
        dialog.show()
    }


    private fun clickImages() {
        binding.etSearch.setOnClickListener {
            binding.etSearch.setBackgroundColor(Color.GREEN)
            binding.ivFire.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivAmbulance.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivGas.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivPolice.setBackground(resources.getDrawable(R.drawable.bg_image))
            logicClickSendMessage("Favqulodda Qo'ng'iroq")
        }
        binding.llFire.setOnClickListener {
            binding.etSearch.setBackgroundColor(Color.RED)
            binding.ivFire.setBackground(resources.getDrawable(R.drawable.bg_image_blue))
            binding.ivAmbulance.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivGas.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivPolice.setBackground(resources.getDrawable(R.drawable.bg_image))
            logicClickSendMessage("101")
        }
        binding.llPolice.setOnClickListener {
            binding.etSearch.setBackgroundColor(Color.RED)
            binding.ivFire.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivAmbulance.setBackground(resources.getDrawable(R.drawable.bg_image_blue))
            binding.ivGas.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivPolice.setBackground(resources.getDrawable(R.drawable.bg_image))
            logicClickSendMessage("102")
        }
        binding.llGas.setOnClickListener {
            binding.etSearch.setBackgroundColor(Color.RED)
            binding.ivFire.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivAmbulance.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivGas.setBackground(resources.getDrawable(R.drawable.bg_image_blue))
            binding.ivPolice.setBackground(resources.getDrawable(R.drawable.bg_image))
            logicClickSendMessage("104")
        }
        binding.llAmbulance.setOnClickListener {
            binding.etSearch.setBackgroundColor(Color.RED)
            binding.ivFire.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivAmbulance.setBackground(resources.getDrawable(R.drawable.bg_image))
            binding.ivPolice.setBackground(resources.getDrawable(R.drawable.bg_image_blue))
            binding.ivGas.setBackground(resources.getDrawable(R.drawable.bg_image))
            logicClickSendMessage("103")
        }
    }

    private fun logicClickSendMessage(title: String) {
        binding.ivMessageSend.setOnClickListener {
            if (binding.etComment.text.isNotEmpty()) {
                clickSendMessage(title)
            } else {
                return@setOnClickListener
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 0
            )
            return
        } else {
            showCurrentLocation()
        }

    }

    private fun showCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            locationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = Point(it.latitude, it.longitude)
                    moveCameraToLocation(currentLocation)
                    Log.d("get value from current lat", it.latitude.toString())
                    Log.d("get value from current long", it.longitude.toString())
                    if (isAdded) {
                        SharedPreferencesManager.setCurrentLocation(
                            context = requireContext(),
                            lat = it.latitude.toString(),
                            long = it.longitude.toString()
                        )
                    } //   getCurrentLocation(currentLocation)
                } ?: run {
                    Toast.makeText(
                        context, "Unable to retrieve the current location.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendLocationToAdmin(lat: String, long: String, title: String, desc: String) {
        // Assuming you have set up Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("locations")
        Log.d("get value lat", "$lat")
        Log.d("get value long", "$long")
        // val locations = LocationAdmin(lat,long)
        // Log.d("get value loc","$locations")
        val dataMap = HashMap<String, Any>()
        dataMap["lati"] = lat
        dataMap["longi"] = long
        dataMap["title"] = title
        dataMap["desc"] = desc
        ref.setValue(dataMap).addOnSuccessListener {
            // Data set successfully
            // Do something if needed
        } // This sends the location to the "locations" node in the database

    }

    private fun moveCameraToLocation(location: Point) {
        locationMapKit.isVisible = true
        val newPosition = CameraPosition(location, 15.0f, 0.0f, 0.0f)
        binding.mapView.map.move(
            newPosition, Animation(Animation.Type.SMOOTH, 1.0f), null
        )
    }

}