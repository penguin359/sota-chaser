package org.northwinds.app.sotachaser.ui.abstraction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import org.northwinds.app.sotachaser.BuildConfig
import org.northwinds.app.sotachaser.ui.SettingsActivity


abstract class AbstractFilterListFragment<E, VB: ViewBinding, R: AbstractRecyclerViewAdapter<E, *>, VM: AbstractViewModel<E>> : Fragment() {
    abstract val TAG: String
    abstract val vmc: Class<VM>
    protected lateinit var model: VM

    protected lateinit var binding: VB
    abstract val bindingInflater: (LayoutInflater) -> VB
    abstract val listView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        requireActivity().addMenuProvider(menu, viewLifecycleOwner)

        return binding.root
    }

    val menu = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(org.northwinds.app.sotachaser.R.menu.list_menu, menu)
        }

        override fun onMenuItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                org.northwinds.app.sotachaser.R.id.enable_favorites -> {
                    true
                }
                org.northwinds.app.sotachaser.R.id.sort -> {
                    true
                }
                else -> false
            }
        }
    }
}
