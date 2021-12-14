package cn.zybwz.audio

import android.view.View

interface IMainActivityEvent {
    fun onStartOrStop(view:View)
    fun onPauseOrResume(view: View)
}