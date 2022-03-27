package cn.zybwz.audio.ui.audioplay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cn.zybwz.audio.R
import cn.zybwz.audio.databinding.FragmentAudioBinding
import cn.zybwz.audio.ui.audioedit.crop.AudioCropActivity
import cn.zybwz.base.BaseFragment

class AudioFragment : BaseFragment<AudioPlayFragmentVM,FragmentAudioBinding>() {

    override val viewModel: AudioPlayFragmentVM by viewModels()

    override fun bindLayout(): Int = R.layout.fragment_audio

    override fun initViewModel() {

    }

    override fun initView(view: View) {
        binding.event=requireActivity() as IAudioEvent
    }
}