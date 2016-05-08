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
    unsigned long hash = 10;
    int c;

    while (c = *str++)
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

    return hash;
}


JNIEXPORT jlong JNICALL
Java_com_hlandim_gituserssearch_web_GitHubApi_getDjb2HashUrl(JNIEnv *env, jobject instance,
                                                             jstring url_) {

    const char *key = (*env)->GetStringUTFChars(env, url_, 0);


    return hash(key);

}