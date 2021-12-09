#include "person/native-util.h"
#include <string>
#include <ctime>
#include <pthread.h>
#include "android/log.h"
//#include "hello.h"
#include <SLES/OpenSLES.h>
#include <unistd.h>
#define LOG_TAG "native-util"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__) // 定义LOGE类型
jobject listener;
jmethodID methodId;


void*  getTime(void * ){
    LOGE("GETTIME");
    JNIEnv * env=nullptr;

    int attach=javaVm->AttachCurrentThread(&env,nullptr);

    if (attach!=JNI_OK){
        return nullptr;
    }
    for (int i = 0; i < 10; ++i) {
        sleep(1);
        time_t now = time(nullptr);
        tm* t=localtime(&now);

        jstring tString= env->NewStringUTF(asctime(t));
        if (listener==nullptr){
            LOGE("no listener");
            return nullptr;
        } else env->CallVoidMethod(listener,methodId,tString);
    }
    javaVm->DetachCurrentThread();
    pthread_exit(0);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_Util_getTime(JNIEnv* env,jobject /* this */){
    time_t now = time(nullptr);
    tm* t=localtime(&now);
    return env->NewStringUTF(asctime(t));

}

extern "C" JNIEXPORT void JNICALL
Java_com_example_jnidemo_Util_timeContinuous(JNIEnv* env,jobject /* this */){

    auto* handles=new pthread_t [1];
    pthread_create(&handles[1], nullptr,getTime,nullptr);
    delete [] handles;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    JNIEnv* jniEnv= nullptr;
    int result=vm->GetEnv((void **)&jniEnv,JNI_VERSION_1_4);
    if (result!=JNI_OK){
        return -1;
    }
    javaVm = vm;
    return JNI_VERSION_1_4;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_jnidemo_Util_isToday(JNIEnv* env,jobject /* this */,jstring ttime){
    time_t now = time(nullptr);
    tm* t=localtime(&now);
    int year=1900+t->tm_year;
    std::string ts=std::to_string(year) +std::to_string(t->tm_mon+1)+std::to_string(t->tm_mday);
    LOGE("%s",ts.c_str());
    const char * ctime=env->GetStringUTFChars(ttime,0);
    int result=ts.compare(ctime);
    LOGE("result is %d" ,result);
    env->ReleaseStringUTFChars(ttime,ctime);
    return result == 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_jnidemo_Util_addListener(JNIEnv* env,jobject /* this */, jobject message_listener){
//    auto* h=new Hello();
//    h->loge("add listener");
    jclass listenerClass=env->GetObjectClass(message_listener);
    listener=env->NewGlobalRef(message_listener);
    methodId=env->GetMethodID(listenerClass,"onMessage", "(Ljava/lang/String;)V");
//    delete h;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_jnidemo_Util_removeListener(JNIEnv* env,jobject /* this */){
//    auto* h=new Hello();
//    h->loge("remove listener");
    env->DeleteGlobalRef(listener);
    listener= nullptr;
//    delete h;
}



