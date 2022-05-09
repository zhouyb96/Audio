//
// Created by dell on 2021/11/23.
//


#include <pthread.h>
#include <semaphore.h>
#include "sonic.h"
#ifndef JNIDEMO_HELLO_H
#define JNIDEMO_HELLO_H
typedef struct StatusCallBack_ StatusCallBack;
struct StatusCallBack_ {
    void (*Start)();
    void (*Pause)();
    void (*Stop)();
    void (*Error)(int code)
};
static int recorderSize = 4096;
static uint8_t playBuffer[4096];
sonicStream tempoStream_;
void createEngine();
void createPlayer(int channels,long rate,int bitFormat);
void registerCallBack(StatusCallBack* call);
void * play();
void opensl_stop();
void opensl_pause();
void opensl_resume();
void opensl_speech(double speech);
#endif //JNIDEMO_HELLO_H


