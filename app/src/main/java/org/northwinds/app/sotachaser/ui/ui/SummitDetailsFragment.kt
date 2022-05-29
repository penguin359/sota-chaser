package org.northwinds.app.sotachaser.ui.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.MapsFragment
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentMapsBinding
import org.northwinds.app.sotachaser.databinding.FragmentSummitBinding
import org.northwinds.app.sotachaser.databinding.FragmentSummitDetailsBinding
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.testing.placeholder.PlaceholderContent
import org.northwinds.app.sotachaser.ui.MapsViewModel
import org.northwinds.app.sotachaser.util.calculateDistance

const val TAG = "SOTAChaser-SummitDetailsFragment"

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class SummitDetailsFragment : Fragment() {
    private val model: MapsViewModel by viewModels()
    private lateinit var binding: FragmentSummitDetailsBinding

    private var association: String? = null
    private var region: String? = null
    private var summit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            association = arguments?.getString("association")!!
            region = arguments?.getString("region")!!
            summit = arguments?.getString("summit")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummitDetailsBinding.inflate(inflater)

        model.associations.observe(viewLifecycleOwner) {
            if(association == null)
                return@observe
            val index = it.indexOf(association)
            if(index >= 0)
                model.setAssociation(index)
        }
        model.regions.observe(viewLifecycleOwner) {
            if(region == null)
                return@observe
            val index = it.indexOf(region)
            if(index >= 0)
                model.setRegion(index)
        }
        model.summits.observe(viewLifecycleOwner) {
            if(summit == null)
                return@observe
            val summitInfo = it.find { summit2 -> summit2.code.split("-")[1] == summit }
            summitInfo?.let { info ->
                binding.summitId.text = info.code.toString()
                binding.name.text = info.name
                binding.altitude.text = "${info.altFt} ft"
                binding.points.text = "${info.points} points"
                binding.activationCount.text = "Activations: ${info.activationCount}"
                binding.activationDate.text = info.activationDate
                binding.activationCallsign.text = info.activationCall
                //binding.distance.text = location?.run { "${"%.2f".format(calculateDistance(latitude, longitude, item.latitude, item.longitude))} miles" } ?: ""
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
}