package org.northwinds.app.sotachaser.ui.associations

import android.view.LayoutInflater
import dagger.hilt.android.AndroidEntryPoint
import org.northwinds.app.sotachaser.databinding.FragmentSummitListBinding
import org.northwinds.app.sotachaser.domain.models.Association
import org.northwinds.app.sotachaser.ui.abstraction.AbstractFilterListFragment


@AndroidEntryPoint
class AssociationFragment : AbstractFilterListFragment<Association, FragmentSummitListBinding, AssociationRecyclerViewAdapter, AssociationViewModel>() {
    override val TAG = "SOTAChaser-AssociationFragment"
    override val vmc = AssociationViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> FragmentSummitListBinding
        = FragmentSummitListBinding::inflate
    override val listView get() = binding.list
    override val filterView get() = binding.filter

    override fun adapterFactory(values: List<Association>) = AssociationRecyclerViewAdapter(values)

    //companion object {
    //    const val TAG = "SOTAChaser-AssociationFragment"
    //}
}
