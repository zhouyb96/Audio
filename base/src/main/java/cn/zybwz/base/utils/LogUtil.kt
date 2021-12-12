package cn.zybwz.base.utils

import android.util.Log
import cn.zybwz.base.BaseFactory


object LogUtil {
    fun v(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.v(tag, msg)
        }
    }

    fun i(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.i(tag, msg)
        }
    }

    fun d(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.d(tag, msg)
        }
    }

    fun w(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.w(tag, msg)
        }
    }

    fun e(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.e(tag, msg)
        }
    }

    fun f(tag:String,msg:String){
        if (BaseFactory.DEBUG){
            Log.e(tag, msg)
        }
    }


}