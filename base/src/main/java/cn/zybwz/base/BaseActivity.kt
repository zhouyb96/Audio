package cn.zybwz.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.DataBinderMapperImpl
import cn.zybwz.base.utils.LogUtil
import com.gyf.immersionbar.ktx.immersionBar

abstract class BaseActivity<VM:BaseViewModel,M:ViewDataBinding>:AppCompatActivity() {
    abstract val viewModel: VM
    lateinit var binding:M
    val TAG = BaseActivity::class.simpleName
    abstract fun bindLayout():Int


    abstract fun titleBar():View?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this, bindLayout())
        immersionBar {
            statusBarColor(R.color.tran)
            navigationBarColor(R.color.tran)
            this@BaseActivity.titleBar()?.let { titleBar(it) }
            statusBarDarkFont(true)
            autoDarkModeEnable(true)
        }
        binding.lifecycleOwner = this
        viewModel.showLoading.observe(this,{
            if (it)
                showLoading()
            else hideLoading()
        })
        initData()
        initView()
        initViewModel()
    }

    fun showLoading(){

    }

    fun hideLoading(){

    }

    abstract fun initViewModel()

    abstract fun initView()

    abstract fun initData()

}