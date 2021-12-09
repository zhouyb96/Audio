//
// Created by dell on 2021/12/4.
//

#ifndef JNIDEMO_NATIVE_AUDIO_ENCODER_H
#define JNIDEMO_NATIVE_AUDIO_ENCODER_H
void encode(AVFrame *frame);
AVFrame* get_av_frame();
int encoder_init();
void destroy();
#endif //JNIDEMO_NATIVE_AUDIO_ENCODER_H
