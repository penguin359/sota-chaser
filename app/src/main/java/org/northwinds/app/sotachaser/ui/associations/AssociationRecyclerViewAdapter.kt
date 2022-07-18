package org.northwinds.app.sotachaser.ui.associations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.northwinds.app.sotachaser.databinding.ListAssociationEntryBinding
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.ui.AbstractRecyclerViewAdapter

typealias AssociationRecyclerViewAdapterVH = AbstractRecyclerViewAdapter.ViewHolder<ListAssociationEntryBinding>

class AssociationRecyclerViewAdapter(
    private val values: List<Association>,
) : AbstractRecyclerViewAdapter<Association, ListAssociationEntryBinding>(values) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListAssociationEntryBinding
        = ListAssociationEntryBinding::inflate

    override fun onBindViewBinding(binding: ListAssociationEntryBinding, position: Int) {
        val item = values[position]
        binding.code.text = item.code
        binding.code.contentDescription = item.code
        binding.name.text = item.name
        binding.manager.text = item.manager
        binding.managerCallsign.text = item.managerCallsign
        binding.summitCount.text = item.summitsCount.toString()
        binding.regionCount.text = item.regionsCount.toString()
        binding.activeFrom.text = item.activeFrom
        binding.details.setOnClickListener {
            val a = item.code
            it.findNavController().navigate(AssociationFragmentDirections.actionAssociationFragmentToRegionFragment(association = a))
        }
    }
}
