package com.example.ambulance.ui.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.adapter.MyLocationListAdapter
import com.example.ambulance.databinding.FragmentHomeAdminBinding
import com.example.ambulance.model.LocationDetails
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import com.example.ambulance.util.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class HomeAdminFragment : Fragment(), Session.RouteListener {
    private var _binding: FragmentHomeAdminBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    // private var drivingRouter: DrivingRouter? = null
    //   private lateinit var mapObjects: MapObjectCollection
    private lateinit var locationList: MutableList<LocationDetails>
    private var drivingRouter: PedestrianRouter? = null
    private var polylineMapObject: PolylineMapObject? = null

    private var mapObjectLayer: MapObjectCollection? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        ).get(AuthViewModel::class.java)

        viewModel.getUserLogged().observe(requireActivity()) {
            if (it) {
                findNavController().navigate(R.id.action_homeAdminFragment_to_signInFragment)
//                findNavController().popBackStack()
            }
        }
        // Set registration flag to false
        SharedPreferencesManager.setRegistered(requireContext(), true)
        locationList = mutableListOf()
        requestLocation()
        binding.button.setOnClickListener {
            viewModel.signOut()
        }
        binding.mapView.map.isRotateGesturesEnabled = true
        mapObjectLayer = binding.mapView.map.mapObjects.addCollection()
        mapObjectLayer!!.clear()
        binding.mapView.map.move(
            CameraPosition(
                Point(
                    41.265593,
                    69.218110
                ), 15.0f, 0.0f, 0.0f
            )
        )

        val mapKit: MapKit = MapKitFactory.getInstance()
        var probki = mapKit.createTrafficLayer(binding.mapView.mapWindow)
        probki.isTrafficVisible = true


        binding.cvNotif.setOnClickListener {
            binding.mapView.visibility = View.GONE
            binding.button.visibility = View.GONE
            binding.appBarLayout.visibility = View.VISIBLE
            binding.lvNotif.visibility = View.VISIBLE
// Fetch location data from Firebase Realtime Database
            fetchLocations()
        }

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


        } else {
            Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocations() {
        // Attach a ValueEventListener to retrieve data from Firebase
        val locationsRef = FirebaseDatabase.getInstance().getReference("locations")
        locationsRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("MissingPermission")
            override fun onDataChange(snapshot: DataSnapshot) {
                locationList.clear()
                // Loop through the data snapshot to populate the locationList

                val locationLat = snapshot.child("lati").getValue(String::class.java)
                val locationLong = snapshot.child("longi").getValue(String::class.java)
                val title = snapshot.child("title").getValue(String::class.java)
                val details = snapshot.child("desc").getValue(String::class.java)
                Log.d("Get value", "$locationLat  $locationLong")
            //    locationList.add(LocationAdmin(locationLat!!, locationLong!!))
                locationList.add(LocationDetails(title!!,details!!))


                if (isAdded()) {
                    val myListAdapter = MyLocationListAdapter(requireActivity(), title!!, details!!,locationList, R.drawable.baseline_mark_email_unread_24)
                    binding.lvNotif.adapter = myListAdapter
//                    val adapter = ArrayAdapter(
//                        requireActivity(),
//                        android.R.layout.simple_list_item_1,
//                        locationList
//                    )

                    //  binding.lvNotif.adapter = adapter
// using this method, we can do whatever we want which will prevent   **java.lang.IllegalStateException: Fragment not attached to Activity** exception.

                }


                binding.lvNotif.setOnItemClickListener { parent, view, position, id ->
                    binding.mapView.visibility = View.VISIBLE
                    binding.button.visibility = View.VISIBLE
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.lvNotif.visibility = View.GONE

                    val newWidth = 120 // Adjust these values as needed

                    val newHeight = 120

                    val iconDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.icon_navigator)
                    val iconDrawable2 =
                        ContextCompat.getDrawable(requireContext(), R.drawable.location_icon)
// Load the drawable and adjust its size
                    val iconBitmap = Bitmap.createScaledBitmap(
                        drawableToBitmap(iconDrawable!!),
                        newWidth,
                        newHeight,
                        false
                    )
                    val iconBitmap2 = Bitmap.createScaledBitmap(
                        drawableToBitmap(iconDrawable2!!),
                        newWidth,
                        newHeight,
                        false
                    )

                    mapObjectLayer!!.addPlacemark(
                        Point(41.265593, 69.218110),
                        ImageProvider.fromBitmap(iconBitmap)
                    )
                    mapObjectLayer!!.addPlacemark(
                        Point(
                            locationLat!!.toDouble(),
                            locationLong!!.toDouble()
                        ), ImageProvider.fromBitmap(iconBitmap2)
                    )


                    val locationA = Point(
                        41.265593,
                        69.218110
                    )
                    // Replace with the latitude and longitude of location A
                    val locationB = Point(
                        locationLat.toDouble(),
                        locationLong.toDouble()
                    )

                    submitRequest(locationA, locationB)

                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error
            }
        })
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

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }


    private fun submitRequest(ROUTE_START_LOCATION: Point, ROUTE_END_LOCATION: Point) {
        Log.d(
            "Get submitRequest",
            "${ROUTE_START_LOCATION.latitude},${ROUTE_START_LOCATION.longitude}," +
                    "  ${ROUTE_END_LOCATION.latitude} ${ROUTE_END_LOCATION.longitude}"
        )


        if (polylineMapObject != null) {
            mapObjectLayer?.remove(polylineMapObject!!)
        }
        polylineMapObject = null
        val requestPoints = ArrayList<RequestPoint>()
        requestPoints.clear()
        val timeOptions = TimeOptions()
        drivingRouter = TransportFactory.getInstance().createPedestrianRouter()


        requestPoints.add(RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null))
        requestPoints.add(RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null))
        drivingRouter!!.requestRoutes(requestPoints, timeOptions, this)
    }

    override fun onMasstransitRoutes(list: MutableList<Route>) {
        if (!list.isEmpty()) {
            val drivingRoute = list.get(0)
            polylineMapObject =
                mapObjectLayer!!.addPolyline(drivingRoute?.getGeometry() ?: Polyline())
            polylineMapObject!!.setStrokeColor(Color.RED)
            polylineMapObject!!.strokeWidth = 5f


        } else {
            Toast.makeText(context, "Error network", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMasstransitRoutesError(p0: Error) {
        TODO("Not yet implemented")
    }
}