package cn.zybwz.audio.utils

import cn.zybwz.audio.bean.FilterBean
import cn.zybwz.audio.R

object AudioConstant {
    fun getFilters():MutableList<FilterBean>{
        val filters= mutableListOf<FilterBean>()
        filters.add(FilterBean("无特效",R.drawable.ic_filter,-1))
        filters.add(FilterBean("回声",R.drawable.ic_filter,0))
        filters.add(FilterBean("机器人",R.drawable.ic_filter,1))
        filters.add(FilterBean("低声",R.drawable.ic_filter,2))
        filters.add(FilterBean("童声",R.drawable.ic_filter,3))
        filters.add(FilterBean("颤音",R.drawable.ic_filter,4))
        filters.add(FilterBean("环绕音",R.drawable.ic_filter,5))
//        filters.add(FilterBean("回声",R.drawable.ic_filter))
//        filters.add(FilterBean("回声",R.drawable.ic_filter))
        return filters
    }
}