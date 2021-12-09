//
// Created by dell on 2021/11/24.
//
#include "person/OpenSLESRecord.h"
#include <android/log.h>
#include "person/OpenSLESRecord.h"
#define LOG_TAG "OpenSLESRecord"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型

SLObjectItf engineObject;
SLEngineItf audioEngine;


static void RecordCallback(SLAndroidSimpleBufferQueueItf bufferQueue, void *context) {

    OpenSLESRecord *recorder = (OpenSLESRecord*) context;

    if (nullptr==recorder)
        return;
    if (nullptr != recorder->recordBuffer) {
        fwrite(recorder->recordBuffer, 1, recorderSize, recorder->pcmFile);
    }

    if (recorder->recordFinished) {
        (*recorder->recorderRecorder)->SetRecordState(recorder->recorderRecorder,
                                                      SL_RECORDSTATE_STOPPED);
        //刷新缓冲区后，关闭流
        fclose(recorder->pcmFile);
        //释放内存
//        delete recorder->recordBuffer;
//        recorder->recordBuffer = NULL;
    } else {
        LOGE("recording...");
        (*bufferQueue)->Enqueue(bufferQueue, recorder->recordBuffer,
                                recorderSize);
    }
}

static void PlayCallBack(SLAndroidSimpleBufferQueueItf pcmBufferQueue, void * context)
{
    OpenSLESRecord *recorder = (OpenSLESRecord*) context;
    if (!feof(recorder->pcmFile))
    {
        fread(recorder->playBuffer, recorderSize, 1, recorder->pcmFile);
    } else {
        recorder->playFinished= true;
        fclose(recorder->pcmFile);
        return;
    }

//    getPcmData(&(recorder->playBuffer));
    // for streaming playback, replace this test by logic to find and fill the next buffer
    if (NULL != recorder) {
        if (recorder->playFinished) {
            (*recorder->player)->SetPlayState(recorder->player,SL_PLAYSTATE_STOPPED);
            fclose(recorder->pcmFile);
        } else{
            // enqueue another buffer
            (*pcmBufferQueue)->Enqueue(pcmBufferQueue, recorder->playBuffer, recorderSize);
            // the most likely other result is SL_RESULT_BUFFER_INSUFFICIENT,
            // which for this code example would indicate a programming error
        }
    }
}

void OpenSLESRecord::createEngine(){
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

SLAndroidSimpleBufferQueueItf recorderBufferQueue; //Buffer接口

void OpenSLESRecord::createRecorder(){

    SLDataLocator_IODevice ioDevice = {SL_DATALOCATOR_IODEVICE,         //类型
                                       SL_IODEVICE_AUDIOINPUT,          //device类型 选择了音频输入类型
                                       SL_DEFAULTDEVICEID_AUDIOINPUT,   //deviceID
                                       nullptr                             //device实例
    };
    SLDataSource slDataSource = {
            &ioDevice,
            nullptr
    };


    // 设置输出buffer队列
    SLDataLocator_AndroidSimpleBufferQueue buffer_queue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,    //类型 这里只能是这个常量
            2                                           //buffer的数量
    };

    LOGE("设置输出数据的格式...");
    SLDataSink audioSink = {
            &buffer_queue,                   //SLDataFormat_PCM配置输出
            &slDataFormatPcm                      //输出数据格式
    };
    LOGE("audioSink...");

    // 创建录制的对象
    const SLInterfaceID id[1] = {SL_IID_ANDROIDSIMPLEBUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};
    SLObjectItf recorderObject;
    SLresult sLresult = (*audioEngine)->CreateAudioRecorder(audioEngine,        //引擎接口
                                                   &recorderObject,   //录制对象地址，用于传出对象
                                                   &slDataSource,          //输入配置
                                                   &audioSink,         //输出配置
                                                   1,                  //支持的接口数量
                                                   id,                 //具体的要支持的接口
                                                   req                 //具体的要支持的接口是开放的还是关闭的
    );

    LOGE("创建录制的对象...");

    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    // 实例化这个录制对象
    sLresult = (*recorderObject)->Realize(recorderObject, SL_BOOLEAN_FALSE);
    LOGE("实例化这个录制对象...");
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    // 获取录制接口
    (*recorderObject)->GetInterface(recorderObject, SL_IID_RECORD, &recorderRecorder);
    LOGE("获取录制接口...");


    // 获取Buffer接口
    (*recorderObject)->GetInterface(recorderObject, SL_IID_ANDROIDSIMPLEBUFFERQUEUE,
                                    &recorderBufferQueue);

    LOGE("获取Buffer接口...");

    sLresult = (*recorderBufferQueue)->RegisterCallback(recorderBufferQueue, RecordCallback,this);

    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("Recorder Create OK");
}


char  getWaveHead(char* header,long totalDataLen){

    strcat(header,"RIFF");
    header[4] = (totalDataLen & 0xff);
    header[5] = ((totalDataLen >> 8) & 0xff);
    header[6] = ((totalDataLen >> 16) & 0xff);
    header[7] = ((totalDataLen >> 24) & 0xff);

}


void OpenSLESRecord::startRecord(const char * path,const char * type) {
    pcmFile=fopen(path,"w");
//    strcpy(saveType,type);
    LOGE("fopen %s...",path);
//    LOGE("save type %s...",saveType);
    if (nullptr==recorderRecorder)
        createRecorder();
    recordFinished= false;
    SLresult sLresult = (*recorderBufferQueue)->Enqueue(recorderBufferQueue, recordBuffer,
                                               recorderSize);
    LOGE("Enqueue...");
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("rate %d bit %d channel %d",slDataFormatPcm.samplesPerSec,slDataFormatPcm.bitsPerSample,slDataFormatPcm.numChannels);
    (*recorderRecorder)->SetRecordState(recorderRecorder, SL_RECORDSTATE_RECORDING);
}

void OpenSLESRecord::stopRecord() {
    if (nullptr != recorderRecorder) {
        recordFinished = true;
    }
}

OpenSLESRecord::OpenSLESRecord() {
    recordFinished= false;
    slDataFormatPcm = {
            SL_DATAFORMAT_PCM,                             //输出PCM格式的数据
            (SLuint32) 2,                                  //输出的声道数量
            SL_SAMPLINGRATE_44_1,                          //输出的采样频率，这里是44100Hz
            SL_PCMSAMPLEFORMAT_FIXED_16,                   //输出的采样格式，这里是16bit
            SL_PCMSAMPLEFORMAT_FIXED_16,                   //一般来说，跟随上一个参数
            SL_SPEAKER_FRONT_LEFT |SL_SPEAKER_FRONT_RIGHT,  //双声道配置，如果单声道可以用 SL_SPEAKER_FRONT_CENTER
            SL_BYTEORDER_LITTLEENDIAN                      //PCM数据的大小端排列
    };
    createEngine();

    LOGE("init start...");
}

void OpenSLESRecord::configRecorder(SLDataFormat_PCM dataFormatPcm) {
    slDataFormatPcm=dataFormatPcm;
    delete recorderRecorder;
    createRecorder();
}

SLDataFormat_PCM OpenSLESRecord::getRecorderConfig() {
    return slDataFormatPcm;
}
SLAndroidSimpleBufferQueueItf simpleBufferQueueItf;
void OpenSLESRecord::createPlayer() {
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
    SLDataSource slDataSource = {
        &bufferQueue,
        &slDataFormatPcm,
    };

    SLDataSink slDataSink = {
           &outputMix,
           nullptr
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

    sLresult= (*simpleBufferQueueItf)->RegisterCallback(simpleBufferQueueItf, PlayCallBack,
                                                        this);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("创建播放器成功...");
}

void OpenSLESRecord::play(const char * path) {
    if (!recordFinished)
        recordFinished=true;
    pcmFile=fopen(path,"r");
    if (nullptr==player){
        createPlayer();
    }
    playFinished= false;
    SLresult sLresult=(*simpleBufferQueueItf)->Enqueue(simpleBufferQueueItf, playBuffer,
                                              recorderSize);
    if (SL_RESULT_SUCCESS != sLresult) {
        return;
    }
    LOGE("开始播放 %s",path);
    (*player)->SetPlayState(player,SL_PLAYSTATE_PLAYING);
}

void OpenSLESRecord::stop() {
    playFinished= true;
    (*player)->SetPlayState(player,SL_PLAYSTATE_STOPPED);
}
