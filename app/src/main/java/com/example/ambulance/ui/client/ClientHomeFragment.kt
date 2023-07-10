package com.example.ambulance.ui.client

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentClientHomeBinding
import com.example.ambulance.ui.client.ui.AboutFragment
import com.example.ambulance.ui.client.ui.ComplainFragment
import com.example.ambulance.ui.client.ui.HistoryClientFragment
import com.example.ambulance.ui.client.ui.HomeMapFragment
import com.example.ambulance.ui.client.ui.LocationsFragment
import com.example.ambulance.ui.client.ui.SettingsFragment
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import com.example.ambulance.util.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error


class ClientHomeFragment : Fragment(), UserLocationObjectListener,
    DrivingSession.DrivingRouteListener {
    private var _binding: FragmentClientHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchManager: SearchManager

    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var auth: FirebaseAuth

    init {
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        ).get(AuthViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        //  requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.tv_name)
        val navUserNomer: TextView = headerView.findViewById(R.id.tv_nomer)


        requestLocation()
        val mapKit: MapKit = MapKitFactory.getInstance()
        var probki = mapKit.createTrafficLayer(binding.fcvMain.mapView.mapWindow)
        probki.isTrafficVisible = true


        locationMapKit = mapKit.createUserLocationLayer(binding.fcvMain.mapView.mapWindow)
        locationMapKit.isVisible = true
        binding.fcvMain.mapView.map.move(
            CameraPosition(Point(41.315355, 69.252374), 14f, 0.0f, 0.0f)
        )


        locationMapKit.setObjectListener(this)
        SearchFactory.initialize(context)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = binding.fcvMain.mapView.map.mapObjects.addCollection()

       val pref = SharedPreferencesManager.getEmail(requireContext())
        val email = pref.first
        val pass = pref.second

        auth.signInWithEmailAndPassword(email!!, pass!!)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {

                    if (p0.isSuccessful) {
                        val user = auth.currentUser
                        val userRef =
                            FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {

                                val userName =
                                    dataSnapshot.child("name").getValue(String::class.java)!!
                                Log.d("Tag" + "get UserRepository", userName)
                                val userSurname =
                                    dataSnapshot.child("surname").getValue(String::class.java)!!
                                val userPhoneNumber =
                                    dataSnapshot.child("number").getValue(String::class.java)!!

                                navUsername.text =
                                    getString(R.string.full_name, userName, userSurname)
                                navUserNomer.text = userPhoneNumber
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })

                    }
                }
            })

        // Set registration flag to false
        SharedPreferencesManager.setRegistered(requireContext(), true)

        binding.btnNav.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            // Change the status bar color
            changeStatusBarColor(true)

        }
        // Set drawer listener to handle drawer close event

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Close the drawer
            binding.drawerLayout.closeDrawers()

            // Navigate to the selected item's destination
            val itemId = menuItem.itemId
            when (itemId) {
                R.id.nav_item0 -> {
                    binding.frameFragments.visibility = View.GONE
                    binding.fcvMain.motion.visibility = View.VISIBLE
                    val fragment = HomeMapFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val fragment = HistoryClientFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item1 -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val fragment = LocationsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item2 -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val fragment = ComplainFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item3 -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val fragment = SettingsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item4 -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val fragment = AboutFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.frame_fragments, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item5 -> {
                    binding.frameFragments.visibility = View.VISIBLE
                    binding.fcvMain.motion.visibility = View.GONE
                    val dialogBuilder = AlertDialog.Builder(context)

                    // set message of alert dialog
                    dialogBuilder
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton(
                            "Ok",
                            DialogInterface.OnClickListener { dialog, id ->
                                viewModel.signOut()

                                // Set registration flag to false
                                SharedPreferencesManager.setRegistered(
                                    requireContext(),
                                    false
                                )

                                viewModel.getUserLogged().observe(viewLifecycleOwner) {
                                    if (it) {
                                        findNavController().navigate(R.id.action_clientHomeFragment_to_signInFragment)
                                    }
                                }

                            })
                        // negative button text and action
                        .setNegativeButton(
                            "Cancel",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.cancel()
                            })

                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("Akauntizdan chiqishni xoxlaysizmi?")

                    // show alert dialog
                    alert.show()


                    true
                }

                else -> {
                    false
                }
            }

        }

        binding.fcvMain.cvCurrentLocation.setOnClickListener{
            requestLocation()
        }

        binding.fcvMain.cvBottomSheet.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_card))

    }


    private fun changeStatusBarColor(isDrawerOpen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (isDrawerOpen) {
                // Set the status bar color when the drawer is open
                if (Build.VERSION.SDK_INT >= 21) {
                    requireActivity().window.statusBarColor =
                        ContextCompat.getColor(requireContext(), R.color.white)
                    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

                }
            } else {
                // Set the status bar color when the drawer is closed
                val window: Window = requireActivity().window
                window.addFlags(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST)

            }
        }
    }


    override fun onStop() {
        super.onStop()
        binding.fcvMain.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.fcvMain.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            locationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = Point(it.latitude, it.longitude)
                    moveCameraToLocation(currentLocation)
                } ?: run {
                    Toast.makeText(
                        context,
                        "Unable to retrieve the current location.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun moveCameraToLocation(location: Point) {
        locationMapKit.isVisible = true
        val newPosition = CameraPosition(location, 15.0f, 0.0f, 0.0f)
        binding.fcvMain.mapView.map.move(
            newPosition,
            Animation(Animation.Type.SMOOTH, 1.0f),
            null
        )
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {}

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }


    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
        for (route in p0) {
            mapObjects!!.addPolyline(route.geometry)
        }
    }

    override fun onDrivingRoutesError(p0: Error) {
        val errorMessage = "Unknown error"
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

}