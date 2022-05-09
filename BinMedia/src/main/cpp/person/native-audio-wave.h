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

unsigned char* decode_wave(const char *file_name,int sample);

#endif //JNIDEMO_NATIVE_AUDIO_DECODE_H
