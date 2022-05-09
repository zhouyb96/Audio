package cn.zybwz.binmedia;


import cn.zybwz.binmedia.bean.FilterInfo;

public class FilterUtil {
    public final static int ECHO=0;
    private static FilterInfo echoMountain=new FilterInfo("回声","aecho","in_gain=%s:out_gain=%s:delays=%s:decays=%s",
            "ffmpeg -i %s -filter aecho=%s:%s:%s:%s %s",new String[]{"0.8","0.9","1000","0.3"});
    private static  FilterInfo vibrato=new FilterInfo("颤音","vibrato","f=%s:d=%s",
            "ffmpeg -i %s -filter vibrato=f=%s:d=%s %s",new String[]{"5","0.5"});
    private static FilterInfo robot=new FilterInfo("机器人","aecho","in_gain=%s:out_gain=%s:delays=%s:decays=%s",
            "ffmpeg -i %s -filter aecho=%s:%s:%s:%s %s",new String[]{"0.8","0.95","50","0.4"});

    private static FilterInfo man=new FilterInfo("浑厚","atempo,asetrate","%s,sample_rate=%s",
                "ffmpeg -i %s -filter asetrate=sample_rate=%s,atempo=%s %s",new String[]{"1.25","35280"});

    private static FilterInfo silence=new FilterInfo("浑厚","silenceremove","stop_periods=-1:stop_duration=%s:stop_threshold=-30dB",
            "ffmpeg -i %s -filter asetrate=sample_rate=%s,atempo=%s %s",new String[]{"0.3"});

    private static FilterInfo baby=new FilterInfo("娃娃音","asetrate,atempo","sample_rate=%s,%s",
            "ffmpeg -i %s -filter asetrate=sample_rate=%s,atempo=%s %s",new String[]{"73500","0.6"});


    private static FilterInfo flanger=new FilterInfo("环绕","flanger","delay=%s",
            "ffmpeg -i %s -filter  flanger=delay=%s %s",new String[]{"0"});

    private static FilterInfo karaoke=new FilterInfo("卡拉OK","stereotools","mlev=%s",
            "ffmpeg -i %s -filter  stereotools=mlev=%s %s",new String[]{"0.015625"});

    private static FilterInfo lowcut=new FilterInfo("低切","highpass","%s",
            "ffmpeg -i %s -filter  highpass=%s %s",new String[]{"300"});

    private static FilterInfo noiseGate=new FilterInfo("低切","agate","knee=%s:ratio=%s:range=%s",
            "ffmpeg -i %s -filter  agate=%s %s",new String[]{"1","1.5","0.08"});

    public static FilterInfo getLive(int type){

        switch (type){
            case 0:
                return echoMountain;
            case 1:
                return vibrato;
            case 2:
                return man;
            case 3:
                return baby;
            case 4:
                return flanger;
            case 5:
                return robot;
            case 6:
                return null;

        }
        return null;
    }

    public static FilterInfo getProcess(int type){
        switch (type){
            case 0:
                return lowcut;
            case 1:
                return noiseGate;
        }
        return null;
    }
}
