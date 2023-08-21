package com.example.ambulance.ui.client

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.ambulance.MainActivity
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.LinkedList


class ClientHomeFragment : Fragment() {
    private var _binding: FragmentClientHomeBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel

    private var auth: FirebaseAuth

    private val navigationHistory: MutableList<Int> = mutableListOf() // Initialize this with proper values
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.white)

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        val fragment = HomeMapFragment()

        val ft: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        ft.replace(R.id.secondNavHostFragment, fragment)
            .addToBackStack(null) // Add the fragment to the back stack
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        ).get(AuthViewModel::class.java)

        val navigationView: NavigationView = view.findViewById(R.id.navigation_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.tv_name)
        val navUserNomer: TextView = headerView.findViewById(R.id.tv_nomer)


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
                    binding.tvMainTitle.text = "Home"
                  //  if (::navController.isInitialized) {
                        //findNavController().navigate(R.id.homeMapFragment)
                   // }
                    val fragment2 = HomeMapFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment2)
                        .addToBackStack(null) // Add the fragment to the back stack
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item -> {
                    binding.tvMainTitle.text = "History"
                   // if (::navController.isInitialized) {
                      //  findNavController().navigate(R.id.historyClientFragment)
                   // }
                    val fragment3 = HistoryClientFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment3)
                        .addToBackStack(null) // Add the fragment to the back stack
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item1 -> {
                    binding.tvMainTitle.text = "My Locations"
                    val fragment4 = LocationsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment4)
                        .addToBackStack(null) // Add the fragment to the back stack
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item2 -> {
                    binding.tvMainTitle.text = "Complaints and Comments"
                    val fragment5 = ComplainFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment5)
                        .addToBackStack(null) // Add the fragment to the back stack
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()

                    true
                }

                R.id.nav_item3 -> {
                    binding.tvMainTitle.text = "Settings"

                    val fragment6 = SettingsFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment6)
                        .addToBackStack(null) // Add the fragment to the back stack
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
                    true
                }

                R.id.nav_item4 -> {
                    binding.tvMainTitle.text = "About"
                     //   findNavController().navigate(R.id.action_clientHomeFragment_to_aboutFragment2)
                    val fragment5 = AboutFragment()

                    val ft: FragmentTransaction =
                        requireActivity().supportFragmentManager.beginTransaction()
                    ft.replace(R.id.secondNavHostFragment, fragment5)
                        .addToBackStack(null) // Add the fragment to the back stack
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
                    alert.setTitle("Leave of Your Account?")

                    // show alert dialog
                    alert.show()


                    true
                }

                else -> {
                    false
                }
            }
            // Add the destination to the navigation history
            navigationHistory.add(itemId)
        }

        // Intercept the back button press event
//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                handleCustomBackPressed()
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    override fun onResume() {
        super.onResume()

        // Clear navigation history when fragment is resumed
        navigationHistory.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Clear navigation history when fragment view is destroyed
        navigationHistory.clear()
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
}