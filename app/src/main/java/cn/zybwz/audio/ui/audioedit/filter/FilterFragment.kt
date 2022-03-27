package cn.zybwz.audio.ui.audioedit.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.zybwz.audio.R
import cn.zybwz.audio.adapter.FilterAdapter
import cn.zybwz.audio.bean.FilterBean
import cn.zybwz.audio.databinding.FragmentFilterBinding
import cn.zybwz.audio.ui.audioplay.AudioPlayActivityVM
import cn.zybwz.audio.utils.AudioConstant
import cn.zybwz.base.BaseFragment

class FilterFragment : BaseFragment<FilterFragmentVM,FragmentFilterBinding>() {

    companion object {
        fun newInstance() =
            FilterFragment()
    }

    override val viewModel: FilterFragmentVM by viewModels()
    private val activityVM:AudioPlayActivityVM by viewModels(
        ownerProducer = {requireActivity()}
    )

    override fun bindLayout(): Int = R.layout.fragment_filter
    private val filterAdapter=FilterAdapter()
    override fun initViewModel() {

    }

    override fun initView(view: View) {
        binding.filterRecycler.layoutManager=
            LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
        filterAdapter.addData(AudioConstant.getFilters())
        filterAdapter.event=object : FilterAdapter.Event{
            override fun onClick(filterBean: FilterBean, position: Int) {
                activityVM.filterData.value=filterBean
            }
        }
        binding.filterRecycler.adapter=filterAdapter
    }

}