package cn.zybwz.base.utils

import android.view.Gravity
import android.widget.Toast
import cn.zybwz.base.BaseFactory


object ToastUtil {
    fun show(msg:String){
        val makeText = Toast.makeText(BaseFactory.application, msg, Toast.LENGTH_LONG)
        makeText.setGravity(
            Gravity.CENTER,
            0,
            0
        )
        makeText.show()
    }
}