package cn.zybwz.audio.ui.vosk

import cn.zybwz.audio.R
import android.view.View
import androidx.activity.viewModels
import cn.zybwz.audio.databinding.ActivityVoskBinding
import cn.zybwz.base.BaseActivity
import org.vosk.Model
import org.vosk.android.StorageService
import java.io.IOException
import org.vosk.android.SpeechStreamService

import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.InputStream
import java.lang.Exception


class VoskActivity : BaseActivity<VoskActivityVM,ActivityVoskBinding>(),RecognitionListener {
    override val viewModel: VoskActivityVM
        by viewModels()

    override fun bindLayout(): Int = R.layout.activity_vosk

    override fun titleBar(): View? {
        return binding.titleBar
    }

    override fun initViewModel() {

    }

    override fun initView() {

    }

    override fun initData() {

    }
    private var model: Model? = null
    private fun initModel() {
        StorageService.unpack(this, "model-en-us", "model",
            { model: Model ->
                this.model = model
                //setUiState(STATE_READY)
            }
        ) { exception: IOException ->
            exception.printStackTrace()
//            setErrorState(
//                "Failed to unpack the model" + exception.message
//            )
        }
    }

    private val speechService: SpeechService? = null
    private var speechStreamService: SpeechStreamService? = null
    private fun recognizeFile() {
        if (speechStreamService != null) {

            speechStreamService?.stop()
            speechStreamService = null
        } else {
            try {
                val rec = Recognizer(
                    model, 16000f, "[\"one zero zero zero one\", " +
                            "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]"
                )
                val ais: InputStream = assets.open(
                    "10001-90210-01803.wav"
                )
                if (ais.skip(44) !== 44L) throw IOException("File too short")
                speechStreamService = SpeechStreamService(rec, ais, 16000f)
                speechStreamService?.start(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPartialResult(hypothesis: String?) {

    }

    override fun onResult(hypothesis: String?) {

    }

    override fun onFinalResult(hypothesis: String?) {

    }

    override fun onError(exception: Exception?) {

    }

    override fun onTimeout() {

    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_vosk)
//    }
}