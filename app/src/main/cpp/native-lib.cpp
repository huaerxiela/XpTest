#include <jni.h>
#include <string>
#include "log.h"
#include "elf.h"


typedef int(*add_func_type)(int x, int y);
extern int(*orig_add)(int x, int y);

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_xptest_MainActivity_stringFromJNI(JNIEnv *env, jclass clazz) {
    std::string hello = "Hello from C++";
    char str[15] = ", yuanrenxue";
    return env->NewStringUTF(strcat(const_cast<char *>(hello.c_str()), str));
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_xptest_HookTest_add1(JNIEnv *env, jclass clazz, jint x, jint y) {
    void* add = ElfUtils::GetModuleOffset("lessontest.so", 0xFFBC);
    auto add_func = reinterpret_cast<add_func_type>(add);
    return add_func(x, y);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_xptest_HookTest_add2(JNIEnv *env, jclass clazz, jint x, jint y) {
    return orig_add(x, y);
}

