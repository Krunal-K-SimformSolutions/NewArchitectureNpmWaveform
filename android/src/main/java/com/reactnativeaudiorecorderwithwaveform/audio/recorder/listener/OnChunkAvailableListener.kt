package com.reactnativeaudiorecorderwithwaveform.audio.recorder.listener

import com.reactnativeaudiorecorderwithwaveform.audio.recorder.chunk.AudioChunk

/**
 * Listener for handling [AudioChunk]
 */
interface OnChunkAvailableListener {

    /**
     * Called when ByteArray has success to came out.
     */
    fun onChunkAvailable(audioChunk: AudioChunk)
}
