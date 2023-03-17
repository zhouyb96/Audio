package cn.zybwz.audio.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log


import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import cn.zybwz.audio.R
import cn.zybwz.binmedia.bean.AudioInfo
import java.io.File
import java.util.*

class FormatRenameDialog(context: Context):Dialog(context,R.style.dialog_bottom_full) {

    private lateinit var name:EditText
    private lateinit var type:TextView
    private lateinit var title:TextView
    private lateinit var confirm:Button
    var event:Event?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_format_rename)
        name=findViewById(R.id.name)
        type=findViewById(R.id.type)
        confirm=findViewById(R.id.confirm)
        title=findViewById(R.id.format)
        confirm.setOnClickListener {
            event?.onConfirm(name.text.toString()+type.text.toString(),title.text.toString())
        }
        val window: Window? = window
        window?.setGravity(Gravity.BOTTOM)
        window?.setWindowAnimations(R.style.share_animation)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }


    fun setName(name: String){
        val nameHead = name.substring(0, name.indexOfLast { it == '.' })
        this.name.setText(nameHead)
        this.type.setText(name.substring(name.indexOfLast { it == '.' }))
        title.setText(name.substring(name.indexOfLast { it == '.' }+1)
            .uppercase(Locale.getDefault()))
    }

    interface Event{
        fun onConfirm(name:String,type:String)
    }
}