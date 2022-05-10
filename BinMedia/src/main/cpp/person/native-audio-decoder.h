//
// Created by dell on 2021/12/28.
//

#ifndef JNIDEMO_NATIVE_AUDIO_DECODE_H
#define JNIDEMO_NATIVE_AUDIO_DECODE_H
#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>


#include "libavcodec/packet.h"
#include "libavutil/frame.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavfilter/avfilter.h"
#include "libavfilter/buffersink.h"
#include "libavfilter/buffersrc.h"
#include <libavutil/channel_layout.h>
#include <semaphore.h>
#include <android/log.h>
#include <unistd.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "native-audio-common.h"

void seek(long time);
int decode_audio(const char *file_name);
void (*onProgress)(long ms);
void (*deal_packet)(AVCodecContext *ctx,AVFrame *frame,AVPacket * packet);
void intercept_decode();
void decoder_release();
#endif //JNIDEMO_NATIVE_AUDIO_DECODE_H
