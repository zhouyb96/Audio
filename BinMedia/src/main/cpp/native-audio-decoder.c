//
// Created by dell on 2021/12/28.
//

#include <libswresample/swresample.h>
#include "native-audio-decoder.h"
#define LOG_TAG "NativeAudio"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
FILE *f;
AVCodecContext *c= NULL;
AVFrame *decoded_frame = NULL;
int bufferInSize=0;
AVPacket * pkt;
AVFormatContext  * avFormatContext = NULL;
AVRational time_base;
unsigned char decoding=0;
unsigned char is_planar=0;
SwrContext * swrContext;
void deal_packet()
{
    int i, ch;
    int ret;
    char err[128];
    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(c, pkt);
    if (ret < 0) {
        av_make_error_string(err,128,ret);
        LOGE("Error submitting the packet to the decoder%s",err);
        return;
    }
    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {
        ret = avcodec_receive_frame(c, decoded_frame);

        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF){
            return;
        }
        else if (ret < 0) {
            LOGE("Error during decoding");
            exit(1);
        }

        int data_size = av_get_bytes_per_sample(c->sample_fmt);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            LOGE("Failed to calculate data size");
            exit(1);
        }

        int s=data_size*c->channels*decoded_frame->nb_samples;
        uint8_t *sendBuffer=(uint8_t *)malloc(s);

        if (sendBuffer==NULL){
            LOGE("BBB NULL");
        }
        swr_convert(swrContext,&sendBuffer,s,(const uint8_t **)decoded_frame->data,decoded_frame->nb_samples);
//        if (is_planar){
//            for (i = 0; i < decoded_frame->nb_samples; i++){
//                for (ch = 0; ch < c->channels; ch++){
//                    uint8_t *src=decoded_frame->data[ch]+ data_size*i;
//                    if (src!=NULL){
//                        memcpy(sendBuffer+bufferInSize,src,data_size);
//                        bufferInSize+=data_size;
//                    }
//                }
//            }
//        } else{
//            memcpy(sendBuffer,decoded_frame->data[0],s);
//        }
        int64_t current=pkt->pts*(av_q2d(time_base))*1000;
        onProgress(current);
        send_frame(sendBuffer,s);
        free(sendBuffer);
        av_packet_unref(pkt);
    }
}

int decode_ready(AVCodecParameters* parameters){
    const AVCodec *codec = NULL;

    pkt = av_packet_alloc();

    /* find the MPEG audio decoder */
    codec = avcodec_find_decoder(parameters->codec_id);

    if (!codec) {
        LOGE("Codec not found\n");
        return -1;
    }
    LOGE("codec %s",codec->name);
    c = avcodec_alloc_context3(codec);

    if (!c) {
        LOGE("Could not allocate audio codec context\n");
        return -1;
    }

    /* open it */
    int ret=0;
    char errbuf[256];
    avcodec_parameters_to_context(c,parameters);
    if ((ret=avcodec_open2(c, codec, NULL)) < 0) {
        av_strerror(ret, errbuf, 256);
        LOGE("Could not open codec %s\n",errbuf);
        return -1;
    }

    swrContext = swr_alloc_set_opts(NULL, parameters->channel_layout, AV_SAMPLE_FMT_S32, parameters->sample_rate,
                                      c->channel_layout, c->sample_fmt, c->sample_rate,
                                      0, NULL);
    swr_init(swrContext);
   // is_planar=av_sample_fmt_is_planar(c->sample_fmt);
//    LOGE("TIMEBASE %d %d",c->time_base.num,c->time_base.den);
//    c->time_base.den=AV_TIME_BASE ;
//    c->time_base.num=1;
//    LOGE("TIMEBASE %d %d",c->time_base.num,c->time_base.den);
//    time_base=c->time_base;
    return 0;
}

int parse_audio_info(const char * filename){
    int ret = avformat_open_input(&avFormatContext,filename,NULL,NULL);
    LOGE("open_ret%d",ret);
    if (ret<0){
        LOGE( "Could not open input file.");
        return ret;
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
            int channels=audio_stream->codecpar->channels;
            time_base=audio_stream->time_base;
            ret=decode_ready(audio_stream->codecpar);
            int64_t duration=audio_stream->duration;
            int sample_rate=audio_stream->codecpar->sample_rate;
            int bit_format=av_get_bytes_per_sample(audio_stream->codecpar->format)*8;
            int64_t d=duration*av_q2d(time_base);
            //createPlayer(channels,sample_rate,bit_format);
            //LOGE("bit %d %d %d %d %ld",channels,sample_rate,bit_format,audio_stream->time_base.den,d );
            fFmpegAudioInfo.bit_format=bit_format;
            fFmpegAudioInfo.channels=channels;
            fFmpegAudioInfo.bit_rate=sample_rate;
            fFmpegAudioInfo.channel_layout=AV_CH_LAYOUT_STEREO;
            fFmpegAudioInfo.duration=d;
        }
    }

    return ret;
}


int decode()
{
    int ret;
    decoding=1;
    while (decoding) {
        if (!decoded_frame) {
            LOGE("av_frame_alloc");
            if (!(decoded_frame = av_frame_alloc())) {
                LOGE("Could not allocate audio frame");
                return 0;
            }
        }
        ret = av_read_frame(avFormatContext, pkt);
        if (ret < 0)
            break;
        if (pkt->size){
            deal_packet();
        }
    }
    send_frame(NULL,0);
    avformat_free_context(avFormatContext);
    avFormatContext=NULL;
    avcodec_free_context(&c);
    av_frame_free(&decoded_frame);
    av_packet_free(&pkt);
    return 0;
}

void intercept_decode(){
    decoding=0;
}

void* sourceThread(void * p){
    decode();
    pthread_exit(NULL);
}

void seek(long time){
    int64_t t=(int64_t)((time/1000.00)*AV_TIME_BASE);
    LOGE("seek t%lld",t);
    int ret=av_seek_frame(avFormatContext,-1,t,AVSEEK_FLAG_ANY );

}

int decode_audio(const char *file_name){
    pthread_t sourceT;
    LOGE("%s",file_name);
    int ret=parse_audio_info(file_name);
    if (ret<0)
        return ret;
    pthread_create(&sourceT, NULL, (void *(*)(void *)) sourceThread, (void *)NULL);
    return 0;
}