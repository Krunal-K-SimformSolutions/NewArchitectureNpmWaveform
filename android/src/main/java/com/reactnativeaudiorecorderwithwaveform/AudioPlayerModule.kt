package com.reactnativeaudiorecorderwithwaveform

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.modules.core.PermissionListener
import com.reactnativeaudiorecorderwithwaveform.audio.Player
import com.reactnativeaudiorecorderwithwaveform.audio.player.model.AmpsState
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.constants.AudioConstants
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.model.DebugState
import com.reactnativeaudiorecorderwithwaveform.permission.RuntimePermission
import com.reactnativeaudiorecorderwithwaveform.permission.callback.PermissionCallback

class AudioPlayerModule(reactContext: ReactApplicationContext) :NativeAudioPlayerSpec(reactContext), PermissionListener {
    private lateinit var player: Player
    private lateinit var runtimePermission: RuntimePermission
    private var localEventDispatcher: DeviceEventManagerModule.RCTDeviceEventEmitter?

    init {
        localEventDispatcher = reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
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
        const val NAME = "AudioPlayer"
    }

    private fun sendEventEmit(eventName: String, data: Any?) {
        localEventDispatcher?.emit(eventName, data)
    }

    private fun checkPlayerInit(): Boolean {
        if(!this::player.isInitialized) {
            val params = buildMap<String, Any> {
                "error" to Constant.NOT_INIT_PLAYER
            }
            sendEventEmit("onError", params)
            return false
        }
        return true
    }

    private fun setPermissionResult(it: PermissionCallback) {
        DebugState.debug("setPermissionResult ->")
        with(it) {
            if (hasAccepted()) {
                try {
                    if(checkPlayerInit()) {
                        player.startPlaying()
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

    override fun setupPlayer(
        isDebug: Boolean?,
        subscriptionDurationInMilliseconds: Double?
    ) {
        val isDebugLocal = isDebug ?: true
        val subscriptionDurationInMillisecondsLocal = subscriptionDurationInMilliseconds?.toLong() ?: AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS

        DebugState.state = isDebugLocal
        DebugState.debug("setUpPlayer -> $isDebugLocal $subscriptionDurationInMillisecondsLocal")
        try {
            player = Player.getInstance(reactApplicationContext.applicationContext).init(isDebugLocal, subscriptionDurationInMillisecondsLocal).apply {
                onProgress = { time, _ ->
                    DebugState.debug("onProgress -> time: $time")
                    val params = buildMap<String, Any> {
                        "currentTime" to time
                        "maxTime" to player.getTotalDuration()
                    }
                    sendEventEmit("onProgress", params)
                    //root.setProgress((time.toFloat() / player.getTotalDuration()) * 100)
                }
                onPlayState = {
                    DebugState.debug("onPlayState -> playState: $it")
                    val params = buildMap<String, Any> {
                        "playState" to it.name
                    }
                    sendEventEmit("onPlayerState", params)
                }
            }
        } catch (e: Exception) {
            DebugState.error("setUpPlayer", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun setupSource(filePath: String?, isAmplitudaMode: Boolean?) {
        val isMode = isAmplitudaMode ?: false
        DebugState.debug("setSource -> $filePath")
        try {
            if(checkPlayerInit() && filePath != null) {
                player.setSource(filePath)
                val params = buildMap<String, Any> {
                    "ampsState" to AmpsState.START.name
                }
                sendEventEmit("onAmpsState", params)
                player.loadFileAmps(isMode).subscribe({ amps ->
                    DebugState.debug("loadFileAmps -> amps: $amps")
                    //root.setWaveForm(amps)
                    var params1 = buildMap<String, Any> {
                        "loadAmps" to Utils.toWritableArray(amps)
                    }
                    sendEventEmit("onLoadAmps", params1)
                    params1 = buildMap<String, Any> {
                        "ampsState" to AmpsState.SUCCESS.name
                    }
                    sendEventEmit("onAmpsState", params1)
                }, {
                    DebugState.error("loadFileAmps", it)
                    var params1 = buildMap<String, Any> {
                        "error" to it.toString()
                    }
                    sendEventEmit("onError", params1)
                    params1 = buildMap<String, Any> {
                        "ampsState" to AmpsState.ERROR.name
                    }
                    sendEventEmit("onAmpsState", params1)
                }, {
                    val params1 = buildMap<String, Any> {
                        "ampsState" to AmpsState.COMPLETED.name
                    }
                    sendEventEmit("onAmpsState", params1)
                })
            }
        } catch (e: Exception) {
            DebugState.error("setSource", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun startPlaying() {
        DebugState.debug("startPlaying")
        runtimePermission = RuntimePermission(reactApplicationContext.currentActivity, this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .onResponse { setPermissionResult(it) }
        runtimePermission.ask()
    }

    override fun pausePlaying() {
        DebugState.debug("pausePlaying")
        try {
            if(checkPlayerInit()) {
                player.pausePlaying()
            }
        } catch (e: Exception) {
            DebugState.error("pausePlaying", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun resumePlaying() {
        DebugState.debug("resumePlaying")
        try {
            if(checkPlayerInit()) {
                player.resumePlaying()
            }
        } catch (e: Exception) {
            DebugState.error("resumePlaying", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun stopPlaying() {
        DebugState.debug("stopPlaying")
        try {
            if(checkPlayerInit()) {
                player.stopPlaying()
            }
        } catch (e: Exception) {
            DebugState.error("stopPlaying", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }

    override fun setPlaybackSpeed(speed: Double) {
        DebugState.debug("setPlaybackSpeed -> playbackSpeed: $speed")
        try {
            if(checkPlayerInit()) {
                player.playbackSpeed(speed.toFloat())
            }
        } catch (e: Exception) {
            DebugState.error("setPlaybackSpeed", e)
            val params = buildMap<String, Any> {
                "error" to e.toString()
            }
            sendEventEmit("onError", params)
        }
    }
}
