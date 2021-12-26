//
// Created by Administrator on 2021/12/26.
//

#include <string.h>
#include <malloc.h>
#include <pthread.h>

const unsigned int capacity= 4096 * 10;
static char pool[capacity];

unsigned int free_size = capacity;
pthread_mutex_t mutex ;
pthread_cond_t  playFinish ;
pthread_cond_t  decodeFinish ;

void init(){
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

void send_frame(char * buffer,unsigned int size){
    pthread_mutex_lock(&mutex);
    memcpy(pool+(capacity-free_size),buffer,size);
    free_size-=size;
    if (free_size<=capacity*0.2){
        //lock
        pthread_cond_broadcast(&decodeFinish);
        pthread_cond_wait(&playFinish,&mutex);
    }
    pthread_mutex_unlock(&mutex);
}

void read_frame(char * buffer,unsigned int size){
    memcpy(buffer,pool,size);
    free_size+=size;
    move_head(size);
    if (free_size>=capacity*0.8){
        //unlock
        pthread_cond_broadcast(&playFinish);
        pthread_cond_wait(&decodeFinish,&mutex);
    }
}

void destroy(){
    pthread_mutex_destroy( &mutex );
    pthread_cond_destroy( &playFinish );
    pthread_cond_destroy( &decodeFinish );
}

