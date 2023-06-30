package com.example.ambulance.ui.client.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.ambulance.R
import com.example.ambulance.databinding.FragmentClientHomeBinding
import com.example.ambulance.databinding.FragmentHomeMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map


class HomeMapFragment : Fragment() {
    private var _binding: FragmentHomeMapBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeMapBinding.inflate(inflater, container, false)
//        MapKitFactory.setApiKey("ab5af028-3c9e-4395-9919-b1dc2f568e15")
//        MapKitFactory.initialize(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
//        binding.mapView.map.move(CameraPosition(Point(41.304113, 69.233312),11.0f,0.0f,0.0f),
//        Animation(Animation.Type.SMOOTH,300f),null
//        )


    }

//    override fun onStop() {
//        super.onStop()
//        binding.mapView.onStop()
//        MapKitFactory.getInstance().onStop()
//    }
//
//    override fun onStart() {
//        binding.mapView.onStart()
//        MapKitFactory.getInstance().onStart()
//        super.onStart()
//    }
}