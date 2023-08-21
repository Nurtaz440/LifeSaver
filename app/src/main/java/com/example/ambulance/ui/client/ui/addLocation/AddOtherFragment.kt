package com.example.ambulance.ui.client.ui.addLocation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentAddOtherBinding
import com.example.ambulance.ui.client.ui.ComplainFragment
import com.example.ambulance.ui.client.ui.HomeMapFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.image.ImageProvider


class AddOtherFragment : Fragment() {
    private var _binding: FragmentAddOtherBinding? = null
    val binding get() = _binding!!
    private var mapObjectLayer: MapObjectCollection? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddOtherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapViewAddLocation.getMap().move(
            CameraPosition(
                Point(
                    41.265593,
                    69.218110
                ),
                10.0f,
                0.0f,
                0.0f
            )
        ) // Example initial camera position


        binding.mapViewAddLocation.map.addInputListener(inputListener)

    }

    fun clickSendLocation(title: String, lat: Double, lon: Double) {
     //   findNavController().navigate(R.id.homeMapFragment)
        val fragment5 = HomeMapFragment()

        val ft: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        ft.replace(R.id.secondNavHostFragment, fragment5)
            .addToBackStack(null) // Add the fragment to the back stack
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        val db = Firebase.firestore
        // Create a new user with a first and last name
        val user = hashMapOf(
            "title" to title,
            "lat" to lat.toString(),
            "long" to lon.toString(),
        )

// Add a new document with a generated ID
        db.collection("otherLocation").document("first")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapViewAddLocation.onStart()
    }

    override fun onStop() {
        binding.mapViewAddLocation.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            val latitude = point.latitude
            val longitude = point.longitude
            val newWidth = 120 // Adjust these values as needed

            val newHeight = 120

            val iconDrawable2 =
                ContextCompat.getDrawable(requireContext(), R.drawable.location_icon)
            val iconBitmap2 = Bitmap.createScaledBitmap(
                drawableToBitmap(iconDrawable2!!),
                newWidth,
                newHeight,
                false
            )
            mapObjectLayer = binding.mapViewAddLocation.map.mapObjects.addCollection()
            mapObjectLayer!!.addPlacemark(
                Point(
                    latitude!!.toDouble(),
                    longitude!!.toDouble()
                ), ImageProvider.fromBitmap(iconBitmap2)
            )
            Log.d("CLICKED", "Tapped the point (${longitude}, ${latitude})")
            binding.btnAdd.setOnClickListener {
                if (binding.etTitleLocation.text.isNotEmpty()) {
                    val title = binding.etTitleLocation.text.trim().toString()
                    clickSendLocation(title, latitude, longitude)
                } else {
                    return@setOnClickListener
                }
            }
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            TODO("Not yet implemented")
        }

    }
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}