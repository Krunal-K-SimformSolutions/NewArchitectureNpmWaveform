#include <jni.h>
#include "react-native-audio-recorder-with-waveform.h"
#include "log.h"

extern "C" JNIEXPORT jdouble JNICALL
Java_com_reactnativeaudiorecorderwithwaveform_AudioRecorderModule_nativeMultiply(JNIEnv *env, jclass type, jdouble num1, jdouble num2)
{
    LOGI("Calling nativeMultiply");
    return audiorecorderwithwaveform::multiply(num1, num2);
}
