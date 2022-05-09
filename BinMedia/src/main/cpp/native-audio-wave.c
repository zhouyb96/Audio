//
// Created by dell on 2022/4/19.
//
#include "person/native-audio-wave.h"
AVFormatContext  * avFormatContext = NULL;
AVCodecContext *c= NULL;

AVFrame *decoded_frame = NULL;
AVPacket * pkt;

int decoding=0;
int bufferInSize=0;
int is_planar=0;
int sample_rate=44100;
int channels=2;
int bit_format=16;
char *pcm_buffer;
int wave_sample=10;
unsigned char * wave_buffer;
int wave_index=0;
long duration=0L;
int byte_size=0;
int wave_count=0;
enum AVSampleFormat asf=AV_SAMPLE_FMT_NONE;
#include <android/log.h>
#define LOG_TAG "AudioWave"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型

int open_coder(AVCodecParameters* parameters){
    const AVCodec *codec = NULL;

    /* find the MPEG audio decoder */
    codec = avcodec_find_decoder(parameters->codec_id);

    if (!codec) {
        LOGE("Codec not found\n");
        return -3;
    }

    c=avcodec_alloc_context3(codec);

    if (!c){
        //初始化codec context失败
        LOGE("初始化codec context失败");
        return -4;
    }
    int ret=0;
    char errbuf[256];
    avcodec_parameters_to_context(c,parameters);
    if ((ret=avcodec_open2(c, codec, NULL)) < 0) {
        av_strerror(ret, errbuf, 256);
        LOGE("Could not open codec %s\n",errbuf);
        return -5;
    }

    return 0;
}

int parse_info(const char * file_name){
    int res=avformat_open_input(&avFormatContext,file_name,NULL,NULL);
    if (res<0){
        //无法打开文件
        LOGE("无法打开文件");
        return -1;
    }
    avformat_find_stream_info(avFormatContext, NULL);
    int  audio_stream_idx = av_find_best_stream(avFormatContext,
                                                AVMEDIA_TYPE_AUDIO,
                                                -1,
                                                -1,
                                                NULL,
                                                0);
    if (audio_stream_idx<0){
        //无法找到合适解析的帧
        LOGE("无法找到合适解析的帧");
        return -2;
    }

    //准备开始获取音频信息了
    AVStream *audio_stream = avFormatContext->streams[audio_stream_idx];
    open_coder(audio_stream->codecpar);
    duration=audio_stream->duration*av_q2d(audio_stream->time_base)*1000;
    channels=audio_stream->codecpar->channels;
    sample_rate=audio_stream->codecpar->sample_rate;//采样率
    asf=audio_stream->codecpar->format;
    bit_format=av_get_bytes_per_sample(audio_stream->codecpar->format)*8;
    is_planar=av_sample_fmt_is_planar(audio_stream->codecpar->format);//判断是不是
    LOGE("sample_rate %d  bit_format %d channels %d is_planar %d  %ld %d %d",sample_rate,bit_format,channels,is_planar,duration,wave_count,byte_size);
    return 0;
}


/**
 * 获取所有振幅之平均值 计算db (振幅最大值 2^16-1 = 65535 最大值是 96.32db)
 * 16 bit == 2字节 == short int
 * 无符号16bit：96.32=20*lg(65535);
 *
 * @param pcmdata 转换成char类型，才可以按字节操作
 * @param size pcmdata的大小
 * @return
 */
int getPcmDB(const unsigned char *pcmdata, size_t size,int bitFormat) {

    int db = 0;
    float value = 0;
    double sum = 0;
    for(int i = 0; i < size; i += bitFormat/8)
    {
        memcpy(&value, pcmdata+i, bitFormat/8); //获取2个字节的大小（值）
        sum += abs(value); //绝对值求和
    }
    sum = sum / (size / (bitFormat/8)); //求平均值（2个字节表示一个振幅，所以振幅个数为：size/2个）
    if(sum > 0)
    {
        db = (int)(20.0*log10(sum));
    }
    LOGE("DB  %d",db);
    return db;
}
void getPcmDB16(const unsigned char *pcmdata, size_t size) {
    int db = 0;
    short int value = 0;
    double sum = 0;
    for(int i = 0; i < size; i += bit_format/8)
    {
        memcpy(&value, pcmdata+i, bit_format/8); //获取2个字节的大小（值）
        sum += abs(value); //绝对值求和
    }
    sum = sum / (size / (bit_format/8)); //求平均值（2个字节表示一个振幅，所以振幅个数为：size/2个）
    if(sum > 0)
    {
        db = (int)(20.0*log10(sum));
    }
    memcpy(wave_buffer+wave_index,(char*)&db,1);
    wave_index++;
}

void getPcmDB24(const unsigned char *pcmdata, size_t size) {
    int db = 0;
    short int value = 0;
    double sum = 0;
    for(int i = 0; i < size; i += 3)
    {
        value=(pcmdata[i])|((pcmdata[i+1]<<8));
        sum += abs(value); //绝对值求和
    }
    sum = sum / (size/3) ;
    if(sum > 0)
    {
        db = (int)(20.0*log10(sum));
    }
    memcpy(wave_buffer+wave_index,(char*)&db,1);
    wave_index++;
}

void getPcmDB32(const unsigned char *pcmdata, size_t size) {
    int db = 0;
    int64_t value = 0;
    double sum = 0;
    for(int i = 0; i < size; i += bit_format/8)
    {
        memcpy(&value, pcmdata+i, bit_format/8); //获取2个字节的大小（值）
        sum += (abs(value)/65535); //绝对值求和
    }
    sum = sum / (size / (bit_format/8)); //求平均值（2个字节表示一个振幅，所以振幅个数为：size/2个）
    if(sum > 0)
    {
        db = (int)(20.0*log10(sum));
    }
    memcpy(wave_buffer+wave_index,(char*)&db,1);
    wave_index++;
}

void getPcmDBFloat(const unsigned char *pcmdata, size_t size) {
    int db = 0;
    float value = 0;
    double sum = 0;
    for(int i = 0; i < size; i += bit_format/8)
    {
        memcpy(&value, pcmdata+i, bit_format/8); //获取2个字节的大小（值）
        sum += abs(value*65535); //绝对值求和
    }
    sum = sum / (size / (bit_format/8)); //求平均值（2个字节表示一个振幅，所以振幅个数为：size/2个）
    if(sum > 0)
    {
        db = (int)(20.0*log10(sum));
    }
    memcpy(wave_buffer+wave_index,(char*)&db,1);
    wave_index++;
}

void getPCMDB(const unsigned char *pcmdata, size_t size){
    switch (bit_format) {
        case 16:
            getPcmDB16(pcmdata,size);
            break;
        case 24:
            getPcmDB24(pcmdata,size);
            break;
        case 32:
            if (asf==AV_SAMPLE_FMT_S32P||asf==AV_SAMPLE_FMT_S32)
                getPcmDB32(pcmdata,size);
            else if (asf==AV_SAMPLE_FMT_FLTP||asf==AV_SAMPLE_FMT_FLT)
                getPcmDBFloat(pcmdata,size);
            break;
        default:
            return;
    }
}


void self_deal_packet(AVCodecContext *ctx,AVFrame *frame,AVPacket * packet)
{
    int i, ch;
    int ret;
    char err[128];
    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(ctx, packet);
    if (ret < 0) {
        av_make_error_string(err,128,ret);
        LOGE("Error submitting the packet to the decoder%s",err);
        return;
    }
    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {
        ret = avcodec_receive_frame(ctx, frame);

        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF){
            return;
        }
        else if (ret < 0) {
            LOGE("Error during decoding");
            exit(1);
        }

        int data_size = av_get_bytes_per_sample(ctx->sample_fmt);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            LOGE("Failed to calculate data size");
            exit(1);
        }

        int s=data_size*ctx->channels*frame->nb_samples;

        if (is_planar){
            for (i = 0; i < decoded_frame->nb_samples; i++){
                for (ch = 0; ch < c->channels; ch++){
                    uint8_t *src=decoded_frame->data[ch]+ data_size*i;
                    if (src!=NULL){
                        if (bufferInSize+data_size>=byte_size){
                            int a=bufferInSize-byte_size;
                            getPCMDB(pcm_buffer,bufferInSize);
                            memset(pcm_buffer,0,byte_size);
                            bufferInSize=0;
                        } else{
                            memcpy(pcm_buffer+bufferInSize,src,data_size);
                            bufferInSize+=data_size;
                        }
                    }
                }
            }
        } else{
            if (bufferInSize+s>byte_size){
                memcpy(pcm_buffer+bufferInSize,decoded_frame->data[0],(byte_size-bufferInSize));
                getPCMDB(pcm_buffer,bufferInSize);
                memset(pcm_buffer,0,byte_size);
                memcpy(pcm_buffer,decoded_frame->data[0]+(byte_size-bufferInSize),s-(byte_size-bufferInSize));

                bufferInSize=(s-(byte_size-bufferInSize));
            } else {
                memcpy(pcm_buffer+bufferInSize,decoded_frame->data[0],s);
                bufferInSize+=s;
            }
        }
    }
}

int decode()
{
    int ret;
    decoding=1;

    while (decoding) {
        if (!decoded_frame) {
            if (!(decoded_frame = av_frame_alloc())) {
                LOGE("Could not allocate audio frame");
                return 0;
            }
            pkt = av_packet_alloc();
        }
        ret = av_read_frame(avFormatContext, pkt);
        if (ret < 0)
            break;
        if (pkt->size){
            self_deal_packet(c,decoded_frame,pkt);
            av_packet_unref(pkt);
        }
    }
    avcodec_free_context(&c);
    av_frame_free(&decoded_frame);
    av_packet_free(&pkt);
    avformat_close_input(&avFormatContext);
    avformat_free_context(avFormatContext);
    avFormatContext=NULL;
    return 0;
}

unsigned char* decode_wave(const char *file_name,int sample){
    wave_sample=sample;
    bufferInSize=0;
    decoding=0;
    bit_format=0;
    asf=AV_SAMPLE_FMT_NONE;
    int res=parse_info(file_name);
    if (res<0){
        LOGE("parse info failed");
        return NULL;
    }
    wave_index=0;
    byte_size=sample_rate*channels*bit_format/8/wave_sample;
    pcm_buffer=malloc(byte_size);
    wave_count=((duration+1000)/1000)*10;
    if (wave_buffer){
        free(wave_buffer);
        wave_buffer=NULL;
    }
    wave_buffer=(unsigned char *)malloc(wave_count);
    decode();
    free(pcm_buffer);
    LOGE("FINISH hello world %d %d %d",wave_count,wave_index,byte_size);
    return wave_buffer;
}

