package org.northwinds.app.sotachaser.ui.associations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.app.sotachaser.databinding.ListAssociationEntryBinding
import org.northwinds.app.sotachaser.domain.models.Association

class AssociationRecyclerViewAdapter(
    private val values: List<Association>,
) : RecyclerView.Adapter<AssociationRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListAssociationEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.b.code.text = item.code
        holder.b.code.contentDescription = item.code
        holder.b.name.text = item.name
        holder.b.manager.text = item.manager
        holder.b.managerCallsign.text = item.managerCallsign
        holder.b.summitCount.text = item.summitsCount.toString()
        holder.b.regionCount.text = item.regionsCount.toString()
        holder.b.activeFrom.text = item.activeFrom
        holder.b.details.setOnClickListener {
            val a = item.code
            it.findNavController().navigate(AssociationFragmentDirections.actionAssociationFragmentToRegionFragment(association = a))
        }
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: ListAssociationEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val b = binding
    }
}
