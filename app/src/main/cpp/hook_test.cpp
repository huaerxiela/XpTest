#include <cstdio>
#include <cstring>
#include <jni.h>
#include <dlfcn.h>
#include <string>
#include "lsp.h"
#include "log.h"


static HookFunType hook_func = nullptr;


int (*backup)();

int fake() {
    return backup() + 666;
}

FILE *(*backup_fopen)(const char *filename, const char *mode);

FILE *fake_fopen(const char *filename, const char *mode) {
    if (strstr(filename, "file.txt")){
        return nullptr;
    }
    return backup_fopen(filename, mode);
}

jclass (*backup_FindClass)(JNIEnv *env, const char *name);
jclass fake_FindClass(JNIEnv *env, const char *name)
{
    LOGI("FindClass: %s", name);
    return backup_FindClass(env, name);
}

void on_library_loaded(const char *name, void *handle) {
    // hooks on `.so`
    if (strstr(name, "lessontest.so")) {
        void *target = dlsym(handle, "target_fun");
        hook_func(target, (void *) fake, (void **) &backup);
    }
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    hook_func((void *)env->functions->FindClass, (void *)fake_FindClass, (void **)&backup_FindClass);
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    // system hooks
    hook_func((void*) fopen, (void*) fake_fopen, (void**) &backup_fopen);
    return on_library_loaded;
}