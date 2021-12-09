//
// Created by dell on 2021/11/23.
//


#include <pthread.h>
#include <semaphore.h>
#ifndef JNIDEMO_HELLO_H
#define JNIDEMO_HELLO_H

static unsigned recorderSize = 4608*2;
static uint8_t playBuffer[4608*2];
void createEngine();
uint8_t *getPlayQueenBuffer();
void createPlayer(int channels,long rate,int bitFormat);
short * play(pthread_mutex_t* m,pthread_cond_t* p,pthread_cond_t* d);
void stop();
void send2PlayBuffer(uint8_t * buffer,int offset,unsigned int size);
#endif //JNIDEMO_HELLO_H


