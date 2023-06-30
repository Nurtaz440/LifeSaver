package com.example.ambulance.ui.client

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ambulance.R
import com.example.ambulance.adapter.MyListAdapter
import com.example.ambulance.database.AppDatabase
import com.example.ambulance.databinding.FragmentClientHomeBinding
import com.example.ambulance.model.LocationModel
import com.example.ambulance.repository.UserRepository
import com.example.ambulance.ui.client.ui.AboutFragment
import com.example.ambulance.ui.client.ui.ComplainFragment
import com.example.ambulance.ui.client.ui.HistoryClientFragment
import com.example.ambulance.ui.client.ui.HomeMapFragment
import com.example.ambulance.ui.client.ui.LocationsFragment
import com.example.ambulance.ui.client.ui.SettingsFragment
import com.example.ambulance.ui.registration.viewModel.AuthViewModel
import com.example.ambulance.util.SharedPreferencesManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class ClientHomeFragment : Fragment(), UserLocationObjectListener, Session.SearchListener,
    CameraListener,
    DrivingSession.DrivingRouteListener {
    private var _binding: FragmentClientHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var profileViewModel: ProfileViewModel
    lateinit var locationMapKit: UserLocationLayer
    lateinit var searchManager: SearchManager
    lateinit var searchSession: Session
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var ROUTE_START_LOCATION = Point(41.310529, 69.256265)
    private var ROUTE_END_LOCATION = Point(40.492518, 68.701816)
    private var SCREEN_CENTER = Point(
        (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
        (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
    )
    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    var list = mutableListOf<LocationModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query, VisibleRegionUtils.toPolygon(binding.fcvMain.mapView.map.visibleRegion),
            SearchOptions(), this
        )
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
        backPressed()
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)
        //  requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.tv_name)
        val navUserNomer: TextView = headerView.findViewById(R.id.tv_nomer)

//        binding.fcvMain.mapView.map.move(
//            CameraPosition(Point(41.304113, 69.233312), 13.0f, 0.0f, 0.0f),
//            Animation(Animation.Type.SMOOTH, 0f), null
//        )
        requestLocation()
        val mapKit: MapKit = MapKitFactory.getInstance()
        var probki = mapKit.createTrafficLayer(binding.fcvMain.mapView.mapWindow)
        probki.isTrafficVisible = false

        binding.fcvMain.probikbtn.setOnClickListener {
            if (probki.isTrafficVisible == false) {
                probki.isTrafficVisible = true
                binding.fcvMain.probikbtn.setCardBackgroundColor(Color.BLUE)
            } else {
                probki.isTrafficVisible = false
                binding.fcvMain.probikbtn.setCardBackgroundColor(Color.YELLOW)
            }
        }
        locationMapKit = mapKit.createUserLocationLayer(binding.fcvMain.mapView.mapWindow)
        locationMapKit.isVisible = true
        binding.fcvMain.mapView.map.move(
            CameraPosition( binding.fcvMain.mapView.map.cameraPosition.target, 13f, 0.0f, 0.0f))


        locationMapKit.setObjectListener(this)
        SearchFactory.initialize(context)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        binding.fcvMain.mapView.map.addCameraListener(this)
        binding.fcvMain.etSearch.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitQuery(binding.fcvMain.etSearch.text.toString())
                }
                return false
            }
        })
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = binding.fcvMain.mapView.map.mapObjects.addCollection()

        submitRequest()


        val userDao = AppDatabase.getInstance(requireContext()).userDao()
        val userRepository = UserRepository(userDao)
        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModel.ProfileViewModelFactory(userRepository)
        ).get(ProfileViewModel::class.java)

        // Observe the allUsers LiveData
        profileViewModel.allUsers.observe(viewLifecycleOwner) {
            // Update UI with user data
            navUsername.text = getString(R.string.full_name, it.name!!, it.surName!!)
            navUserNomer.text = it.number!!
        }
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
                    //  navController.navigate(R.id.action_homeMapFragment_to_historyClientFragment)
                    val fragment = HomeMapFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item -> {
                    val fragment = HistoryClientFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item1 -> {
                    val fragment = LocationsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item2 -> {
                    val fragment = ComplainFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item3 -> {
                    val fragment = SettingsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item4 -> {
                    val fragment = AboutFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.fcvMain, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item5 -> {
                    val dialogBuilder = AlertDialog.Builder(context)

                    // set message of alert dialog
                    dialogBuilder
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                            viewModel.signOut()

                            // Set registration flag to false
                            SharedPreferencesManager.setRegistered(requireContext(), false)

                            viewModel.getUserLogged().observe(viewLifecycleOwner) {
                                if (it) {
                                    findNavController().navigate(R.id.action_clientHomeFragment_to_signInFragment)
                                }
                            }

                        })
                        // negative button text and action
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
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

        binding.fcvMain.btcancel.setOnClickListener { mapObjects!!.clear() }
        var opened = false
        binding.fcvMain.btlv.setOnClickListener {
            if (opened){
                binding.fcvMain.listView.visibility = View.VISIBLE
                opened = false
            }else{
                binding.fcvMain.listView.visibility = View.GONE
                opened = true
            }
        }

        reaacord()
        binding.fcvMain.cvBottomSheet.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_card))

    }

    @SuppressLint("MissingPermission")
    private fun reaacord() {
        val db = Firebase.firestore
        db.collection("cords")
            .get()
            .addOnSuccessListener { result->
                for (document in result){
                    var name = document.get("name").toString()
                    var lat = document.get("lat").toString().toDouble()
                    var lon = document.get("long").toString().toDouble()
                    mapObjects!!.addPlacemark(Point(lat,lon))
                    list.add(LocationModel(name, lat.toString(), lon.toString()))
                  var  adapter = MyListAdapter(requireContext(),R.layout.row,list)
                    binding.fcvMain.listView.adapter = adapter
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location : android.location.Location->
                        ROUTE_START_LOCATION = Point(location.latitude.toString().toDouble(),location.longitude.toString().toDouble())
                       true
                    }
                    binding.fcvMain.listView.setOnItemClickListener { parent, view, position, id ->
                        when(position){
                            0-> {
                                ROUTE_END_LOCATION = Point(list[0].lat.toString().toDouble(),list[0].long.toString().toDouble())
                                SCREEN_CENTER =  Point(
                                    (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
                                    (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
                                )
                                submitRequest()
                            }
                            1-> {
                                ROUTE_END_LOCATION = Point(list[1].lat.toString().toDouble(),list[1].long.toString().toDouble())
                                SCREEN_CENTER =  Point(
                                    (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
                                    (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
                                )
                                submitRequest()
                            }
                            2-> {
                                ROUTE_END_LOCATION = Point(list[2].lat.toString().toDouble(),list[2].long.toString().toDouble())
                                SCREEN_CENTER =  Point(
                                    (ROUTE_START_LOCATION.latitude + ROUTE_END_LOCATION.latitude) / 2,
                                    (ROUTE_START_LOCATION.longitude + ROUTE_END_LOCATION.longitude) / 2
                                )
                                submitRequest()
                            }


                        }
                    }

                }
            }
            .addOnFailureListener {

            }
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

    private fun backPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // in here you can do logic when backPress is clicked
                    isEnabled = false
                    activity?.onBackPressed()
                }
            })
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
                Manifest.permission.ACCESS_FINE_LOCATION
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
        }

    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        locationMapKit.setAnchor(
            PointF(
                (binding.fcvMain.mapView.width() * 0.5f).toFloat(),
                (binding.fcvMain.mapView.height() * 0.5f).toFloat()
            ),
            PointF(
                (binding.fcvMain.mapView.width() * 0.5f).toFloat(),
                (binding.fcvMain.mapView.height() * 0.83f).toFloat()
            )
        )
//        userLocationView.arrow.setIcon(
//            ImageProvider.fromResource(
//                context,
//                R.drawable.icon_navigator
//            )
//        )
//        val picIcon = userLocationView.pin.useCompositeIcon()
//        picIcon.setIcon(
//            "icon", ImageProvider.fromResource(context, R.drawable.baseline_location_on_24),
//            IconStyle().setRotationType(RotationType.ROTATE).setZIndex(0f).setScale(1f)
//        )
//        picIcon.setIcon(
//            "pin",
//            ImageProvider.fromResource(context, R.drawable.baseline_location_on_24),
//            IconStyle()
//                .setAnchor(PointF(0.5f, 0.5f)).setRotationType(RotationType.ROTATE).setZIndex(1f)
//                .setScale(0.5f)
//        )
//        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001

    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }

    override fun onSearchResponse(response: Response) {
        val mapObjects: MapObjectCollection = binding.fcvMain.mapView.map.mapObjects
        //  mapObjects.clear()
        for (searchResult in response.collection.children) {
            val resultLocation = searchResult.obj!!.geometry[0].point!!
            if (response != null) {
                mapObjects.addPlacemark(
                    resultLocation, ImageProvider.fromResource(
                        context, R.drawable.baseline_location_on_24
                    )
                )
            }
        }
    }

    override fun onSearchError(error: Error) {
        //   var errorMessage = "Unknown error"
//        if (error is RemoteError){
//            errorMessage = "remote error"
//        }else if (error is NetworkError){
//            errorMessage = "Problem with internet"
//        }
        //  Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            submitQuery(binding.fcvMain.etSearch.text.toString())
        }
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

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints:ArrayList<RequestPoint> = ArrayList()
        requestPoints.add(RequestPoint(ROUTE_START_LOCATION,RequestPointType.WAYPOINT,null))
        requestPoints.add(RequestPoint(ROUTE_END_LOCATION,RequestPointType.WAYPOINT,null))
        drivingSession = drivingRouter!!.requestRoutes(requestPoints,drivingOptions,vehicleOptions,this)
    }
}