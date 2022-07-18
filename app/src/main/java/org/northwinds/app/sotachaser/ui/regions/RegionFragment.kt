package org.northwinds.app.sotachaser.ui.regions

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
import android.widget.SimpleAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.ui.map.MapsViewModel
import org.northwinds.app.sotachaser.ui.summits.SummitFragmentArgs


@AndroidEntryPoint
class RegionFragment : Fragment() {
    private val model: MapsViewModel by viewModels()
    private lateinit var binding: FragmentSummitListBinding

    val args: RegionFragmentArgs by navArgs()

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
        with(binding.list) {
            model.regions.observe(viewLifecycleOwner) { regions ->
                Log.d(TAG, "Loading data from ${model.association.value} with ${regions.count()} regions")
                adapter = RegionRecyclerViewAdapter(model.association.value!!, regions)
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "SOTAChaser-RegionFragment"
    }
}