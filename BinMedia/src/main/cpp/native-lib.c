#include <libavutil/opt.h>
#include <libavutil/channel_layout.h>
#include <libavutil/audio_fifo.h>
#include "person/native-lib.h"
#include "native-audio-filter.h"
#include "native-audio-encoder.h"
int av_codec_id=0;

AVFilterGraph* av_filter_graph;
AVFilterContext* src_filter_context;
AVFilterContext* sink_filter_context;

char errstr[1024];
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
AVAudioFifo *audioFifo =NULL;
static void log_callback_test2(void *ptr, int level, const char *fmt, va_list vl)
{
    va_list vl2;
    char *line = malloc(128 * sizeof(char));
    static int print_prefix = 1;
    va_copy(vl2, vl);
    av_log_format_line(ptr, level, fmt, vl2, line, 128, &print_prefix);
    va_end(vl2);
    line[127] = '\0';
    LOGE("%s", line);
    free(line);
}

void decode()
{
    int i, ch;
    int ret;
    /* send the packet with the compressed data to the decoder */
    //LOGE("async decode");
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


       // bufferInSize=filterBuffer(getPlayQueenBuffer(),decoded_frame,c);
        //return;
//        ret=av_audio_fifo_realloc(audioFifo,decoded_frame->nb_samples);
//        if (ret < 0){
//            LOGE("Failed to av_audio_fifo_realloc");
//        }
        int data_size = av_get_bytes_per_sample(c->sample_fmt);
       // LOGE("per_sample %d %d %d %d %d",data_size,decoded_frame->nb_samples,decoded_frame->channels,(int )decoded_frame->channel_layout,decoded_frame->format);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            LOGE("Failed to calculate data size");
            return;
        }
        LOGE("async decode %d %d",decoded_frame->nb_samples,data_size);
        for (i = 0; i < decoded_frame->nb_samples; i++)
            for (ch = 0; ch < c->channels; ch++){
                uint8_t *src=decoded_frame->data[ch] + data_size*i;
                //av_audio_fifo_write(audioFifo,(void **)&src,data_size);
                fwrite(src, 1, data_size, outfile);
                send2PlayBuffer(src,bufferInSize,data_size);
                bufferInSize+=data_size;
            }

        return;
        av_audio_fifo_write(audioFifo,(void **)decoded_frame->data,decoded_frame->nb_samples);

//
//        for (i = 0; i < decoded_frame->nb_samples; i++)
//            for (ch = 0; ch < c->channels; ch++){
//                uint8_t *src=decoded_frame->data[ch]+ data_size*i;
//                av_audio_fifo_write(audioFifo,(void **)&src,data_size);
//            }

        if (av_audio_fifo_size(audioFifo)>=1024){
            AVFrame *frame = get_av_frame();
            av_audio_fifo_read(audioFifo,(void **)frame->data,1024);
            //LOGE("ready encode %d",av_audio_fifo_size(audioFifo));
            for (i = 0; i < frame->nb_samples; i++)
            for (ch = 0; ch < c->channels; ch++){
                uint8_t *src=frame->data[ch]+ data_size*i;
                //av_audio_fifo_write(audioFifo,(void **)&src,data_size);
                send2PlayBuffer(src,bufferInSize,data_size);
                bufferInSize+=data_size;
                //memcpy(getPlayQueenBuffer()+data_size*i,src,data_size);
            }

            //encode(frame);
        }
        if (av_audio_fifo_size(audioFifo)>=1024){
            AVFrame *frame = get_av_frame();
            av_audio_fifo_read(audioFifo,(void **)frame->data,1024);
            //LOGE("ready encode %d",av_audio_fifo_size(audioFifo));
            for (i = 0; i < frame->nb_samples; i++)
                for (ch = 0; ch < c->channels; ch++){
                    uint8_t *src=frame->data[ch]+ data_size*i;
                    //av_audio_fifo_write(audioFifo,(void **)&src,data_size);
                    send2PlayBuffer(src,bufferInSize,data_size);
                    bufferInSize+=data_size;
                }
        }
    }
}

void getAudioInfo(const char * filename){
    AVFormatContext  * avFormatContext = NULL;
    int open_ret = avformat_open_input(&avFormatContext,filename,NULL,NULL);
    avformat_find_stream_info(avFormatContext, NULL);
    if (open_ret<0){
        LOGE( "Could not open input file.");
    } else {
        int  audio_stream_idx = av_find_best_stream(avFormatContext,
                                                    AVMEDIA_TYPE_AUDIO,
                                                    -1,
                                                    -1,
                                                    NULL,
                                                    0);
        if  (audio_stream_idx >= 0) {
            AVStream *audio_stream = avFormatContext->streams[audio_stream_idx];
            av_codec_id = audio_stream->codecpar->codec_id;
            int channels=audio_stream->codecpar->channels;
            int sample_rate=audio_stream->codecpar->sample_rate;
            int bit_format=av_get_bytes_per_sample(audio_stream->codecpar->format)*8;
            createPlayer(channels,sample_rate,bit_format);
            LOGE("bit %d %d %d",channels,sample_rate,bit_format);
        }
    }
    avformat_free_context(avFormatContext);
}

int decodeAudio(const char * filename,int async)
{
    const AVCodec *codec;
    AVCodecParserContext *parser = NULL;
    int len, ret;
    uint8_t inbuf[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    uint8_t *data;
    size_t   data_size;
    LOGE("av_packet_alloc %s\n",filename);
    pkt = av_packet_alloc();

    /* find the MPEG audio decoder */

    codec = avcodec_find_decoder(AV_CODEC_ID_MP3);

    if (!codec) {
        LOGE("Codec not found\n");
        return 0;
    }

    parser = av_parser_init(codec->id);
    if (!parser) {
        LOGE("Parser not found\n");
        return 0;
    }

    c = avcodec_alloc_context3(codec);

    if (!c) {
        LOGE("Could not allocate audio codec context\n");
        return 0;
    }

    /* open it */

    if (avcodec_open2(c, codec, NULL) < 0) {
        LOGE("Could not open codec\n");
        return 0;
    }
    LOGE("bit avcodec_open2 %d %d",c->sample_fmt,AV_SAMPLE_FMT_S16P);
    f = fopen(filename, "rb");

    if (!f) {
        LOGE("Could not open %s\n", filename);
        exit(1);
    }

    /* decode until eof */
    data      = inbuf;

    data_size = fread(inbuf, 1, AUDIO_INBUF_SIZE, f);
    LOGE("fread all ok %d",data_size);
    LOGE("frame_size %d",c->frame_size);
    while (data_size > 0) {
        if (async){
            pthread_mutex_lock(&mutex);
           // LOGE("async pthread_mutex_lock");
        }


        if (!decoded_frame) {
            LOGE("av_frame_alloc");
            if (!(decoded_frame = av_frame_alloc())) {
                LOGE("Could not allocate audio frame");
                isDecode=0;
                return 0;

            }
        }
        ret = av_parser_parse2(parser, c, &pkt->data, &pkt->size,
                               data, data_size,
                               AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
        if (ret < 0) {
            LOGE("Error while parsing");
            isDecode=0;
            return 0;
        }
        data      += ret;
        data_size -= ret;
        if (pkt->size){
            decode();
            if (bufferInSize>0){
                if (async)
                    pthread_cond_broadcast(&decodeFinish);
                bufferInSize=0;
                if (async)
                    pthread_cond_wait(&playFinish,&mutex);
            }

        }

        if (data_size < AUDIO_REFILL_THRESH) {
            memmove(inbuf, data, data_size);
            data = inbuf;
            len = fread(data + data_size, 1,
                        AUDIO_INBUF_SIZE - data_size, f);
            if (len > 0)
                data_size += len;
        }
        if (async)
            pthread_mutex_unlock(&mutex);
    }
    /* flush the decoder */
    pkt->data = NULL;
    pkt->size = 0;
    decode();
    encode(NULL);
    fclose(f);

    avcodec_free_context(&c);
    av_parser_close(parser);
    av_frame_free(&decoded_frame);
    destroy();
    av_packet_free(&pkt);
    return 0;
}

void* sourceThread(void * p){
    char * fileConfig = (char *)p;
    decodeAudio(fileConfig,1);
    pthread_mutex_destroy( &mutex );
    pthread_cond_destroy( &playFinish );
    pthread_cond_destroy( &decodeFinish );
    pthread_exit(NULL);
}

int init_filter(){
    const char * baseInfo="in_gain=0.8:out_gain=0.9:delays=1000:decays=0.3";
//    base_filter = avfilter_get_by_name("afade");
//    base_filter_ctx = avfilter_graph_alloc_filter(avFilterGraph,base_filter,"afade_in");
//
//    const char * baseInfo="t=out:ss=0:d=15";
    AVFilterContext* base_filter_ctx=buildAVFilterContextStr("aecho",baseInfo);
    add_filter(base_filter_ctx);

    remove_filter(base_filter_ctx);
//    remove_filter(base_filter_ctx);
    AVFilterContext* base_filter_ctx2=buildAVFilterContextStr("aecho",baseInfo);
    add_filter(base_filter_ctx2);
    return 0;
}

void play_music(){
//    pthread_mutex_init(&mutex,NULL);
//    pthread_cond_init(&decodeFinish,NULL);
//    pthread_cond_init(&playFinish,NULL);
//    createEngine();
//    getAudioInfo(hello);
//    play(&mutex,&playFinish,&decodeFinish);
}

//JNIEXPORT jstring JNICALL Java_com_example_jnidemo_MainActivity_stringFromJNI(
//        JNIEnv* env,
//        jobject clazz ,jstring in,jstring out) {
//    const char* hello =  (*env)->GetStringUTFChars(env,in,0);
//    //encoder();
//    pthread_t sourceT;
////
////    pthread_mutex_init(&mutex,NULL);
////    pthread_cond_init(&decodeFinish,NULL);
////    pthread_cond_init(&playFinish,NULL);
//    pthread_mutex_init(&mutex,NULL);
//    pthread_cond_init(&decodeFinish,NULL);
//    pthread_cond_init(&playFinish,NULL);
//    createEngine();
//    getAudioInfo(hello);
//    play(&mutex,&playFinish,&decodeFinish);
////    encoder_init();
//    //audioFifo  = av_audio_fifo_alloc(AV_SAMPLE_FMT_S16, 2, 1024);
//    if (audioFifo==NULL){
//        LOGE("audioFifo failed");
//    }
//    play_music();
//    const char *filename="/storage/emulated/0/Android/data/com.example.jnidemo/cache/ccc.aac";
//    outfile = fopen(filename, "wb");
////    createEngine();
////    play(&mutex,&playFinish,&decodeFinish);
//    av_log_set_callback(log_callback_test2);
//    int ret=init_filter();
//    if (ret<0){
//        return (*env)->NewStringUTF(env,hello);
//    }
//
//    pthread_create(&sourceT, NULL, (void *(*)(void *)) sourceThread, (void *)hello);
//    (*env)->ReleaseStringUTFChars(env,in,hello);
//    return (*env)->NewStringUTF(env,"ccc");
//}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* jniEnv= NULL;
    int result=(*vm)->GetEnv((vm),(void **)&jniEnv,JNI_VERSION_1_4);
    if (result!=JNI_OK){
        return -1;
    }
    javaVm = vm;
    return JNI_VERSION_1_4;
}
