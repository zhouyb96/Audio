package cn.zybwz.audio

import android.view.View

interface IMainActivityEvent {
    fun onStart(view:View)
    fun onPause(view: View)
    fun onResume(view: View)
    fun onStop(view: View)
}