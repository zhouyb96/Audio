package cn.zybwz.binmedia;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import cn.zybwz.binmedia.bean.FilterInfo;

public class BinPlayer {
    static {
        System.loadLibrary("native-audio-player");
    }
    public native void  play(String path);
    public native void  seek(long time);
    public native void  pause();
    public native void  stop();
    public native void  resume();
    public native void  destroy();
    public native void  speed(double speed);
    public native void addProgressListener(IProgressListener progressListener);
    public native void addStatusListener(IStatusChangeListener statusListener);

    /**
     * @param type
     */
    public native void addFilter(int type);

    public native void addFilterCustom(String name,String str,int filter);

    public void addLiveFilter(@NotNull FilterInfo filterInfo){
        String live = filterInfo.buildLiveCmd();
        if (live.contains(",")){
            String[] splitCmd = live.split(",");
            String[] splitName = filterInfo.getName().split(",");
            for (int i=0;i<splitCmd.length;i++){
                Log.e("BinPlayer", "addLiveFilter: "+splitName[i]+"   "+splitCmd[i] );
                if (i< splitCmd.length-1)
                    addFilterCustom(splitName[i],splitCmd[i],0);
                else addFilterCustom(splitName[i],splitCmd[i],1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else addFilterCustom(filterInfo.getName(),live,1);
        //addFilterCustom(filterInfo.getName(),live);
    }

    public interface IProgressListener{
        void onProgress(long recorderMs);
    }

    public interface IStatusChangeListener{
        void onStart();
        void onPause();
        void onStop();
        void onError(int errorCode);
    }
}
