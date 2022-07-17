package org.northwinds.app.sotachaser.ui.summits

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.ui.map.MapsViewModel


@AndroidEntryPoint
class SummitFragment : Fragment() {
    private val model: MapsViewModel by viewModels()
    private lateinit var binding: FragmentSummitListBinding

    val args: SummitFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummitListBinding.inflate(inflater)

        model.associations.observe(viewLifecycleOwner) { associations ->
            if (associations == null)
                return@observe
            val index = associations.indexOfFirst { it.code == args.association }
            if (index >= 0)
                model.setAssociation(index)
        }
        model.regions.observe(viewLifecycleOwner) { regions ->
            if (regions == null)
                return@observe
            val index = regions.indexOfFirst { it.code == args.region }
            if (index >= 0)
                model.setRegion(index)
        }

        // Set the adapter
        with(binding.list) {
            model.summits.observe(viewLifecycleOwner) { summits ->
                adapter = SummitRecyclerViewAdapter(summits, model.location.value)
            }
            model.location.observe(viewLifecycleOwner) { location ->
                if(model.summits.value != null) {
                    adapter = SummitRecyclerViewAdapter(model.summits.value!!, location)
                }
            }
        }

        if(GoogleApiAvailability().isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    val locationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                    locationClient.lastLocation.addOnSuccessListener { location ->
                        model.setLocation(location)
                    }
                }
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                    // ...
                }
                else -> {
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                        if (isGranted) {
                            val locationClient =
                                LocationServices.getFusedLocationProviderClient(requireActivity())
                            locationClient.lastLocation.addOnSuccessListener { location ->
                                model.setLocation(location)
                            }
                        }
                    }.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "SOTAChaser-SummitFragment"
    }
}
