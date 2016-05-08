#include <jni.h>

/*
 * djb2
 * this algorithm (k=33) was first reported by dan bernstein many years ago in comp.lang.c.
 * another version of this algorithm (now favored by bernstein) uses xor: hash(i) = hash(i - 1) * 33 ^ str[i];
 * the magic of number 33 (why it works better than many other constants, prime or not) has never been adequately explained
 *
 * */
unsigned long
hash(unsigned char *str) {
    unsigned long hash = 5381;
    int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

    return hash;
}


JNIEXPORT jlong JNICALL
Java_com_hlandim_gituserssearch_adapter_UsersListAdapter_getDjb2HashUrl(JNIEnv *env,
                                                                        jobject instance,
                                                                        jstring url_) {
    // __android_log_write(ANDROID_LOG_DEBUG, "JNI_LIB", url_);

//    char *mystring = (*env)->GetStringChars(env, url_, JNI_TRUE);

    int vaules = hash(url_);

    // __android_log_write(ANDROID_LOG_DEBUG, "JNI_LIB", vaules);


    return vaules;

}
