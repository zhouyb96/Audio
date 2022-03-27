package cn.zybwz.binmedia

import cn.zybwz.binmedia.bean.FilterInfo


fun ms2Format(ms: Long): String {
    val l = ms % 100
    val s = (ms / 100) % 60
    val m = ms / 60 / 100

    return String.format("%02d:%02d:%02d", m, s, l)
}

fun ms2FFFormat(ms: Long): String {
    val l = ms % 100
    val s = (ms / 100) % 60
    val m = ms / 60 / 100

    return String.format("00:%02d:%02d.%02d", m, s, l)
}

fun getFilterInfo():MutableList<FilterInfo>{
    val filterInfoList= mutableListOf<FilterInfo>()
    //filterInfoList.add(getEchoInfo())
    filterInfoList.add(getVibratoInfo())

    return filterInfoList
}


fun getEchoLiveDefault():String{
    val format=echoMountain.realTimeFormat

    val result = String.format(
        format,
        echoMountain.defaultArg[0],
        echoMountain.defaultArg[1],
        echoMountain.defaultArg[2],
        echoMountain.defaultArg[3]
    )
    return result
}

fun getEchoDefault():String{
    return String.format(echoMountain.realTimeFormat, echoMountain.defaultArg)
}

//fun getEcho(inGain:String,outGain:String,delay:String,decay:String){
//
//    getEchoInfo().realTimeFormat
//}
val echoMountain:FilterInfo by lazy{
    val echoArray= arrayOf("0.8","0.9","1000","0.3")

    FilterInfo("回声","aecho",
        "in_gain=%s:out_gain=%s:delays=%s:decays=%s",
        "ffmpeg -i %s -filter aecho=%s:%s:%s:%s %s",echoArray)
}
//fun getEchoInfo():FilterInfo{
//
//}

fun getVibratoInfo():FilterInfo{
    val vibratoArray= arrayOf("5","0.5")
    return FilterInfo("回声","vibrato",
        "f=%s:d=%s",
        "ffmpeg -i %s -filter vibrato=f=%s:d=%s %s",vibratoArray)
}