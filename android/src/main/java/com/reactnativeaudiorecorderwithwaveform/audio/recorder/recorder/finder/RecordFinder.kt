package com.reactnativeaudiorecorderwithwaveform.audio.recorder.recorder.finder

import com.reactnativeaudiorecorderwithwaveform.audio.recorder.recorder.AudioRecorder
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.writer.RecordWriter
import java.io.File

/**
 * find proper [AudioRecorder] class which condition
 */
interface RecordFinder {

    /**
     * find [AudioRecorder] with given [extension]
     */
    fun find(extension: String, file: File, writer: RecordWriter): AudioRecorder
}
