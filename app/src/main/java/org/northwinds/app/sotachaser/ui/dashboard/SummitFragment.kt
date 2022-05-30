package org.northwinds.app.sotachaser.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.ui.MapsViewModel
import org.northwinds.app.sotachaser.ui.dashboard.MySummitRecyclerViewAdapter

const val TAG = "SOTAChaser-SummitFragment"

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class SummitFragment : Fragment() {
    private val model: MapsViewModel by viewModels()
    private lateinit var binding: FragmentSummitListBinding

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_summit_list, container, false)
        binding = FragmentSummitListBinding.inflate(inflater)

        binding.association.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(TAG, "Selecting association: $position")
                model.setAssociation(position)
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString("association", model.associations.value!![position])
                    //putString("region", "")
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        binding.region.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(_adapter: AdapterView<*>?, _view: View?, position: Int, _id: Long) {
                Log.d(TAG, "Selecting region: $position")
                model.setRegion(position)
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString("region", model.regions.value!![position])
                }
            }

            override fun onNothingSelected(_adapter: AdapterView<*>?) {
            }
        }
        model.associations.observe(viewLifecycleOwner) {
            binding.association.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            val last = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("association", "")
            if(last != "") {
                val index = model.associations.value?.indexOf(last) ?: -1
                if(index >= 0) {
                    binding.association.setSelection(index)
                }
            }
        }
        model.regions.observe(viewLifecycleOwner) {
            binding.region.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
            val last = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("region", "")
            if(last != "") {
                val index = model.regions.value?.indexOf(last) ?: -1
                if(index >= 0) {
                    binding.region.setSelection(index)
                }
            }
        }

        // Set the adapter
        with(binding.list) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            model.summits.observe(viewLifecycleOwner) { summits ->
                adapter = MySummitRecyclerViewAdapter(summits, model.location.value)
            }
            model.location.observe(viewLifecycleOwner) { location ->
                if(model.summits.value != null) {
                    adapter = MySummitRecyclerViewAdapter(model.summits.value!!, location)
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

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            SummitFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}