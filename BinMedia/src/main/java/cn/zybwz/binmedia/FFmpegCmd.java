package cn.zybwz.binmedia;

import static cn.zybwz.binmedia.UtilKt.ms2FFFormat;

/**
 * todo 缺少 进度 和 状态回调
 */
public class FFmpegCmd {
    private final String cropCmd="ffmpeg -i %s -ss %s -t %s -acodec copy %s";
    private final String pcm2Mp3="ffmpeg -f s16le -ar 44100 -ac 2 -i %s -ar 44100 -ac 2 -y %s";
    static {
        System.loadLibrary("ffmpeg-cmd");
    }

    public native void run(String[] cmd);

    public void crop(String file,long startTime,long duration,String out){
        // ms2FFFormat(startTime)
        String format = String.format(cropCmd, file,ms2FFFormat(startTime/10),ms2FFFormat(duration/10), out);
        run(format.split(" "));
    }

    public void pcm2Mp3(String file,String out){
        String format = String.format(pcm2Mp3, file, out);
        run(format.split(" "));
    }
}
