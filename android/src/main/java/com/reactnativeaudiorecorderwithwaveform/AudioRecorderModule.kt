package com.reactnativeaudiorecorderwithwaveform

import android.Manifest
import android.media.AudioFormat
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.modules.core.PermissionListener
import com.reactnativeaudiorecorderwithwaveform.audio.Recorder
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.config.AudioRecordConfig
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.constants.AudioConstants
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.config.FFmpegConvertConfig
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.model.FFmpegBitRate
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.ffmpeg.model.FFmpegSamplingRate
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.model.DebugState
import com.reactnativeaudiorecorderwithwaveform.permission.RuntimePermission
import com.reactnativeaudiorecorderwithwaveform.permission.callback.PermissionCallback

class AudioRecorderModule(reactContext: ReactApplicationContext) :NativeAudioRecorderSpec(reactContext), PermissionListener {
    private lateinit var recorder: Recorder
    private lateinit var runtimePermission: RuntimePermission
    private var localEventDispatcher: DeviceEventManagerModule.RCTDeviceEventEmitter?

    init {
        localEventDispatcher = null;
//        localEventDispatcher = reactContext
//            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
    }

    override fun getName(): String {
        return NAME
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        DebugState.debug("onRequestPermissionsResult -> $requestCode,$permissions,$grantResults")
        runtimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
        return false
    }

    companion object {
        const val NAME = "AudioRecorder"
    }

    private fun sendEventEmit(eventName: String, data: Any?) {
        localEventDispatcher?.emit(eventName, data)
    }

    private fun checkRecorderInit(): Boolean {
        if(!this::recorder.isInitialized) {
            val params = buildMap<String, Any> {
                "error" to Constant.NOT_INIT_RECORDER
            }
            sendEventEmit("onError", params)
            return false
        }
        return true
    }

    private fun setPermissionResult(it: PermissionCallback, @NonNull filePath: String) {
        DebugState.debug("setPermissionResult -> $filePath")
        with(it) {
            if (hasAccepted()) {
                try {
                    if(checkRecorderInit()) {
                        recorder.setSource(filePath)
                        recorder.startRecording()
                    }
                } catch (e: Exception) {
                    DebugState.error("setPermissionResult", e)
                    val params = buildMap<String, Any> {
                        "error" to e.toString()
                    }
                    sendEventEmit("onError", params)
                }
            }

            if (hasDenied()) {
                AlertDialog.Builder(reactApplicationContext)
                    .setTitle(Constant.REQUIRED_PERMISSION)
                    .setMessage(Constant.REQUIRED_PERMISSION_DENIED)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        askAgain()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if (hasForeverDenied()) {
                AlertDialog.Builder(reactApplicationContext)
                    .setTitle(Constant.REQUIRED_PERMISSION)
                    .setMessage(Constant.REQUIRED_PERMISSION_FOREVERDENIED)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        goToSettings()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun setupRecorder(
        sourceMode: String?,
        isFFmpegMode: Boolean?,
        isDebug: Boolean?,
        subscriptionDurationInMilliseconds: Double?,
        audioSource: Double?,
        audioEncoding: Double?,
        frequency: Double?,
        bitRate: Double?,
        samplingRate: Double?,
        mono: Boolean?
    ) {
        val sourceModeLocal = Utils.getSourceMode(sourceMode)
        val isFFmpegModeLocal = isFFmpegMode ?: false
        val isDebugLocal = isDebug ?: true
        val subscriptionDurationInMillisecondsLocal = subscriptionDurationInMilliseconds?.toLong() ?: AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS

        val audioSourceLocal: Int = Utils.getAudioSource(audioSource?.toInt())
        val audioEncodingLocal: Int = Utils.getAudioEncoding(audioEncoding?.toInt())
        val channelLocal: Int = AudioFormat.CHANNEL_IN_MONO
        val frequencyLocal: Int  = frequency?.toInt() ?: AudioConstants.FREQUENCY_44100
        val config = AudioRecordConfig(audioSourceLocal, audioEncodingLocal, channelLocal, frequencyLocal)

        val bitRateLocal: FFmpegBitRate  = Utils.getFFmpegBitRate(bitRate?.toInt())
        val samplingRateLocal: FFmpegSamplingRate = Utils.getFFmpegSamplingRate(samplingRate?.toInt())
        val monoLocal: Boolean  = mono ?: true
        val convertConfig = FFmpegConvertConfig(bitRateLocal, samplingRateLocal, monoLocal)

        DebugState.state = isDebugLocal
        DebugState.debug("setUpRecorder -> $sourceModeLocal $isFFmpegModeLocal $isDebugLocal $subscriptionDurationInMillisecondsLocal $config $convertConfig")
        try {
            recorder = Recorder.getInstance(reactApplicationContext.applicationContext)
                .init(sourceModeLocal, isFFmpegModeLocal, isDebugLocal, subscriptionDurationInMillisecondsLocal, config, convertConfig).apply {
                    onRawBuffer = {
                        DebugState.debug("onRawBuffer -> audioChunk: ${it.getMaxAmplitude()}")
                        //root.addAmp(it.getMaxAmplitude())
                        val params = buildMap<String, Any> {
                            "maxAmplitude" to it.getMaxAmplitude()
                            "bufferData" to Utils.encodeToString(it.toByteArray())
                            "readCount" to it.getReadCount()
                        }
                        sendEventEmit("onBuffer", params)
                    }
                    onSilentDetected = {
                        DebugState.debug("onSilentDetected -> time: $it")
                        val params = buildMap<String, Any> {
                            "time" to it
                        }
                        sendEventEmit("onSilentDetected", params)
                    }
                    onProgress = { currentTime, maxTime ->
                        DebugState.debug("onProgress -> currentTime: $currentTime, maxTime: $maxTime")
                        val params = buildMap<String, Any> {
                            "currentTime" to currentTime
                            "maxTime" to maxTime
                        }
                        sendEventEmit("onProgress", params)
                    }
                    onFFmpegState = {
                        DebugState.debug("onFFmpegState -> fFmpegState: $it")
                        val params = buildMap<String, Any> {
                            "ffmpegState" to it.name
                        }
                        sendEventEmit("onFFmpegState", params)
                    }
                    onRecordState = {
                        DebugState.debug("onRecordState -> recordState: $it")
                        val params = buildMap<String, Any> {
                            "recordState" to it.name
                        }
                        sendEventEmit("onRecorderState", params)
                    }
                    onFinished = { file, metadata ->
                        DebugState.debug("onFinished -> file: $file, metadata: $metadata")
                        val params = buildMap<String, Any> {
                            "file" to (metadata?.file?.path ?: file?.path ?: "")
                            "duration" to (metadata?.duration ?: 0f)
                        }
                        sendEventEmit("onFinished", params)
                    }
                }
        } catch (e: Exception) {
            DebugState.error("setUpRecorder", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun startRecording(filePath: String?) {
        DebugState.debug("startRecording -> $filePath")
        if (filePath != null) {
            runtimePermission = RuntimePermission(reactApplicationContext.currentActivity, this)
                .request(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .onResponse { setPermissionResult(it, filePath) }
            runtimePermission.ask()
        }
    }

    override fun pauseRecording() {
        DebugState.debug("pauseRecording")
        try {
            if(checkRecorderInit()) {
                recorder.pauseRecording()
            }
        }  catch (e: Exception) {
            DebugState.error("pauseRecording", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun resumeRecording() {
        DebugState.debug("resumeRecording")
        try {
            if(checkRecorderInit()) {
                recorder.resumeRecording()
            }
        }  catch (e: Exception) {
            DebugState.error("resumeRecording", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun stopRecording() {
        DebugState.debug("stopRecording")
        try {
            if(checkRecorderInit()) {
                recorder.stopRecording()
            }
        }  catch (e: Exception) {
            DebugState.error("stopRecording", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun cancelRecording() {
        DebugState.debug("cancelRecording")
        try {
            if(checkRecorderInit()) {
                recorder.cancelRecording()
            }
        }  catch (e: Exception) {
            DebugState.error("cancelRecording", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }
}
