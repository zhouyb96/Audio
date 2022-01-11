//
// Created by dell on 2021/12/27.
//

#ifndef JNIDEMO_AUDIO_CACHE_POOL_H
#define JNIDEMO_AUDIO_CACHE_POOL_H
#ifdef __cplusplus
extern "C" {
#endif
    static unsigned decode_finish=0;
    void pool_init();
    void send_frame(unsigned char * buffer,unsigned int size);
    unsigned int read_frame(unsigned char * buffer,unsigned int size);
    void destroy();
    void pool_seek();
void pool_seek_ok();
#ifdef __cplusplus
}
#endif
#endif //JNIDEMO_AUDIO_CACHE_POOL_H
