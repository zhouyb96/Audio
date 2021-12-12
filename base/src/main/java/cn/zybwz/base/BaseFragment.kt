package cn.zybwz.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<VM:BaseViewModel,M: ViewDataBinding>:Fragment() {
    abstract val viewModel: VM
    lateinit var binding:M

    abstract fun bindLayout():Int
    abstract fun initViewModel()

    abstract fun initView(view: View)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,bindLayout(),container,false)
        val root = binding.root
        initView(root)
        initViewModel()
        viewModel.showLoading.observe(this,{
            if (it)
                showLoading()
            else hideLoading()
        })
        return root
    }

    fun showLoading(){

    }

    fun hideLoading(){

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }
}