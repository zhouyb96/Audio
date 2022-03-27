package cn.zybwz.audio.ui.vosk

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import cn.zybwz.audio.R
import cn.zybwz.audio.databinding.ActivityVoskBinding
import cn.zybwz.audio.databinding.FragmentVoskBinding
import cn.zybwz.base.BaseFragment
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class VoskFragment : BaseFragment<VoskActivityVM,FragmentVoskBinding>(),RecognitionListener {


    companion object {

        @JvmStatic
        fun newInstance(path: String) =
            VoskFragment().apply {
                arguments=Bundle().apply {
                    putString("path",path)
                }
            }
    }

    override val viewModel: VoskActivityVM by viewModels()

    override fun bindLayout(): Int {
        return R.layout.fragment_vosk
    }

    override fun initViewModel() {

    }

    private lateinit var filePath:String
    override fun initView(view: View) {
        filePath= arguments?.getString("path")?:return
        initModel()

    }

    private var model: Model? = null
    private fun initModel() {
        StorageService.unpack(requireContext(), "model-en-us", "model",
            { model: Model ->
                this.model = model
                //setUiState(STATE_READY)
                Handler(Looper.getMainLooper()).postDelayed({
                    recognizeFile(filePath)
                },500)

            }
        ) { exception: IOException ->
            exception.printStackTrace()
//            setErrorState(
//                "Failed to unpack the model" + exception.message
//            )
        }
    }


    private var speechStreamService: SpeechStreamService? = null

    fun recognizeFile(path: String) {
        Log.e("TAG", "recognizeFile: "+path)
        if (speechStreamService != null) {

            speechStreamService?.stop()
            speechStreamService = null
        } else {
            try {
                val rec = Recognizer(
                    model, 16000f
                )
                //val w="/storage/emulated/0/Android/data/cn.zybwz.audio/files/recorder/"+"10001-90210-01803.wav"
                val ais: InputStream = FileInputStream(path)
                //if (ais.skip(44) !== 44L) throw IOException("File too short")
                speechStreamService = SpeechStreamService(rec, ais, 16000f)
                speechStreamService?.start(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPartialResult(hypothesis: String?) {
        Log.e("TAG", "onPartialResult: $hypothesis")
    }

    override fun onResult(hypothesis: String?) {
        Log.e("TAG", "onResult: $hypothesis")
    }

    override fun onFinalResult(hypothesis: String?) {
        binding.voskResult.setText(hypothesis?:"解析失败")

    }

    override fun onError(exception: Exception?) {
        exception?.printStackTrace()
    }

    override fun onTimeout() {
        Toast.makeText(requireContext(),"超时",Toast.LENGTH_LONG).show()
    }
}