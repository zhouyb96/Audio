//
// Created by dell on 2021/11/30.
//

#include <pthread.h>


#include "libavcodec/packet.h"
#include "libavutil/frame.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "person/opensl-player.h"

#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>

#include <android/log.h>
#include <stdbool.h>
#include "native-audio-common.h"
#define LOG_TAG "native-player"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
unsigned  char play_status=0;//0 start 1 recording 2 pause 3 stop
SLObjectItf engineObject;
SLEngineItf audioEngine;
SLPlayItf player= NULL;
static pthread_mutex_t* mutex ;
static pthread_cond_t*  playFinish ;
static pthread_cond_t*  decodeFinish ;

StatusCallBack* callBack;
unsigned char notifyStart=0;
static void PlayCallBack(SLAndroidSimpleBufferQueueItf pcmBufferQueue, void * context)
{
    if (NULL != player) {
        if (play_status==3) {
            callBack->Stop();
            (*player)->SetPlayState(player,SL_PLAYSTATE_STOPPED);
        } else if (play_status==2){
            callBack->Pause();
            (*player)->SetPlayState(player,SL_PLAYSTATE_PAUSED);
        }else if (play_status==1){
            if (notifyStart){
                callBack->Start();
                notifyStart=0;
            }
            unsigned int read_size=read_frame(playBuffer,recorderSize);

            if (read_size<recorderSize){
                (*pcmBufferQueue)->Enqueue(pcmBufferQueue,playBuffer , read_size);
                callBack->Stop();
                (*player)->SetPlayState(player,SL_PLAYSTATE_STOPPED);
                LOGE("SL_PLAYSTATE_STOPPED");
            }else(*pcmBufferQueue)->Enqueue(pcmBufferQueue,playBuffer , recorderSize);
        }
    }
}

void createEngine(){
    LOGE("createEngine start...");
    SLresult sLresult= slCreateEngine(&engineObject,
                                      0,
                                      NULL,
                                      0,
                                      NULL,
                                      0);
    LOGE("slCreateEngine...");
    if (SL_RESULT_SUCCESS != sLresult){
        return;
    }

    sLresult=(*engineObject)->Realize(engineObject, 0);
    LOGE("slCreateEngine Realize...");
    if (SL_RESULT_SUCCESS != sLresult){
        return;
    }
    sLresult=(*engineObject)->GetInterface(engineObject,SL_IID_ENGINE,&audioEngine);
    LOGE("slCreateEngine GetInterface...");
    if (SL_RESULT_SUCCESS != sLresult){
        return;
    }
}

SLAndroidSimpleBufferQueueItf simpleBufferQueueItf;

SLDataFormat_PCM productFormat(int channels,long rate,int bitFormat){
    SLint32 slRate;
    switch (rate) {
        case 8000:
            slRate=SL_SAMPLINGRATE_8;
            break;
        case 44100:
            slRate=SL_SAMPLINGRATE_44_1;
            break;
        case 96000:
            slRate=SL_SAMPLINGRATE_96;
            break;
        default:
            slRate=SL_SAMPLINGRATE_44_1;
            break;
    }
    SLint32 channelMask;
    switch (channels) {
        case 1:
            channelMask = SL_SPEAKER_FRONT_CENTER;
            break;
        case 2:
            channelMask = SL_SPEAKER_FRONT_LEFT |SL_SPEAKER_FRONT_RIGHT;
            break;
        default:
            channelMask = SL_SPEAKER_FRONT_LEFT |SL_SPEAKER_FRONT_RIGHT;
            break;
    }

    SLDataFormat_PCM slDataFormatPcm = {
            SL_DATAFORMAT_PCM,                             //输出PCM格式的数据
            (SLuint32)channels,                                  //输出的声道数量
            slRate,                          //输出的采样频率，这里是44100Hz
            bitFormat,                   //输出的采样格式，这里是16bit
            bitFormat,                   //一般来说，跟随上一个参数
            channelMask,  //双声道配置，如果单声道可以用 SL_SPEAKER_FRONT_CENTER
            SL_BYTEORDER_LITTLEENDIAN                      //PCM数据的大小端排列
    };
    return slDataFormatPcm;
}

void createPlayer(int channels,long rate,int bitFormat) {
    SLObjectItf outputMixObject;
    const SLInterfaceID mids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean mreq[1] = {SL_BOOLEAN_FALSE};
    LOGE("准备创建播放器...");
    SLresult sLresult=(*audioEngine)->CreateOutputMix(audioEngine,&outputMixObject,1,mids,mreq);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("创建混响器成功...");
    sLresult=(*outputMixObject)->Realize(outputMixObject,SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("实例化混响器成功...");
    SLDataLocator_OutputMix outputMix={
            SL_DATALOCATOR_OUTPUTMIX,
            outputMixObject
    };

    SLDataLocator_AndroidSimpleBufferQueue bufferQueue={SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE ,2};
    SLDataFormat_PCM slDataFormatPcm =  productFormat(channels,rate,bitFormat);
    SLDataSource slDataSource = {
            &bufferQueue,
            &slDataFormatPcm,
    };

    SLDataSink slDataSink = {
            &outputMix,NULL
    };
    //需要的接口 操作队列的接口
    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    SLObjectItf playerObject;
    sLresult=(*audioEngine)->CreateAudioPlayer(audioEngine,&playerObject,&slDataSource,&slDataSink,1,ids,req);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("创建播放器对象成功...");
    sLresult=(*playerObject)->Realize(playerObject,SL_BOOLEAN_FALSE);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("实例化播放器对象成功...");
    sLresult=(*playerObject)->GetInterface(playerObject,SL_IID_PLAY,&player);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }


    sLresult= (*playerObject)->GetInterface(playerObject,SL_IID_BUFFERQUEUE,&simpleBufferQueueItf);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("创建缓存队列对象成功...");

    sLresult= (*simpleBufferQueueItf)->RegisterCallback(simpleBufferQueueItf, PlayCallBack,(void*)"");
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("创建播放器成功...");
}


void * play(){
    play_status = 0;

    SLresult sLresult=(*simpleBufferQueueItf)->Enqueue(simpleBufferQueueItf, playBuffer,recorderSize);
    if (SL_RESULT_SUCCESS != sLresult) {
        return NULL;
    }
    LOGE("开始播放");
    notifyStart=1;
    (*player)->SetPlayState(player,SL_PLAYSTATE_PLAYING);
    play_status = 1;

}

void opensl_pause(){
    play_status = 2;
}

void opensl_resume(){
    read_frame(playBuffer,recorderSize);
    (*simpleBufferQueueItf)->Enqueue(simpleBufferQueueItf,playBuffer , recorderSize);
    notifyStart=1;
    (*player)->SetPlayState(player,SL_PLAYSTATE_PLAYING);
    play_status = 1;
}
void opensl_stop(){
    play_status = 3;
}

void registerCallBack(StatusCallBack* call){
    callBack=call;
}

