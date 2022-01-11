package cn.zybwz.binmedia;

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

    public native void addProgressListener(IProgressListener progressListener);
    public native void addStatusListener(IStatusChangeListener statusListener);

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
