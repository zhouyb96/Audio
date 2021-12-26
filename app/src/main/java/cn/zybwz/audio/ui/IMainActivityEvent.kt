package cn.zybwz.audio.ui

import android.view.View

interface IMainActivityEvent {
    fun onStartOrStop(view:View)
    fun onPauseOrResume(view: View)
    fun onRecordFile(view: View)
}