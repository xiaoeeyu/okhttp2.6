#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <android/log.h>
#include <unistd.h>
#include <netdb.h>
#include <pthread.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_okhttp_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    // TODO
    return env->NewStringUTF("hello");
}
