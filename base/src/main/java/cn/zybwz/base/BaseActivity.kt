package cn.zybwz.base

import android.os.Bundle
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.DataBinderMapperImpl
import cn.zybwz.base.utils.LogUtil

abstract class BaseActivity<VM:BaseViewModel,M:ViewDataBinding>:AppCompatActivity() {
    abstract val viewModel: VM
    lateinit var binding:M
    val TAG = BaseActivity::class.simpleName
    abstract fun bindLayout():Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, bindLayout())
        binding.lifecycleOwner = this
        viewModel.showLoading.observe(this,{
            if (it)
                showLoading()
            else hideLoading()
        })
        initView()
        initViewModel()
    }

    fun showLoading(){

    }

    fun hideLoading(){

    }

    abstract fun initViewModel()

    abstract fun initView()

}