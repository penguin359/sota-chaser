package org.northwinds.app.sotachaser.ui.summitdetails

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.DataSetObserver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitDetailsBinding
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.ui.gpxtrack.GpxListFragment
import org.northwinds.app.sotachaser.ui.map.MapsViewModel

const val TAG = "SOTAChaser-SummitDetailsFragment"

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class SummitDetailsFragment : Fragment(), OnMapReadyCallback {
    private val model: SummitDetailsViewModel by viewModels()
    private lateinit var binding: FragmentSummitDetailsBinding

    private var association: String? = null
    private var region: String? = null
    private var summit: String? = null

    private lateinit var mMap: GoogleMap

    val args: SummitDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        association = args.association
        region = args.region
        summit = args.summit
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummitDetailsBinding.inflate(inflater)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        model.updateSummit(args.association, args.region, args.summit)
        model.summit.observe(viewLifecycleOwner) { summitInfo ->
            summitInfo?.let { info ->
                binding.code.text = info.code.toString()
                binding.name.text = info.name
                binding.altitude.text = "${info.altFt} ft"
                binding.points.text = "${info.points} points"
                binding.activationCount.text = "Activations: ${info.activationCount}"
                binding.activationDate.text = info.activationDate
                binding.activationCallsign.text = info.activationCall
                //binding.distance.text = location?.run { "${"%.2f".format(calculateDistance(latitude, longitude, item.latitude, item.longitude))} miles" } ?: ""
                binding.sotaBtn.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://summits.sota.org.uk/summit/${summitInfo.code}")))
                }
                binding.sotlasBtn.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sotl.as/summits/${summitInfo.code}")))
                }
                binding.geoBtn.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${summitInfo.latitude},${summitInfo.longitude}")))
                }
            }

            binding.trackList.setOnClickListener {
                GpxListFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("value", model.tracks.value?.toTypedArray())
                    }
                    show(this@SummitDetailsFragment.childFragmentManager, null)
                    setFragmentResultListener("a", ) { _, response ->
                        findNavController().navigate(SummitDetailsFragmentDirections.actionSummitDetailsFragmentToGpxTrackFragment(args.association, args.region, args.summit, response.getLong("id")))
                    }
                }
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
        model.summit.observe(this) { summitInfo ->
            if(summitInfo == null)
                return@observe
            val location = LatLng(summitInfo.latitude, summitInfo.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(location).title(summitInfo.code))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            //Log.d(MapsFragment.Tag, "Updating Map with summit count: " + summits.count())
        }
    }
}
