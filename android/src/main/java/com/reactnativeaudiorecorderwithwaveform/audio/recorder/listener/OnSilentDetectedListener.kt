package com.reactnativeaudiorecorderwithwaveform.audio.recorder.listener

/**
 * Listener for handling Silent event of recording while
 * using [com.reactnativeaudiorecorderwithwaveform.audio.recorder.source.NoiseAudioSource]
 */
interface OnSilentDetectedListener {

    /**
     * Invoke when silence measured.
     */
    fun onSilence(silenceTime: Long)
}
