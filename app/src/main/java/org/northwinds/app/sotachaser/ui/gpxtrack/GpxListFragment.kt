package org.northwinds.app.sotachaser.ui.gpxtrack

import android.app.Dialog
import android.content.DialogInterface
import android.database.DataSetObserver
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import org.northwinds.app.sotachaser.domain.models.GpxTrack
import org.northwinds.app.sotachaser.ui.summitdetails.SummitDetailsFragmentDirections

class GpxListFragment() : DialogFragment() {
    var items: Array<GpxTrack> = arrayOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        items = arguments?.getSerializable("value") as? Array<GpxTrack> ?: arrayOf<GpxTrack>()
        return AlertDialog.Builder(requireContext())
            .setTitle("Hello")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(requireContext(),
                    android.R.string.yes, Toast.LENGTH_SHORT).show()
            }
            .setAdapter(GpxList(items)) { dialog2, position ->
                Toast.makeText(requireContext(), "Clicked on ${items[position]}", Toast.LENGTH_LONG).show()
                setFragmentResult("a", Bundle().apply {
                    putLong("id", items[position].id)
                })
            }
            .create()
    }
}

class GpxList(private val tracks: Array<GpxTrack>) : BaseAdapter() {
    override fun getCount(): Int {
        return tracks.size
    }

    override fun getItem(position: Int): Any {
        return tracks[position]
    }

    override fun getItemId(position: Int): Long {
        return tracks[position].id
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return (convertView as? TextView ?: TextView(parent?.context)).apply {
            text = tracks[position].trackTitle
        }
    }
}
