//
// Created by dell on 2021/11/23.
//
#include <jni.h>
#include <android/log.h>
#include "person/OpenSLESRecord.h"
#define LOG_TAG "NativeAudio"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
OpenSLESRecord* openSlesRecord = nullptr;
extern "C" JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_start(
        JNIEnv* env,
        jobject /* this */,jstring path,jstring type) {
    if (nullptr==openSlesRecord){
        LOGE("please first call init");
        return;
    }
    const char * p=env->GetStringUTFChars(path,0);
    const char * t=env->GetStringUTFChars(type,0);
    openSlesRecord->startRecord(p,t);
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_startPlay(
        JNIEnv* env,
        jobject /* this */,jstring path) {
    if (nullptr==openSlesRecord){
        LOGE("please first call init");
        return;
    }
    const char * p=env->GetStringUTFChars(path,0);

    openSlesRecord->play(p);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_stop(JNIEnv *env, jobject thiz) {
    if (nullptr==openSlesRecord){
        LOGE("please first call init");
        return;
    }
    openSlesRecord->stopRecord();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_init(JNIEnv *env, jobject thiz) {
    if (nullptr!=openSlesRecord){
        LOGE("please do not reinit");
        return;
    }
    openSlesRecord = new OpenSLESRecord();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_destroy(JNIEnv *env, jobject thiz) {
    if (nullptr==openSlesRecord){
        return;
    }
    delete openSlesRecord;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_setSimpleRate(JNIEnv *env, jobject thiz,jlong rate) {
    SLDataFormat_PCM slDataFormatPcm=openSlesRecord->getRecorderConfig();
    slDataFormatPcm.samplesPerSec=(SLuint32)rate;
    openSlesRecord->configRecorder(slDataFormatPcm);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_setSimpleFormat(JNIEnv *env, jobject thiz,jint bitFormat) {
    SLDataFormat_PCM slDataFormatPcm=openSlesRecord->getRecorderConfig();
    if (bitFormat==8){
        slDataFormatPcm.bitsPerSample=(SLuint32)SL_PCMSAMPLEFORMAT_FIXED_8;
        slDataFormatPcm.containerSize=(SLuint32)SL_PCMSAMPLEFORMAT_FIXED_8;
    } else if (bitFormat==16){
        slDataFormatPcm.bitsPerSample=(SLuint32)SL_PCMSAMPLEFORMAT_FIXED_16;
        slDataFormatPcm.containerSize=(SLuint32)SL_PCMSAMPLEFORMAT_FIXED_16;
    }

    openSlesRecord->configRecorder(slDataFormatPcm);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_jnidemo_BinRecorder_setChannelNumber(JNIEnv *env, jobject thiz,jint channelNumber) {
    SLDataFormat_PCM slDataFormatPcm=openSlesRecord->getRecorderConfig();
    if (channelNumber==1){
        slDataFormatPcm.numChannels=(SLuint32)channelNumber;
        slDataFormatPcm.channelMask = SL_SPEAKER_FRONT_CENTER;
    } else {
        slDataFormatPcm.numChannels=(SLuint32)2;
        slDataFormatPcm.channelMask = SL_SPEAKER_FRONT_LEFT|SL_SPEAKER_FRONT_RIGHT;
    }
    openSlesRecord->configRecorder(slDataFormatPcm);
}

void getInfo(){

}


