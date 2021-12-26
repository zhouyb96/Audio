//
// Created by Administrator on 2021/12/23.
//

#ifndef AUDIO_AUDIO_COMMON_H
#define AUDIO_AUDIO_COMMON_H
struct AudioInfo{
    int decode_id;
    int channels;
    long sample_rate;
    long channel_layout;
    int type;
    int bit_rate;
};
static struct AudioInfo audioInfo;
#endif //AUDIO_AUDIO_COMMON_H
