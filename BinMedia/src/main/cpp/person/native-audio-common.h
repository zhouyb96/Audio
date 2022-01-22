//
// Created by dell on 2021/12/8.
//

#ifndef JNIDEMO_NATIVE_AUDIO_COMMON_H
#define JNIDEMO_NATIVE_AUDIO_COMMON_H
#include "audio-cache-pool.h"
struct FFmpegAudioInfo{
    long sample_rate;//采样率
    int channel_layout;//通道布局
    int av_fmt;//ffmpeg的采样位数的枚举值
    int bit_format;//采样位数
    long bit_rate;//比特率
    int channels;//通道数
    int duration;//时长 单位秒
};
struct FFmpegAudioInfo fFmpegAudioInfo;

struct ResampleInfo{
    uint64_t channel_layout;
    enum AVSampleFormat fmt;
    int sample_rate;
};

struct ResampleInfo inResampleInfo;
struct ResampleInfo outResampleInfo;

static char enableFilter=1;

#endif //JNIDEMO_NATIVE_AUDIO_COMMON_H
