package com.reactnativeaudiorecorderwithwaveform.audio

import android.content.Context
import androidx.annotation.NonNull
import com.reactnativeaudiorecorderwithwaveform.Constant
import com.reactnativeaudiorecorderwithwaveform.audio.player.extensions.SingletonHolder
import com.reactnativeaudiorecorderwithwaveform.audio.player.extensions.recordFile
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.AudioRecorder
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.chunk.AudioChunk
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.config.AudioRecordConfig
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.constants.AudioConstants
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.FFmpegAudioRecorder
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.FFmpegRecordFinder
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.config.FFmpegConvertConfig
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.model.FFmpegConvertState
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.model.RecordMetadata
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.model.RecordState
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.source.AudioSource
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.source.AutomaticGainAudioSource
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.source.DefaultAudioSource
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.source.NoiseAudioSource
import java.io.File

class Recorder private constructor(context: Context) {
    private val appContext = context
    private var withFFmpegMode = false
    private var withDebug = false
    private var withCancel = false
    private var withRefreshTimerMillis: Long = AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS

    private lateinit var recorder: AudioRecorder
    private lateinit var config: AudioRecordConfig
    private lateinit var convertConfig: FFmpegConvertConfig
    private lateinit var source: AudioSource
    private var sourceFilePath: File? = null

    var onRawBuffer: ((AudioChunk) -> Unit)? = null
    var onSilentDetected: ((Long) -> Unit)? = null
    var onProgress: ((Long, Long) -> Unit)? = null
    var onRecordState: ((RecordState) -> Unit)? = null
    var onFFmpegState: ((FFmpegConvertState) -> Unit)? = null
    var onFinished: ((File?, RecordMetadata?) -> Unit)? = null

    fun init(
      @NonNull sourceMode: String,
      @NonNull isFFmpegMode: Boolean = false,
      @NonNull isDebug: Boolean = false,
      @NonNull subscriptionDurationInMilliseconds: Long = AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS,
      @NonNull config: AudioRecordConfig,
      @NonNull convertConfig: FFmpegConvertConfig
    ): Recorder {
      this.withDebug = isDebug
      this.withFFmpegMode = isFFmpegMode
      this.withRefreshTimerMillis = subscriptionDurationInMilliseconds
      this.recorder = AudioRecorder()

      this.config = config
      this.convertConfig = convertConfig

      this.source = when (sourceMode) {
        "noise" -> NoiseAudioSource(this.config)
        "auto" -> AutomaticGainAudioSource(this.config)
        else -> DefaultAudioSource(this.config)
      }
      return this
    }

    fun setSource(@NonNull filePath: String) {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = false
      sourceFilePath = appContext.recordFile(filePath)
      recorder.create(FFmpegRecordFinder::class.java) {
        this.ffmpegMode = withFFmpegMode
        this.destFile = sourceFilePath
        this.recordConfig = config
        this.audioSource = source
        this.refreshTimerMillis = withRefreshTimerMillis
        this.chunkAvailableCallback = onRawBuffer
        this.silentDetectedCallback = onSilentDetected
        this.timerCountCallback = onProgress
        this.debugMode = withDebug
      }

      if (withFFmpegMode) {
        val ffmpegRecorder: FFmpegAudioRecorder =
          recorder.getAudioRecorder() as? FFmpegAudioRecorder ?: return
        ffmpegRecorder.setContext(appContext)
        ffmpegRecorder.setConvertConfig(convertConfig)
        ffmpegRecorder.setOnConvertStateChangeListener {
          onFFmpegState?.invoke(it)
          if (it == FFmpegConvertState.SUCCESS && !withCancel) {
              finishRecording()
          }
        }
      }

      recorder.setOnRecordStateChangeListener { onRecordState?.invoke(it) }
    }

    fun startRecording() {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = false
      if(!recorder.isRecording())
        recorder.startRecording()
    }

    fun stopRecording() {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = false
      if(recorder.isRecording()) {
        recorder.stopRecording()
        if (!withFFmpegMode) {
          finishRecording()
        }
      }
    }

    fun resumeRecording() {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = false
      if(!recorder.isRecording())
        recorder.resumeRecording()
    }

    fun pauseRecording() {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = false
      if(recorder.isRecording())
        recorder.pauseRecording()
    }

    fun cancelRecording() {
      if(!this::recorder.isInitialized)
        throw Exception(Constant.NOT_INIT_RECORDER)

      withCancel = true
      if(recorder.isRecording()) {
        recorder.stopRecording()
      }
    }

    private fun finishRecording() {
        val recordMetadata = recorder.retrieveMetadata(sourceFilePath ?: File(""))
        onFinished?.invoke(sourceFilePath, recordMetadata)
    }

    companion object : SingletonHolder<Recorder, Context>(::Recorder)
}
