package cn.zybwz.audio.ui.audioplay

import android.view.View

interface IAudioEvent {

    fun onCrop(view: View)

    fun onFilter(view: View)

    fun onFade(view: View)

    fun onFormat(view: View)
}