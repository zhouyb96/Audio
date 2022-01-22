//
// Created by dell on 2021/12/3.
//

#include <libavfilter/buffersrc.h>
#include <libavfilter/buffersink.h>
#include <libavcodec/avcodec.h>
#include <android/log.h>
#include "native-audio-filter.h"
#include "native-audio-common.h"
#define LOG_TAG "native-audio-filter"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

//char * getSrcInfo(){
//    const char * srcInfoFormat= "sample_rate=%s:sample_fmt=s16p:channel_layout=stereo";
//    char srcInfo[100];
//    sprintf(srcInfo,srcInfoFormat,inResampleInfo.sample_rate);
//}

int init_filter_graph(){
    int err;
    AVFilterContext* format_ctx;
    const AVFilter* src_filter;
    const AVFilter* format_filter;
    const AVFilter* sink_filter;
    if (filterNodes.thiz==NULL){
        avfilter_graph_free(&av_filter_graph);
        av_filter_graph=NULL;
        return -1;
    } else{
        LOGE("filter %s ",filterNodes.thiz->filter->name);
    }
    src_filter = avfilter_get_by_name("abuffer");
    av_log(av_filter_graph,AV_LOG_ERROR,"TEST AV LOG");
    if (!src_filter) {
        LOGE("Could not find the abuffer filter.\n");
        return -1;
    }
    src_filter_context = avfilter_graph_alloc_filter(av_filter_graph,src_filter,"src");

    const char * srcInfo= "sample_rate=44100:sample_fmt=fltp:channel_layout=stereo";
    if (avfilter_init_str(src_filter_context, srcInfo) <0) {
        LOGE("error init abuffer filter");
        return -1;
    }

    format_filter = avfilter_get_by_name("aformat");
    format_ctx = avfilter_graph_alloc_filter(av_filter_graph,format_filter,"aformat");

    if (!format_ctx) {
        fprintf(stderr, "Could not allocate the aformat instance.\n");
        return AVERROR(ENOMEM);
    }
    //const char * sample_fmts=av_get_sample_fmt_name(AV_SAMPLE_FMT_S16);
    const char * formatInfo= "sample_rates=44100:sample_fmts=fltp:channel_layouts=stereo";
    err = avfilter_init_str(format_ctx, formatInfo);
    if (err <0) {
        LOGE("error init aformat filter");
        return -1;
    }
    sink_filter = avfilter_get_by_name("abuffersink");
    sink_filter_context = avfilter_graph_alloc_filter(av_filter_graph, sink_filter, "sink");
    err = avfilter_init_str(sink_filter_context, NULL);
    if (err<0){
        LOGE("error init sink filter");
        return -1;
    }
    /* Connect the filters;
    * in this simple case the filters just form a linear chain. */
    AVFilterContext* base_filter_ctx=NULL;
    struct FilterNode* node=&filterNodes;
    while (node!=NULL&&node->thiz!=NULL){
        base_filter_ctx=node->thiz;
        LOGE("添加特效%s",base_filter_ctx->filter->name);
        err = avfilter_link(src_filter_context, 0, base_filter_ctx, 0);
        if (err<0){
            LOGE("特效%s 添加失败",base_filter_ctx->filter->name);
            return err;
        }
        node = filterNodes.next;
    }
    if (err >= 0&&base_filter_ctx!=NULL)
        err = avfilter_link(base_filter_ctx, 0, format_ctx, 0);
    if (err >= 0)
        err = avfilter_link(format_ctx, 0, sink_filter_context, 0);
    if (err < 0) {
        fprintf(stderr, "Error connecting filters\n");
        return err;
    }

    /* Configure the graph. */
    err = avfilter_graph_config(av_filter_graph, NULL);
    if (err < 0) {
        av_log(NULL, AV_LOG_ERROR, "Error configuring the filter graph\n");
        return err;
    }
    LOGE("init filter success");
    return 0;
};
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

/**
 *
 * @param name
 * @return 0 有特效但是不存在该名称特效
 *  1 特效存在
 *  -1 没有任何特效
 */
unsigned char filter_exist(const char * name){
    struct FilterNode* node=&filterNodes;
    if (!node->thiz){
        LOGE("没有任何特效");
        return -1;
    }
    do {
        int res=strcmp(node->thiz->name,name);
        if (res>=0)
            return 1;
        node=node->next;
    }while (node!=NULL);
    return 0;
}

struct FilterNode * find_filter(const char * name){
    struct FilterNode* node=&filterNodes;
    do {
        int res=strcmp(node->thiz->name,name);
        if (res>=0)
            return node;
        node=node->next;
    }while (node!=NULL);
    return NULL;
}
AVFilterContext* buildAVFilterContextStr(const char * filter_name,const char * str){
    const AVFilter* base_filter;
    if (av_filter_graph==NULL)
        av_filter_graph = avfilter_graph_alloc();

    base_filter = avfilter_get_by_name(filter_name);
    if (base_filter==0){
        LOGE("error get filter by name:%s",filter_name);
        return NULL;
    }
    AVFilterContext* base_filter_ctx = avfilter_graph_alloc_filter(av_filter_graph,base_filter,filter_name);

    if (base_filter_ctx<0){
        return NULL;
    }
    int ret=avfilter_init_str(base_filter_ctx, str);
    if (ret < 0) {
        LOGE("error init volume filter ");
        return NULL;
    }
    return base_filter_ctx;
}

void add_filter(AVFilterContext* avFilter){
    struct FilterNode new_node={avFilter,NULL,NULL};
    struct FilterNode* node=&filterNodes;
    struct FilterNode * filterNode=find_filter(avFilter->name);
    if (filterNode){
        remove_filter(filterNode->thiz);
    }
    if (!node->thiz){
        filterNodes.thiz=avFilter;
        init_filter_graph();
        return;
    }
    do {
        node=node->next;
    }while (node!=NULL);
    new_node.pre=node;
    node->next=&new_node;
    init_filter_graph();
};

void remove_all_filter(){
    struct FilterNode* node=&filterNodes;
    if (!node->thiz){
        LOGE("没有任何特效");
        return;
    }
    node->thiz=NULL;
    node->next=NULL;
    node->pre=NULL;
    init_filter_graph();
}

void remove_filter(AVFilterContext* avFilter){
    struct FilterNode* node=&filterNodes;
    if (!node->thiz){
        LOGE("没有任何特效");
        return;
    }
    while (node!=NULL){
        if (node->thiz==avFilter){
            if (node->pre!=NULL){
                node->pre->next=node->next;
            }
            if (node->next!=NULL){
                node->next->pre=node->pre;
            }
            node->thiz=NULL;
            LOGE("移除特效%s成功",avFilter->filter->name);
            break;
        }
        node=node->next;
    }
    init_filter_graph();
};

void filter_destroy(){
    avfilter_free(src_filter_context);
    avfilter_free(sink_filter_context);
    avfilter_graph_free(&av_filter_graph);
}

int filterBuffer(uint8_t* buffer,AVFrame* decoded_frame,AVCodecContext *c){
    if (av_filter_graph==NULL)
        return 0;
    int data_size;
    int i,ch ;
    int offset=0;
    int err = av_buffersrc_add_frame(src_filter_context, decoded_frame);
    if (err < 0) {
        LOGE("av_frame_unref");
        av_frame_unref(decoded_frame);
        return 0;
    }
    LOGE("av_buffersink_get_frame");
    av_buffersink_get_frame(sink_filter_context, decoded_frame);
    return offset;
};