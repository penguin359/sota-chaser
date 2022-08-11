package org.northwinds.app.sotachaser.ui.summits

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.ui.abstraction.AbstractFilterListFragment


@AndroidEntryPoint
class SummitFragment : AbstractFilterListFragment<Summit, FragmentSummitListBinding, SummitRecyclerViewAdapter, SummitViewModel>() {
    override val TAG = "SOTAChaser-SummitFragment"
    override val vmc = SummitViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list
    override val filterView get() = binding.filter

    val args: SummitFragmentArgs by navArgs()

    override fun adapterFactory(value: List<Summit>) = SummitRecyclerViewAdapter(value, model.location.value)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        model.setRegion(args.association, args.region)
        model.refresh()

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
                    class A : DialogFragment() {
                        private lateinit var permissionRequest: ActivityResultLauncher<Array<String>>

                        override fun onCreate(savedInstanceState: Bundle?) {
                            super.onCreate(savedInstanceState)
                            permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                                if (result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ||
                                    result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                                    val locationClient =
                                        LocationServices.getFusedLocationProviderClient(requireActivity())
                                    locationClient.lastLocation.addOnSuccessListener { location ->
                                        model.setLocation(location)
                                    }
                                }
                            }
                        }

                        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                            return AlertDialog.Builder(requireContext())
                                .setTitle("Location Permission")
                                .setMessage("This app uses location to find your distance from various summits" +
                                        "and to show your position on a map with summit location.")
                                .setPositiveButton(android.R.string.ok) { dialog, which ->
                                    permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                                }
                                .setNegativeButton(android.R.string.cancel) { dialog, which ->
                                    // TODO save preference
                                }
                                .create()
                        }
                    }
                    A().show(childFragmentManager, null)
                }
                else -> {
                    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                        if (result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) ||
                            result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)) {
                            val locationClient =
                                LocationServices.getFusedLocationProviderClient(requireActivity())
                            locationClient.lastLocation.addOnSuccessListener { location ->
                                model.setLocation(location)
                            }
                        }
                    }.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                }
            }
        }

        return root
    }
}
