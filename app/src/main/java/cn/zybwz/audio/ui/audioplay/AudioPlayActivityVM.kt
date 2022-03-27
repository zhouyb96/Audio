package cn.zybwz.audio.ui.audioplay

import androidx.lifecycle.MutableLiveData
import cn.zybwz.audio.bean.FilterBean
import cn.zybwz.audio.bean.RecordBean
import cn.zybwz.audio.bean.ToolBean
import cn.zybwz.audio.db.AudioDataBase
import cn.zybwz.base.BaseViewModel
import cn.zybwz.audio.R

class AudioPlayActivityVM:BaseViewModel() {
    val playStatusData = MutableLiveData(0)
    var filterData = MutableLiveData<FilterBean>()
    var filterIndex=-1

    fun getBaseTool():MutableList<ToolBean>{
        val toolBeans= mutableListOf<ToolBean>()
        toolBeans.add(ToolBean(R.drawable.ic_edit,"剪辑"))
        toolBeans.add(ToolBean(R.drawable.ic_filter,"特效"))
        toolBeans.add(ToolBean(R.drawable.ic_deal,"处理"))
        toolBeans.add(ToolBean(R.drawable.ic_format_change,"格式"))
        toolBeans.add(ToolBean(R.drawable.ic_voice_2_text,"转文字"))
        return toolBeans
    }

    fun getEditTool():MutableList<ToolBean>{
        val toolBeans= mutableListOf<ToolBean>()
        toolBeans.add(ToolBean(R.drawable.crop,"裁剪"))
        toolBeans.add(ToolBean(R.drawable.ic_fade,"淡入淡出"))
        toolBeans.add(ToolBean(R.drawable.ic_mix,"混音"))
        toolBeans.add(ToolBean(R.drawable.ic_speech_10,"倍速"))
        toolBeans.add(ToolBean(R.drawable.ic_mute,"静音消除"))
        return toolBeans
    }

    fun getFilterTool():MutableList<ToolBean>{
        val toolBeans= mutableListOf<ToolBean>()
        toolBeans.add(ToolBean(R.drawable.ic_null,"无"))
        toolBeans.add(ToolBean(R.drawable.crop,"回声"))
        toolBeans.add(ToolBean(R.drawable.ic_viborate,"颤音"))
        toolBeans.add(ToolBean(R.drawable.ic_man,"浑厚"))
        toolBeans.add(ToolBean(R.drawable.ic_baby,"娃娃音"))
        toolBeans.add(ToolBean(R.drawable.ic_sorround,"环绕"))
        toolBeans.add(ToolBean(R.drawable.ic_robot,"机器人"))
        toolBeans.add(ToolBean(R.drawable.ic_klok,"卡拉OK"))
        return toolBeans
    }
}