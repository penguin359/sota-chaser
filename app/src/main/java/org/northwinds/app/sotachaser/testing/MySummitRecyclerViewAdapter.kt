package org.northwinds.app.sotachaser.testing

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.northwinds.app.sotachaser.R
import org.northwinds.app.sotachaser.databinding.FragmentSummitBinding
import org.northwinds.app.sotachaser.room.Summit

import org.northwinds.app.sotachaser.testing.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MySummitRecyclerViewAdapter(
    private val values: List<Summit>
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
        holder.altView.text = "${item.altFt}"
        holder.pointsView.text = "${item.points}"
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentSummitBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val altView = binding.altitude
        val pointsView = binding.points

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}
