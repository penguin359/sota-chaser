package org.northwinds.app.sotachaser.ui.gpxtrack

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentGpxTrackBinding

@AndroidEntryPoint
class GpxTrackFragment : Fragment(), OnMapReadyCallback {
    private val model: GpxTrackViewModel by viewModels()
    private lateinit var binding: FragmentGpxTrackBinding

    private lateinit var mMap: GoogleMap

    val args: GpxTrackFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model.updateTrack(args.association, args.region, args.summit, args.track)

        binding = FragmentGpxTrackBinding.inflate(inflater)
        binding.code.text = " "
        binding.name.text = " "
        binding.trackNotes.text = " "
        binding.trackTitle.text = " "
        binding.callsign.text = " "

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        model.summit.observe(viewLifecycleOwner) {
            it?.let { info ->
                binding.code.text = info.code.toString()
                binding.name.text = info.name
                binding.altitude.text = "${info.altFt} ft"
            }
        }

        model.track.observe(viewLifecycleOwner) {
            it?.let {
                binding.trackTitle.text = it.trackTitle
                binding.trackNotes.text = it.trackNotes
                binding.callsign.text = it.callsign
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

    fun setMapType(value: String) {
        mMap.mapType = when(value) {
            "satellite" -> GoogleMap.MAP_TYPE_SATELLITE
            "hybrid" -> GoogleMap.MAP_TYPE_HYBRID
            "terrain" -> GoogleMap.MAP_TYPE_TERRAIN
            else -> GoogleMap.MAP_TYPE_NORMAL
        }
    }

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener(function = { prefs, key ->
        if(key == "map_type" || key == null)
            setMapType(prefs.getString("map_type", "") ?: "")
    })

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        setMapType(prefs.getString("map_type", "") ?: "")

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
                var minLatitude = pointList[0].latitude
                var maxLatitude = pointList[0].latitude
                var minLongitude = pointList[0].longitude
                var maxLongitude = pointList[0].longitude
                val polyline = PolylineOptions().apply {
                    pointList.forEach {
                        if(it.latitude < minLatitude)
                            minLatitude = it.latitude
                        if(it.latitude > maxLatitude)
                            maxLatitude = it.latitude
                        if(it.longitude < minLongitude)
                            minLongitude = it.longitude
                        if(it.longitude > maxLongitude)
                            maxLongitude = it.longitude
                        add(LatLng(it.latitude, it.longitude))
                    }
                    color(0xff0000ff.toInt())
                }
                val build = mMap.addPolyline(polyline)
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        LatLngBounds(
                            LatLng(minLatitude, minLongitude),
                            LatLng(maxLatitude, maxLongitude)
                        ), 75))
                //build.remove()
                Log.d(TAG, "Updating Map with track points: " + pointList.count())
            }
        }
    }

    companion object {
        private const val TAG = "SOTAChaser-GpxTrackFragment"
    }
}
