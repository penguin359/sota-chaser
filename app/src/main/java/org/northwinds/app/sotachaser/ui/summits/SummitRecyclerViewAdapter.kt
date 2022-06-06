package org.northwinds.app.sotachaser.ui.summits

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.northwinds.app.sotachaser.databinding.ListSummitEntryBinding
import org.northwinds.app.sotachaser.domain.models.Summit
import org.northwinds.app.sotachaser.domain.models.SummitDetail
import org.northwinds.app.sotachaser.util.calculateDistance
import org.northwinds.app.sotachaser.ui.abstraction.AbstractRecyclerViewAdapter

typealias SummitRecyclerViewAdapterVH = AbstractRecyclerViewAdapter.ViewHolder<ListSummitEntryBinding>

class SummitRecyclerViewAdapter(
    private val values: List<SummitDetail>
) : AbstractRecyclerViewAdapter<SummitDetail, ListSummitEntryBinding>(values) {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ListSummitEntryBinding
        = ListSummitEntryBinding::inflate

    override fun onBindViewBinding(binding: ListSummitEntryBinding, position: Int) {
        val item = values[position]
        binding.code.text = item.code
        binding.code.contentDescription = item.code
        binding.name.text = item.name
        binding.altitude.text = "${item.altFt} ft"
        binding.points.text = "${item.points} points"
        binding.activationCount.text = "Activations: ${item.activationCount}"
        binding.activationDate.text = item.activationDate
        binding.activationCallsign.text = item.activationCall
        binding.distance.text = item.distance?.run { "${"%.2f".format(this)} miles" } ?: ""
        binding.details.setOnClickListener {
            val a = item.code.split("/")[0]
            val r = item.code.split("/")[1].split("-")[0]
            val s = item.code.split("-")[1]
            it.findNavController().navigate(SummitFragmentDirections.actionNavigationDashboardToSummitDetailsFragment(association = a, region = r, summit = s))
        }
    }
}
