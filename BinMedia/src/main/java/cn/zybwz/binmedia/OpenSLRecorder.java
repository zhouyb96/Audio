package cn.zybwz.binmedia;

public class OpenSLRecorder {
    static {
        System.loadLibrary("opensl-recorder");
    }
    public native void init();

    public native void setOutputProperty(RecorderParams recorderParams);

    public native RecorderParams getOutputProperty();

    public native void start(String savePath);

    public native void pause();

    public native void resume();

    public native void stop();

    public native void destroy();

    public native void addProgressListener(IProgressListener listener);

    public native void addStatusChangeListener(IStatusChangeListener listener);

    public interface IProgressListener{
        void onProgress(long recorderMs);
        void onWave(int db);
    }

    interface IStatusChangeListener{
        void onStart();
        void onPause();
        void onStop();
        void onError(int errorCode);
    }
}
