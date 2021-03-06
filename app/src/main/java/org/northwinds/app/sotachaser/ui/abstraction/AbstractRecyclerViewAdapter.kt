package org.northwinds.app.sotachaser.ui.abstraction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class AbstractRecyclerViewAdapter<E, VB: ViewBinding>(
    private val values: List<E>,
) : RecyclerView.Adapter<AbstractRecyclerViewAdapter.ViewHolder<VB>>() {
    protected lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        return ViewHolder(
            bindingInflater.invoke(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    abstract fun onBindViewBinding(binding: VB, position: Int)

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        onBindViewBinding(holder.binding, position)
    }

    override fun getItemCount(): Int = values.size

    class ViewHolder<VB: ViewBinding>(binding: VB) : RecyclerView.ViewHolder(binding.root) {
        val binding = binding
    }
}
