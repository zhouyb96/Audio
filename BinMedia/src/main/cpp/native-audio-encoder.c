#include <libavcodec/avcodec.h>
#include <libavutil/channel_layout.h>
#include "libavcodec/codec.h"

//
// Created by dell on 2021/12/4.
//
#include <android/log.h>
#define LOG_TAG "native-audio-encoder"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
/* check that a given sample format is supported by the encoder */
static AVCodecContext *ctx= NULL;
static FILE *output;
static AVPacket *pkt;
int check_sample_fmt(const AVCodec *codec, enum AVSampleFormat sample_fmt)
{
    const enum AVSampleFormat *p = codec->sample_fmts;

    while (*p != AV_SAMPLE_FMT_NONE) {
        if (*p == sample_fmt)
            return 1;
        p++;
    }
    return 0;
}

/* just pick the highest supported samplerate */
static int select_sample_rate(const AVCodec *codec)
{
    const int *p;
    int best_samplerate = 0;

    if (!codec->supported_samplerates)
        return 44100;

    p = codec->supported_samplerates;
    while (*p) {
        if (!best_samplerate || abs(44100 - *p) < abs(44100 - best_samplerate))
            best_samplerate = *p;
        p++;
    }
    return best_samplerate;
}

/* select layout with the highest channel count */
static int select_channel_layout(const AVCodec *codec)
{
    const uint64_t *p;
    uint64_t best_ch_layout = 0;
    int best_nb_channels   = 0;

    if (!codec->channel_layouts)
        return AV_CH_LAYOUT_STEREO;

    p = codec->channel_layouts;
    while (*p) {
        int nb_channels = av_get_channel_layout_nb_channels(*p);

        if (nb_channels > best_nb_channels) {
            best_ch_layout    = *p;
            best_nb_channels = nb_channels;
        }
        p++;
    }
    return best_ch_layout;
}

void encode(AVFrame *frame)
{
    int ret;
    /* send the frame for encoding */
    ret = avcodec_send_frame(ctx, frame);
    if (ret < 0) {
        LOGE("Error sending to encoder %d  %d\n",frame->nb_samples,ctx->frame_size);
        return;
    }

    /* read all the available output packets (in general there may be any
     * number of them */
    while (ret >= 0) {
        ret = avcodec_receive_packet(ctx, pkt);
        if (ret == AVERROR(EAGAIN)){
//            char es[1024];
//            av_strerror(ret,es,1024);
//            LOGE( "Error AVERROR %s %d\n",es,ctx);
            return;
        }
        fwrite(pkt->data, 1, pkt->size, output);
        //av_packet_unref(pkt);
    }
    LOGE(" fwrite SUCCESS");
}


void destroy(){
    fclose(output);
    av_packet_free(&pkt);
    avcodec_free_context(&ctx);
}

AVFrame* get_av_frame(){
    AVFrame *frame = av_frame_alloc();
    frame->nb_samples     = ctx->frame_size;
    frame->format         = ctx->sample_fmt;
    frame->channel_layout = ctx->channel_layout;
    frame->channels = ctx->channels;
    av_frame_get_buffer(frame, 0);
    //LOGE("%d %d %d %d ",frame->nb_samples,frame->format,(int )frame->channel_layout, frame->channels );
    return frame;
}

int encoder_init()
{
    const char *filename="/storage/emulated/0/Android/data/com.example.jnidemo/cache/ccc.aac";
    const AVCodec *codec;

    codec = avcodec_find_encoder_by_name("libfdk_aac");
    if (!codec) {
        LOGE( "Codec not found\n");
        return -1;
    }
    LOGE( "Codec id is %d\n",codec->id);
    ctx = avcodec_alloc_context3(codec);
    if (!ctx) {
        LOGE( "Could not allocate audio codec context\n");
        return -1;
    }

    /* put sample parameters */
    ctx->bit_rate = 44100*2*16;

    /* check that the encoder supports s16 pcm input */
    ctx->sample_fmt = AV_SAMPLE_FMT_S16;
    if (!check_sample_fmt(codec, ctx->sample_fmt)) {
        LOGE( "Encoder does not support sample format %s",
                av_get_sample_fmt_name(ctx->sample_fmt));
        return -1;
    }

    /* select other audio parameters supported by the encoder */
    ctx->sample_rate    = 44100;
    ctx->channel_layout = AV_CH_LAYOUT_STEREO;
    ctx->channels       = 2;

    /* open it */
    if (avcodec_open2(ctx, codec, NULL) < 0) {
        LOGE("Could not open codec\n");
        return -1;
    }

    output = fopen(filename, "wb");
    if (!output) {
        LOGE("Could not open %s\n", filename);
        return -1;
    }
    LOGE("addrr%d",ctx);
    /* packet for holding encoded output */
    pkt = av_packet_alloc();
    if (!pkt) {
        LOGE("could not allocate the packet\n");
        return -1;
    }

    return 0;
}



