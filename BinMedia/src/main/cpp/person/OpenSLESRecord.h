//
// Created by dell on 2021/11/24.
//

#ifndef JNIDEMO_OPENSLESRECORD_H
#define JNIDEMO_OPENSLESRECORD_H


#include <cstdio>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include "RecordBuffer.h"
#include <string>
//录制大小设为4096
#define RECORDER_FRAMES (2048)
static unsigned recorderSize = RECORDER_FRAMES * 2;
//typedef struct RecordConfig{
//    char * saveType = "wav";
//    SLuint32 channelNumber = 2;
//    SLuint32 simpleRate = SL_SAMPLINGRATE_44_1;
//    SLuint16 simpleFormat = SL_PCMSAMPLEFORMAT_FIXED_16;
//    SLuint32 containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
//    SLuint32 channelMask = SL_SPEAKER_FRONT_LEFT |SL_SPEAKER_FRONT_RIGHT;
//}RecordConfig;

class OpenSLESRecord {
private:
    char * saveType = (char *)"wav";
    SLDataFormat_PCM slDataFormatPcm;
    static void createEngine();
    void createRecorder();
    void createPlayer();
public:
    OpenSLESRecord();
    SLRecordItf recorderRecorder= nullptr;
    SLPlayItf player= nullptr;
    short recordBuffer[RECORDER_FRAMES*sizeof(short)];
    short playBuffer[RECORDER_FRAMES*sizeof(short)];
    FILE *pcmFile = nullptr;

    bool recordFinished;
    bool playFinished;
    void startRecord(const char * path,const char * saveType);
    void stopRecord();
    void configRecorder(SLDataFormat_PCM slDataFormatPcm);
    SLDataFormat_PCM getRecorderConfig();
    void play(const char * path);
    void stop();

};


#endif //JNIDEMO_OPENSLESRECORD_H
