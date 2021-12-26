//
// Created by Administrator on 2021/12/17.
//
#include <libavutil/opt.h>
#include <libavutil/channel_layout.h>
#include <libavutil/audio_fifo.h>
#include <libavcodec/avcodec.h>
#include "native-audio-decoder.h"

AVAudioFifo *audioFifo =NULL;
unsigned char async=1;

void decode_frame()
{
    int ret;
    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(c, pkt);
    if (ret < 0) {
        LOGE("Error submitting the packet to the decoder");
        return;
    }
    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {
        ret = avcodec_receive_frame(c, decoded_frame);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF)
            return;
        else if (ret < 0) {
            LOGE("Error during decoding");
            return;
        }
        /*
         * 给别人处理帧数据
         */
    }
}


int getAudioInfo(const char * filename){
    AVFormatContext  * avFormatContext = NULL;
    int open_ret = avformat_open_input(&avFormatContext,filename,NULL,NULL);
    if (open_ret<0){
        LOGE( "Could not open input file.");
        return -1;
    } else {
        avformat_find_stream_info(avFormatContext, NULL);
        int  audio_stream_idx = av_find_best_stream(avFormatContext,
                                                    AVMEDIA_TYPE_AUDIO,
                                                    -1,
                                                    -1,
                                                    NULL,
                                                    0);
        if  (audio_stream_idx >= 0) {
            AVStream *audio_stream = avFormatContext->streams[audio_stream_idx];
            audioInfo.decode_id = audio_stream->codecpar->codec_id;
            audioInfo.channels=audio_stream->codecpar->channels;
            audioInfo.channel_layout=audio_stream->codecpar->channel_layout;
            audioInfo.sample_rate=audio_stream->codecpar->sample_rate;
            audioInfo.bit_rate=av_get_bytes_per_sample(audio_stream->codecpar->format)*8;
            return 0;
        }
    }
    avformat_free_context(avFormatContext);
    return -2;
}

int decode_prepare(const AVCodec * codec,AVCodecParserContext *parser){
    pkt = av_packet_alloc();
    codec = avcodec_find_decoder(audioInfo.decode_id);
    if (!codec) {
        LOGE("Codec not found\n");
        return -1;
    }
    parser = av_parser_init(codec->id);
    if (!parser) {
        LOGE("Parser not found\n");
        return -1;
    }

    c = avcodec_alloc_context3(codec);

    if (!c) {
        LOGE("Could not allocate audio codec context\n");
        return -1;
    }

    /* open it */

    if (avcodec_open2(c, codec, NULL) < 0) {
        LOGE("Could not open codec\n");
        return -1;
    }

    if (!decoded_frame) {
        LOGE("av_frame_alloc");
        if (!(decoded_frame = av_frame_alloc())) {
            LOGE("Could not allocate audio frame");
            return 0;
        }
    }
}

void decode_finish(AVCodecParserContext *parser){
    pkt->data = NULL;
    pkt->size = 0;
    decode_frame();
    fclose(f);
    avcodec_free_context(&c);
    av_parser_close(parser);
    av_frame_free(&decoded_frame);
    av_packet_free(&pkt);
}

int decode_audio()
{
    const AVCodec *codec;
    AVCodecParserContext *parser = NULL;
    int len, ret;
    uint8_t in_buff[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    uint8_t *data;
    size_t   data_size;

    ret=decode_prepare(codec,parser);

    LOGE("bit avcodec_open2 %d %d",c->sample_fmt,AV_SAMPLE_FMT_S16P);

    if (ret<0)
        return ret;
    /* decode until eof */
    data      = in_buff;
    data_size = fread(in_buff, 1, AUDIO_INBUF_SIZE, f);
    while (data_size > 0) {
        if (async){
            pthread_mutex_lock(&mutex);
        }

        ret = av_parser_parse2(parser, c, &pkt->data, &pkt->size,
                               data, data_size,
                               AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
        if (ret < 0) {
            LOGE("Error while parsing");
            break;
        }
        data      += ret;
        data_size -= ret;
        if (pkt->size){
            decode_frame();
            if (bufferInSize>0){
                if (async)
                    pthread_cond_broadcast(&decodeFinish);
                bufferInSize=0;
                if (async)
                    pthread_cond_wait(&playFinish,&mutex);
            }
        }

        if (data_size < AUDIO_REFILL_THRESH) {
            memmove(in_buff, data, data_size);
            data = in_buff;
            len = fread(data + data_size, 1,
                        AUDIO_INBUF_SIZE - data_size, f);
            if (len > 0)
                data_size += len;
        }
        if (async)
            pthread_mutex_unlock(&mutex);
    }
    /* flush the decoder */
    decode_finish(parser);
    return ret;
}

void* sourceThread(void * p){
    char * filename = (char *)p;
    f = fopen(filename, "rb");
    if (!f) {
        LOGE("Could not open %s\n", filename);
        return 0;
    }
    decode_audio();
//    pthread_mutex_destroy( &mutex );
//    pthread_cond_destroy( &playFinish );
//    pthread_cond_destroy( &decodeFinish );
    pthread_exit(NULL);
}

void decode_get_bytes(char * bytes){
    decode_buffer=bytes;
    resultType=1;
}

void decode_get_frame(AVFrame *frame){
    decoded_frame=frame;
    resultType=0;
}

int decode_file(const char * file_path){
    pthread_t sourceT;
    pthread_mutex_init(&mutex,NULL);
    pthread_cond_init(&decodeFinish,NULL);
    pthread_cond_init(&playFinish,NULL);
    int res=getAudioInfo(file_path);
    if (res<0){
        LOGE("audioFifo failed");
        return res;
    }
    pthread_create(&sourceT, NULL, (void *(*)(void *)) sourceThread, (void *)file_path);
}

