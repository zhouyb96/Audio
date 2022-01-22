#include "person/native-audio-player.h"
#include "native-audio-common.h"
#include "native-audio-decoder.h"
#include "opensl-player.h"
jmethodID progressListenerId;
jobject progressListenerObject;

jobject statusListenerObject;
jmethodID startId;
jmethodID pauseId;
jmethodID stopId;
jmethodID errorId;
StatusCallBack statusCallBack;
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
 Java_cn_zybwz_binmedia_BinPlayer_seek(JNIEnv *env, jobject thiz, jlong time) {
    pool_seek();
    seek((long )time);
    pool_seek_ok();

}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_play(JNIEnv *env, jobject thiz, jstring path) {
    const char* hello =  (*env)->GetStringUTFChars(env,path,0);
    av_log_set_callback(log_callback_test2);
    pool_init();
    int ret=decode_audio(hello);
    if (ret<0){
        LOGE("decoder init fail");
        return;
    }
    LOGE("decoder ready");
    createPlayer(fFmpegAudioInfo.channels,fFmpegAudioInfo.sample_rate,fFmpegAudioInfo.bit_format);
    play();
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_addProgressListener(JNIEnv *env, jobject thiz,
                                                     jobject progress_listener) {
    jclass listenerClass=(*env)->GetObjectClass(env,progress_listener);
    progressListenerId=(*env)->GetMethodID(env,listenerClass,"onProgress", "(J)V");
    progressListenerObject=(*env)->NewGlobalRef(env,progress_listener);
}
JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_addStatusListener(JNIEnv *env, jobject thiz,
                                                   jobject status_listener) {
    jclass listenerClass=(*env)->GetObjectClass(env,status_listener);
    statusListenerObject=(*env)->NewGlobalRef(env,status_listener);
    startId=(*env)->GetMethodID(env,listenerClass,"onStart", "()V");
    pauseId=(*env)->GetMethodID(env,listenerClass,"onPause", "()V");
    stopId=(*env)->GetMethodID(env,listenerClass,"onStop", "()V");
    errorId=(*env)->GetMethodID(env,listenerClass,"onError", "(I)V");
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_pause(JNIEnv *env, jobject thiz) {
    opensl_pause();
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_stop(JNIEnv *env, jobject thiz) {
    opensl_stop();
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_destroy(JNIEnv *env, jobject thiz) {
    destroy();
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_resume(JNIEnv *env, jobject thiz) {
    opensl_resume();
}

JNIEXPORT void JNICALL
Java_cn_zybwz_binmedia_BinPlayer_addFilter(JNIEnv *env, jobject thiz, jint type) {

}

void progressCall(long ms){
    JNIEnv* env= NULL;
    (*javaVm)->AttachCurrentThread(javaVm,&env,NULL);
    int result=(*javaVm)->GetEnv((javaVm),(void **)&env,JNI_VERSION_1_4);

    if (result==JNI_OK){
        (*env)->CallVoidMethod(env,progressListenerObject,progressListenerId,(jlong)ms);
    }

    (*javaVm)->DetachCurrentThread(javaVm);
}

void startCall(){
    JNIEnv* env= NULL;
    (*javaVm)->AttachCurrentThread(javaVm,&env,NULL);
    int result=(*javaVm)->GetEnv((javaVm),(void **)&env,JNI_VERSION_1_4);

    if (result==JNI_OK){

        (*env)->CallVoidMethod(env,statusListenerObject,startId);
    }
    (*javaVm)->DetachCurrentThread(javaVm);
}

void stopCall(){
    JNIEnv* env= NULL;

    (*javaVm)->AttachCurrentThread(javaVm,&env,NULL);
    int result=(*javaVm)->GetEnv((javaVm),(void **)&env,JNI_VERSION_1_4);

    if (result==JNI_OK){
        (*env)->CallVoidMethod(env,statusListenerObject,stopId);
    }

    (*javaVm)->DetachCurrentThread(javaVm);
}

void pauseCall(){
    JNIEnv* env= NULL;

    (*javaVm)->AttachCurrentThread(javaVm,&env,NULL);
    int result=(*javaVm)->GetEnv((javaVm),(void **)&env,JNI_VERSION_1_4);

    if (result==JNI_OK){
        LOGE("pauseCall111");
        (*env)->CallVoidMethod(env,statusListenerObject,pauseId);
    }
    LOGE("pauseCall");

    (*javaVm)->DetachCurrentThread(javaVm);
}

void errorCall(int code){
//    JNIEnv* env= NULL;
//
//    (*javaVm)->AttachCurrentThread(javaVm,&env,NULL);
//
//    int result=(*javaVm)->GetEnv((javaVm),(void **)&env,JNI_VERSION_1_4);
//
//    if (result==JNI_OK){
//        //LOGE("frame_size %lu recorder_ms%lu",frame_size,recorder_ms);
//
//        (*env)->CallVoidMethod(env,progressListenerObject,progressListenerId,(jlong)recorder_ms);
//    }
//
//    (*javaVm)->DetachCurrentThread(javaVm);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* jniEnv= NULL;
    int result=(*vm)->GetEnv((vm),(void **)&jniEnv,JNI_VERSION_1_4);
    if (result!=JNI_OK){
        return -1;
    }

    statusCallBack.Start=startCall;
    statusCallBack.Pause=pauseCall;
    statusCallBack.Stop=stopCall;
    registerCallBack(&statusCallBack);
    onProgress=progressCall;
     javaVm = vm;
    createEngine();
    return JNI_VERSION_1_4;
}
