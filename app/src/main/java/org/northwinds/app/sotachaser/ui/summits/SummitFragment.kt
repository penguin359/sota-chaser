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
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.ui.abstraction.AbstractFilterListFragment


@AndroidEntryPoint
class SummitFragment : AbstractFilterListFragment<Summit, FragmentSummitListBinding, SummitRecyclerViewAdapter, SummitViewModel>() {
    override val TAG = "SOTAChaser-SummitFragment"
    override val vmc = SummitViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list

    val args: SummitFragmentArgs by navArgs()

    override fun adapterFactory(values: List<Summit>) = SummitRecyclerViewAdapter(values, model.location.value)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        model.setRegion(args.association, args.region)

        with(binding.list) {
            model.location.observe(viewLifecycleOwner) { location ->
                if(model.list_items.value != null) {
                    adapter = SummitRecyclerViewAdapter(model.list_items.value!!, location)
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

        return root
    }
}
