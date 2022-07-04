package com.reactnativeaudiorecorderwithwaveform.audio.recorder.recorder

import com.reactnativeaudiorecorderwithwaveform.audio.recorder.writer.RecordWriter
import java.io.File

/**
 * [AudioRecorder] for record audio and save in pcm file
 */
class PcmAudioRecorder(file: File, recordWriter: RecordWriter) : DefaultAudioRecorder(file, recordWriter)
