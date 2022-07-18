package org.northwinds.app.sotachaser.ui.associations

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
import androidx.preference.PreferenceManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.databinding.ListAssociationEntryBinding
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.ui.AbstractFilterListFragment
import org.northwinds.app.sotachaser.ui.map.MapsViewModel


@AndroidEntryPoint
class AssociationFragment : AbstractFilterListFragment<Association, FragmentSummitListBinding, AssociationRecyclerViewAdapter>() {
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list
    override val data get() = model.associations

    override fun adapterFactory(values: List<Association>) = AssociationRecyclerViewAdapter(values)

    override fun refresh() {
        model.refreshAssociations()
    }

    //companion object {
    //    const val TAG = "SOTAChaser-AssociationFragment"
    //}
}
