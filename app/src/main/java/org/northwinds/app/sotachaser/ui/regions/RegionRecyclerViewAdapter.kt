package org.northwinds.app.sotachaser.ui.regions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.app.sotachaser.databinding.ListRegionEntryBinding
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.ui.associations.AssociationFragmentDirections

class RegionRecyclerViewAdapter(
    private val association: String,
    private val values: List<Region>,
) : RecyclerView.Adapter<RegionRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListRegionEntryBinding.inflate(
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
        holder.b.details.setOnClickListener {
            it.findNavController().navigate(RegionFragmentDirections.actionRegionFragmentToNavigationDashboard(association = association, region = item.code))
        }
        //holder.topView.setOnClickListener {
        //    val a = item.code.split("/")[0]
        //    val r = item.code.split("/")[1].split("-")[0]
        //    val s = item.code.split("-")[1]
        //    it.findNavController().navigate(SummitFragmentDirections.actionNavigationDashboardToSummitDetailsFragment(association = a, region = r, summit = s))
        //}
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder(binding: ListRegionEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val b = binding
    }
}
