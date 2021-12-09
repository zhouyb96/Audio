//
// Created by dell on 2021/12/1.
//

#ifndef JNIDEMO_NATIVE_LIB_H
#define JNIDEMO_NATIVE_LIB_H
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
#include <semaphore.h>
#include <android/log.h>
#include <unistd.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "native-player.h"
#define LOG_TAG "NativeAudio"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
pthread_mutex_t mutex ;
pthread_cond_t  playFinish ;
pthread_cond_t  decodeFinish ;

static JavaVM* javaVm;
FILE *f, *outfile ,* rightFile;
AVCodecContext *c= NULL;
AVFrame *decoded_frame = NULL;
AVPacket *pkt;
int isDecode = 1;
int bufferInSize=0;
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096
#endif //JNIDEMO_NATIVE_LIB_H
