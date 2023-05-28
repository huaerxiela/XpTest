#include <jni.h>
#include <string>
#include "log.h"


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_xptest_MainActivity_stringFromJNI(JNIEnv *env, jclass clazz) {
    std::string hello = "Hello from C++";
    char str[15] = ", yuanrenxue";
    return env->NewStringUTF(strcat(const_cast<char *>(hello.c_str()), str));
}