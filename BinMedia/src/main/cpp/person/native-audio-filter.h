#include <stdlib.h>
#include "libavfilter/avfilter.h"
#ifndef JNIDEMO_NATIVE_AUDIO_FILTER_H
#define JNIDEMO_NATIVE_AUDIO_FILTER_H


struct FilterNode{
    AVFilterContext* thiz;
    struct FilterNode* next;
    struct FilterNode* pre;
};

AVFilterGraph* av_filter_graph=NULL;
AVFilterContext* src_filter_context=NULL;
AVFilterContext* sink_filter_context=NULL;
struct FilterNode filterNodes={NULL,NULL,NULL};
int init_filter_graph();

void add_filter(AVFilterContext* avFilter);

void remove_filter(AVFilterContext* avFilter);

int filterBuffer(uint8_t* buffer,AVFrame* decoded_frame,AVCodecContext *c);

AVFilterContext* buildAVFilterContextStr(const char * filter_name,const char * str);


#endif //JNIDEMO_NATIVE_AUDIO_FILTER_H
