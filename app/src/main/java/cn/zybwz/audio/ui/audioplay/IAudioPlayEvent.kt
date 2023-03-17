package cn.zybwz.audio.ui.audioplay

import android.view.View

interface IAudioPlayEvent {
    fun onControl(view: View)
    fun onBack(view: View)
    fun onForward(view: View)
    fun onInfo(view: View)
}