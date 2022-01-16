//
// Created by Administrator on 2022/1/14.
//
#include "ffmpeg.h"
#include <jni.h>
#include <android/log.h>
#define LOG_TAG "FFmpegCmd"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
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

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_FFmpegCmd_run(JNIEnv
* env,
jobject thiz, jobjectArray cmd) {

    int argc = (*env)->GetArrayLength(env,cmd);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env,cmd, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env,js, 0);
    }
    exc(argc,argv);
}
static void exit_call(int ret){
    LOGE("exit_call %d", ret);
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* jniEnv= NULL;
    int result=(*vm)->GetEnv((vm),(void **)&jniEnv,JNI_VERSION_1_4);
    if (result!=JNI_OK){
        return -1;
    }

    av_log_set_callback(log_callback_test2);
    register_exit(exit_call);

    return JNI_VERSION_1_4;
}

