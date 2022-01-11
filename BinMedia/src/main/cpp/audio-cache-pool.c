//
// Created by dell on 2021/12/27.
//
#include <string.h>
#include <malloc.h>
#include <pthread.h>
#include <android/log.h>
#include "audio-cache-pool.h"
const unsigned int capacity= 4096 * 10;
static char pool[capacity];

unsigned int free_size = capacity;
pthread_mutex_t mutex ;

pthread_cond_t  playFinish ;
pthread_cond_t  decodeFinish ;
#define LOG_TAG "NativeAudio"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
char is_seek=0;
void pool_init(){
    pthread_mutex_init(&mutex,NULL);
    pthread_cond_init(&decodeFinish,NULL);
    pthread_cond_init(&playFinish,NULL);
}
void move_head(unsigned int size){
    char * buck=(char*)malloc(capacity);
    memcpy(buck,pool,capacity);
    memset(pool,0,capacity);
    memcpy(pool,buck+size,capacity-size);
    free(buck);
}

void send_frame(unsigned char * buffer,unsigned int size){
    if (is_seek){
        is_seek=0;
        return;
    }
    pthread_mutex_lock(&mutex);
    if (size==0){
        pthread_cond_broadcast(&decodeFinish);
        pthread_mutex_unlock(&mutex);
        decode_finish=1;
        return;
    }


    decode_finish=0;
   // LOGE("send_frame 222 %d is_seek %d %d  %d  %d",size,is_seek,free_size,capacity,(capacity-free_size));
    if (size<0||size>free_size||is_seek){
        is_seek=0;
        free_size=capacity;
        pthread_mutex_unlock(&mutex);
        return;
    }
    if (free_size>capacity)
        free_size=capacity;
    memcpy(pool+(capacity-free_size),buffer,size);
    free_size-=size;
    if (free_size<=0.2*capacity){
        pthread_cond_wait(&playFinish,&mutex);
    }
    if (free_size<=0.8*capacity){
        pthread_cond_broadcast(&decodeFinish);
    }
    pthread_mutex_unlock(&mutex);
}

unsigned int read_frame(unsigned char * buffer,unsigned int size){
    free_size+=size;
    if (decode_finish!=0){
        if (free_size>capacity){
            memset(pool,0,capacity);
            free_size=capacity;
            return 0;
        }

        if (free_size<=capacity){
            memcpy(buffer,pool,size);
            move_head(size);
            return size;
        }
    }else {
        if (free_size>=capacity*0.8){
            pthread_cond_broadcast(&playFinish);
            pthread_cond_wait(&decodeFinish,&mutex);
        }
        memcpy(buffer,pool,size);
        move_head(size);
        return size;
    }

}

void pool_seek(){
    pthread_mutex_lock(&mutex);
    //memset(pool,0,capacity);
    pthread_cond_broadcast(&decodeFinish);
    free_size=capacity;
    is_seek=1;

}
void pool_seek_ok(){
    pthread_mutex_unlock(&mutex);
}

void destroy(){
    pthread_mutex_destroy( &mutex );
    pthread_cond_destroy( &playFinish );
    pthread_cond_destroy( &decodeFinish );
}

