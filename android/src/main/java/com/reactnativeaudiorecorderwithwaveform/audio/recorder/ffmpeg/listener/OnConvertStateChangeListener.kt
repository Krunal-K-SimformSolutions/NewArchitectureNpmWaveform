package com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.listener

import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.model.FFmpegConvertState

/**
 * Listener for handling state changes
 */
interface OnConvertStateChangeListener {

    /**
     * Call when [FFmpegConvertState] is changed
     */
    fun onState(state: FFmpegConvertState)
}
