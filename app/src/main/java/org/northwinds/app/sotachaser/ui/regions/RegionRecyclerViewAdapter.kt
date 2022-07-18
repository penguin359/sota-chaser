package org.northwinds.app.sotachaser.ui.regions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.app.sotachaser.databinding.ListRegionEntryBinding
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.ui.associations.AssociationFragmentDirections
import org.northwinds.app.sotachaser.ui.abstraction.AbstractRecyclerViewAdapter

typealias RegionRecyclerViewAdapterVH = AbstractRecyclerViewAdapter.ViewHolder<ListRegionEntryBinding>

class RegionRecyclerViewAdapter(
    private val association: String,
    private val values: List<Region>,
) : AbstractRecyclerViewAdapter<Region, ListRegionEntryBinding>(values) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListRegionEntryBinding
        = ListRegionEntryBinding::inflate

    override fun onBindViewBinding(binding: ListRegionEntryBinding, position: Int) {
        val item = values[position]
        binding.code.text = item.code
        binding.code.contentDescription = item.code
        binding.name.text = item.name
        binding.manager.text = item.manager
        binding.managerCallsign.text = item.managerCallsign
        binding.summitCount.text = item.summitsCount.toString()
        binding.details.setOnClickListener {
            it.findNavController().navigate(RegionFragmentDirections.actionRegionFragmentToNavigationDashboard(association = association, region = item.code))
        }
    }
}
