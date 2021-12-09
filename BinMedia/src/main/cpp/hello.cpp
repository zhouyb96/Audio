//
// Created by dell on 2021/11/23.
//
#include "person/hello.h"
#include "person/native-util.h"

void Hello::loge(char *err) {
    JNIEnv* jniEnv=NULL;
    javaVm->GetEnv((void **)jniEnv,JNI_VERSION_1_4);
    javaVm->AttachCurrentThread(&jniEnv,NULL);
    jclass clazz=jniEnv->FindClass("com/example/jnidemo/C2J");
    jmethodID cmethodId=jniEnv->GetMethodID(clazz,"<init>", "()V");
    jmethodID methodId=jniEnv->GetMethodID(clazz,"LogError", "(Ljava/lang/String;)V");
    jobject j=jniEnv->NewObject(clazz,cmethodId);
    jniEnv->CallVoidMethod(j,methodId,jniEnv->NewStringUTF(err));
    delete j;
//    javaVm->DetachCurrentThread();
}
