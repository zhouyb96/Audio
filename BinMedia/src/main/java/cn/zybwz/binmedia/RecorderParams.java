package cn.zybwz.binmedia;

public class RecorderParams {
    private long sampleRate=44100000;
    private int bitFormat=16;
    private int channels=2;
    private int channelLayout=3;

    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBitFormat() {
        return bitFormat;
    }

    public void setBitFormat(int bitFormat) {
        this.bitFormat = bitFormat;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getChannelLayout() {
        return channelLayout;
    }

    public void setChannelLayout(int channelLayout) {
        this.channelLayout = channelLayout;
    }
}
