package org.northwinds.app.sotachaser.ui.regions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.domain.models.Region
import org.northwinds.app.sotachaser.ui.abstraction.AbstractFilterListFragment


@AndroidEntryPoint
class RegionFragment : AbstractFilterListFragment<Region, FragmentSummitListBinding, RegionRecyclerViewAdapter, RegionViewModel>() {
    override val TAG = "SOTAChaser-RegionFragment"
    override val vmc = RegionViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list
    override val filterView get() = binding.filter

    val args: RegionFragmentArgs by navArgs()

    override fun adapterFactory(value: List<Region>) = RegionRecyclerViewAdapter(args.association, value)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = super.onCreateView(inflater, container, savedInstanceState)

        model.setAssociation(args.association)
        model.refresh()  // TODO This should be handled by superclass

        return root
    }
}
