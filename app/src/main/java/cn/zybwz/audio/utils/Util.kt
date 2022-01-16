package cn.zybwz.audio.utils


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

    return String.format("%02d:%02d.%02d", m, s, l)
}