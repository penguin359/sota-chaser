package org.northwinds.app.sotachaser.ui.abstraction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class AbstractFilterListFragment<E, VB: ViewBinding, R: AbstractRecyclerViewAdapter<E, *>, VM: AbstractViewModel<E>> : Fragment() {
    abstract val TAG: String
    abstract val vmc: Class<VM>
    protected lateinit var model: VM

    protected lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract val listView: RecyclerView
    abstract val filterView: EditText

    abstract fun adapterFactory(value: List<E>): R

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            model = ViewModelProvider(requireActivity())[vmc]
        binding = bindingInflater.invoke(inflater)

        model.refresh()

        with(listView) {
            model.list_items.observe(viewLifecycleOwner) {
                Log.d(TAG, "Loading data with ${it.count()} items")
                adapter = adapterFactory(it)
            }
        }
        with(filterView) {
            doOnTextChanged { text, _, _, _ ->
                model.setFilter(text.toString())
            }
        }

        return binding.root
    }
}
