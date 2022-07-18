package org.northwinds.app.sotachaser.ui

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
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.ui.map.MapsViewModel


abstract class AbstractFilterListFragment<E, VBL: ViewBinding, VB: ViewBinding, R: AbstractRecyclerViewAdapter<E, VBL>> : Fragment() {
    protected val model: MapsViewModel by viewModels()
    protected lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract val listView: RecyclerView
    abstract val data: LiveData<List<E>>

    abstract fun adapterFactory(value: List<E>): R

    abstract fun refresh()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater.invoke(inflater)

        refresh()

        with(listView) {
            data.observe(viewLifecycleOwner) {
                adapter = adapterFactory(it)
            }
        }

        return binding.root
    }

    //companion object {
    //    const val TAG = "SOTAChaser-AssociationFragment"
    //}
}
