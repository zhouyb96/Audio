package cn.zybwz.binmedia.bean;

public class AudioInfo {

    private int sampleRate;

    private int channels;

    private int bitFormat;

    private int bitType;//0 16整形 1 32整形 2 float

    private long duration;


    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getBitFormat() {
        return bitFormat;
    }

    public void setBitFormat(int bitFormat) {
        this.bitFormat = bitFormat;
    }

    public int getBitType() {
        return bitType;
    }

    public void setBitType(int bitType) {
        this.bitType = bitType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
