#include <cstdio>
#include <cstring>
#include <jni.h>
#include <dlfcn.h>
#include <string>
#include "lsp.h"
#include "log.h"
#include "elf.h"


static HookFunType hook_func = nullptr;


#define HOOK_SYMBOL(func) hook_func(func, (void*) new_##func, (void**) &orig_##func)
#define HOOK_DEF(ret, func, ...) \
  ret (*orig_##func)(__VA_ARGS__); \
  ret new_##func(__VA_ARGS__)


HOOK_DEF(int, target_fun){
    return orig_target_fun() + 666;
}

HOOK_DEF(FILE *, myfopen, const char *filename, const char *mode){
    if (strstr(filename, "file.txt")){
        const char *fake_filename = "/data/data/com.hexl.lessontest/fake_file.txt";
        return orig_myfopen(fake_filename, mode);
    }
    return orig_myfopen(filename, mode);
}

HOOK_DEF(jclass, FindClass, JNIEnv *env, const char *name){
    LOGI("FindClass: %s", name);
    return orig_FindClass(env, name);
}

HOOK_DEF(int, add, int x, int y){
    LOGI("add(x,y)=add(%d,%d)", x, y);
    return orig_add(x, y);
}

void on_library_loaded(const char *name, void *handle) {
    // hooks on `.so`
    if (strstr(name, "lessontest.so")) {
        void *target_fun = dlsym(handle, "target_fun");
        HOOK_SYMBOL(target_fun);

        void* add = ElfUtils::GetModuleOffset("lessontest.so", 0xFFBC);
        HOOK_SYMBOL(add);
    }
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    auto FindClass = (void *)env->functions->FindClass;
    HOOK_SYMBOL(FindClass);
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    // system hooks
    auto myfopen = (void*) fopen;
    HOOK_SYMBOL(myfopen);
    return on_library_loaded;
}
