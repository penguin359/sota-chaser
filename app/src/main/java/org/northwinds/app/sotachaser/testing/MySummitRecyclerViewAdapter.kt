package org.northwinds.app.sotachaser.testing

import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitBinding
import org.northwinds.app.sotachaser.room.Summit

import org.northwinds.app.sotachaser.testing.placeholder.PlaceholderContent.PlaceholderItem
import org.northwinds.app.sotachaser.util.calculateDistance

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MySummitRecyclerViewAdapter(
    private val values: List<Summit>,
    private val location: Location?
) : RecyclerView.Adapter<MySummitRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentSummitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.code
        holder.contentView.text = item.name
        holder.altView.text = "${item.altFt} ft"
        holder.pointsView.text = "${item.points} points"
        holder.activationCountView.text = "Activations: ${item.activationCount}"
        holder.activationDateView.text = item.activationDate
        holder.activationCallsignView.text = item.activationCall
        holder.distanceView.text = location?.run { "${"%.2f".format(calculateDistance(latitude, longitude, item.latitude, item.longitude))} miles" } ?: ""
        holder.topView.setOnClickListener {
            val a = item.code.split("/")[0]
            val r = item.code.split("/")[1].split("-")[0]
            val s = item.code.split("-")[1]
            it.findNavController().navigate(R.id.action_navigation_dashboard_to_summitDetailsFragment, bundleOf(
                "association" to a,
                "region" to r,
                "summit" to s,
            ))
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSummitBinding) : RecyclerView.ViewHolder(binding.root) {
        val topView = binding.summitDetails
        val idView = binding.summitId
        val contentView = binding.name
        val altView = binding.altitude
        val pointsView = binding.points
        val activationCountView = binding.activationCount
        val activationDateView = binding.activationDate
        val activationCallsignView = binding.activationCallsign
        val distanceView = binding.distance

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}
