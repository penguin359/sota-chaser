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
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.ui.abstraction.AbstractFilterListFragment
import org.northwinds.app.sotachaser.ui.map.MapsViewModel
import org.northwinds.app.sotachaser.ui.summits.SummitFragmentArgs


@AndroidEntryPoint
class RegionFragment : AbstractFilterListFragment<Region, FragmentSummitListBinding, RegionRecyclerViewAdapter, RegionViewModel>() {
    override val TAG = "SOTAChaser-RegionFragment"
    override val vmc = RegionViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list

    val args: RegionFragmentArgs by navArgs()

    override fun adapterFactory(values: List<Region>) = RegionRecyclerViewAdapter(args.association, values)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        model.setAssociation(args.association)

        return root
    }

    //companion object {
    //    const val TAG = "SOTAChaser-RegionFragment"
    //}
}
