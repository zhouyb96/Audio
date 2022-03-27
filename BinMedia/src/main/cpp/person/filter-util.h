//
// Created by Administrator on 2022/1/18.
//

#ifndef AUDIO_FILTER_UTIL_H
#define AUDIO_FILTER_UTIL_H
struct FilterInfo_{
    char * name;
    char * filter_str;
};

typedef struct FilterInfo_ FilterInfo;

enum FilterType {
    FILTER_ECHO = 0,
    FILTER_FADE,
    FILTER_VOLUME,
    FILTER_VIBRATO,
    FILTER_NB
};

FilterInfo arrayFilter[FILTER_NB]={
        [FILTER_ECHO] = {"aecho","in_gain=0.8:out_gain=0.9:delays=1000:decays=0.3"},//回声,
        [FILTER_FADE] = {"afade","t=%s:ss=%:ns=%ld:st=%d:d=%d:curve=tri"},//渐入渐出
        [FILTER_VOLUME] = {"volume","volume=%f"},//音量
        [FILTER_VIBRATO] = {"vibrato","f=%f:d=%f"}//颤音
};

#endif //AUDIO_FILTER_UTIL_H
