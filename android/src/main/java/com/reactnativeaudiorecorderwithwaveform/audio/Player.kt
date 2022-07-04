package com.reactnativeaudiorecorderwithwaveform.audio

import android.content.Context
import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import com.reactnativeaudiorecorderwithwaveform.Constant
import com.reactnativeaudiorecorderwithwaveform.audio.player.AudioPlayer
import com.reactnativeaudiorecorderwithwaveform.audio.player.extensions.SingletonHolder
import com.reactnativeaudiorecorderwithwaveform.audio.player.extensions.recordFile
import com.reactnativeaudiorecorderwithwaveform.audio.player.model.PlayState
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.constants.AudioConstants
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.lang.Exception

class Player private constructor(context: Context) {
    private val appContext = context
    private lateinit var player: AudioPlayer
    private var sourceFilePath: File? = null
    private var withDebug = false
    private var withRefreshTimerMillis: Long = AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS

    var onProgress: ((Long, Boolean) -> Unit)? = null
    var onPlayState: ((PlayState) -> Unit)? = null

    fun init(isDebug: Boolean = false, subscriptionDurationInMilliseconds: Long = AudioConstants.SUBSCRIPTION_DURATION_IN_MILLISECONDS): Player {
        this.withDebug = isDebug
        this.withRefreshTimerMillis = subscriptionDurationInMilliseconds
        this.player = AudioPlayer()

        return this
    }

    fun setSource(@NonNull filePath: String) {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

        sourceFilePath = appContext.recordFile(filePath)

        player.create(appContext) {
            this.sourceFile = sourceFilePath
            this.refreshTimerMillis = withRefreshTimerMillis
            this.timerCountCallback = onProgress
            this.debugMode = withDebug
        }

        player.setOnPlayStateChangeListener { onPlayState?.invoke(it) }
    }

    fun startPlaying() {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

        if(!player.isPlaying())
          player.startPlaying()
    }

    fun stopPlaying() {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

      if(player.isPlaying())
        player.stopPlaying()
    }

    fun resumePlaying() {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

      if(!player.isPlaying())
        player.resumePlaying()
    }

    fun pausePlaying() {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

      if(player.isPlaying())
        player.pausePlaying()
    }

    fun getTotalDuration(): Long {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

        return player.duration()
    }

    fun seekTo(@NonNull time: Long) {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

        player.seekTo(time)
    }

    fun playbackSpeed(@FloatRange(from = 0.0, fromInclusive = false) speed: Float) {
      if(!this::player.isInitialized)
        throw Exception(Constant.NOT_INIT_PLAYER)

      player.playbackSpeed(speed)
    }

    fun loadFileAmps(isAmplitudaMode: Boolean): Observable<List<Int>> {
        if(!this::player.isInitialized)
            throw Exception(Constant.NOT_INIT_PLAYER)

        return player.loadFileAmps(appContext, isAmplitudaMode)
    }

    companion object : SingletonHolder<Player, Context>(::Player)
}
