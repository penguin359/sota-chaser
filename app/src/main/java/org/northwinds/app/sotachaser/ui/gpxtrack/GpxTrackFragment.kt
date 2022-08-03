package org.northwinds.app.sotachaser.ui.gpxtrack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitDetailsBinding
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.ui.map.MapsViewModel

@AndroidEntryPoint
class GpxTrackFragment : Fragment(), OnMapReadyCallback {
    private val model: GpxTrackViewModel by viewModels()
    private lateinit var binding: FragmentSummitDetailsBinding

    private lateinit var mMap: GoogleMap

    val args: GpxTrackFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model.updateTrack(args.association, args.region, args.summit, args.track)

        binding = FragmentSummitDetailsBinding.inflate(inflater)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        model.summit.observe(viewLifecycleOwner) {
            it?.let { info ->
                binding.code.text = info.code.toString()
                binding.name.text = info.name
                binding.altitude.text = "${info.altFt} ft"
                binding.points.text = "${info.points} points"
                binding.activationCount.text = "Activations: ${info.activationCount}"
                binding.activationDate.text = info.activationDate
                binding.activationCallsign.text = info.activationCall
            }
        }

        //if(GoogleApiAvailability().isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS) {
        //    when {
        //        ContextCompat.checkSelfPermission(
        //            requireContext(),
        //            Manifest.permission.ACCESS_COARSE_LOCATION
        //        ) == PackageManager.PERMISSION_GRANTED
        //        -> {
        //            val locationClient =
        //                LocationServices.getFusedLocationProviderClient(requireActivity())
        //            locationClient.lastLocation.addOnSuccessListener { location ->
        //                model.setLocation(location)
        //            }
        //        }
        //        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) -> {
        //            // ...
        //        }
        //        else -> {
        //            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        //                if (isGranted) {
        //                    val locationClient =
        //                        LocationServices.getFusedLocationProviderClient(requireActivity())
        //                    locationClient.lastLocation.addOnSuccessListener { location ->
        //                        model.setLocation(location)
        //                    }
        //                }
        //            }.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        //        }
        //    }
        //}

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Log.d(MapsFragment.Tag, "Google Maps ready")
        model.summit.observe(this) { summits ->
            if(summits == null)
                return@observe
            val location = LatLng(summits.latitude, summits.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(location).title(summits.code))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            Log.d(TAG, "Updating Map with summit code: " + summits.code)
        }
        model.points.observe(this) { pointList ->
            if(pointList.isNotEmpty()) {
                val polyline = PolylineOptions().apply {
                    pointList.forEach {
                        add(LatLng(it.latitude, it.longitude))
                    }
                }
                val build = mMap.addPolyline(polyline)
                //build.remove()
                Log.d(TAG, "Updating Map with track points: " + pointList.count())
            }
        }
    }

    companion object {
        private const val TAG = "SOTAChaser-GpxTrackFragment"
    }
}
