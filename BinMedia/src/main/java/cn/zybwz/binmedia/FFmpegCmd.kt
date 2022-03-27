package cn.zybwz.binmedia

import cn.zybwz.binmedia.ms2FFFormat

/**
 * todo 缺少 进度 和 状态回调
 */
class FFmpegCmd {
    //    在任意位置混音
    //"ffmpeg -i %s -i %s -filter_complex " +"[1]adelay=delays=%s|%s[aud1];[0][aud1]amix=inputs=2 -y %s";
    //静音移除 大于间隔0.3s的静音全部移除
    private val muteRemove =
        "ffmpeg -i %s -af silenceremove=stop_periods=-1:stop_duration=0.3:stop_threshold=-30dB %s"
    private val highPassCmd = "ffmpeg -i %s -af highpass=300 %s" //低切300 150 75
    private val gateCmd = "ffmpeg -i %s -filter agate=knee=1:ratio=1.5:range=0.08 %s" //降噪
    private val cropCmd = "ffmpeg -i %s -ss %s -t %s -acodec copy %s"
    private val pcm2Mp3 = "ffmpeg -f s16le -ar 44100 -ac 2 -i %s -ar 44100 -ac 2 -y %s"

    /**
     * 滤镜
     */
    private val echoMountainCmd = "ffmpeg -i %s -filter aecho=0.8:0.9:1000:0.3 %s" //山间回音特效
    private val echoRobotCmd = "ffmpeg -i %s -filter aecho=0.8:0.88:6:0.4 %s" //机器人特效
    private val afadeCmd = "ffmpeg -i %s -filter afade=t=%s:ss=%:ns=%ld:st=%d:d=%d:curve=tri %s"
    private val volumeCmd = "ffmpeg -i %s -filter volume=%s %s" //音量
    private val vibratoCmd = "ffmpeg -i %s -filter vibrato=f=%f:d=%f %s" //颤音
    private val asetrateCmd =
        "ffmpeg -i %s -filter asetrate=sample_rate=%s,atempo=%s %s" //男低音 30000 1.25变调 44100
    private val asetrate2Cmd =
        "ffmpeg -i %s -filter asetrate=sample_rate=%s,atempo=%s %s" //娃娃音 73500 0.6 44100
    private val atempo2Cmd = "ffmpeg -i %s -filter atempo=%s %s" //变速
    private val karaokeCmd = "ffmpeg -i %s -filter stereotools=mlev=0.015625 %s" //karaoke
    private val compandCmd =
        "\"ffmpeg -i %s -filter compand=.3|.3:1|1:-90/-60|-60/-40|-40/-30|-20/-20:6:0:-90:0.2 %s" //感觉像声音加强了
    private val flangeCmd = "ffmpeg -i %s -filter flanger=delay=0 %s" //环绕效果

    companion object {
        init {
            System.loadLibrary("ffmpeg-cmd")
        }
    }

    external fun run(cmd: Array<String?>?)
    fun crop(file: String, startTime: Long, duration: Long, out: String) {
        // ms2FFFormat(startTime)
        val format = String.format(
            cropCmd,
            file,
            ms2FFFormat(startTime / 10),
            ms2FFFormat(duration / 10),
            out
        )
        run(format.split(" ").toTypedArray())
    }

    fun pcm2Mp3(file: String, out: String) {
        val format = String.format(pcm2Mp3, file, out)
        run(format.split(" ").toTypedArray())
    }

    fun echoMountain(){

    }
}