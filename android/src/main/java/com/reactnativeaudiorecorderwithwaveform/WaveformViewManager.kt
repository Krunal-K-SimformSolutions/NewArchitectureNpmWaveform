package com.reactnativeaudiorecorderwithwaveform

import android.graphics.Color
import androidx.annotation.NonNull
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.*
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.EventDispatcher
import com.facebook.react.viewmanagers.WaveformViewManagerDelegate
import com.facebook.react.viewmanagers.WaveformViewManagerInterface
import com.reactnativeaudiorecorderwithwaveform.audio.recorder.model.DebugState
import com.reactnativeaudiorecorderwithwaveform.event.OnSeekChangeEvent
import com.reactnativeaudiorecorderwithwaveform.visualizer.SeekBarOnProgressChanged
import com.reactnativeaudiorecorderwithwaveform.visualizer.Utils
import com.reactnativeaudiorecorderwithwaveform.visualizer.WaveGravity
import com.reactnativeaudiorecorderwithwaveform.visualizer.WaveformSeekBar

@ReactModule(name = WaveformViewManager.NAME)
class WaveformViewManager (val reactApplicationContext: ReactApplicationContext) : SimpleViewManager<WaveformSeekBar>(),
  WaveformViewManagerInterface<WaveformSeekBar> {
  private lateinit var localEventDispatcher: EventDispatcher
  private var mDelegate: ViewManagerDelegate<WaveformSeekBar>? = null

  init {
    mDelegate = WaveformViewManagerDelegate(this)
  }
  /**
   * Return a Waveform which will later hold the View
   */
  override fun createViewInstance(@NonNull reactContext: ThemedReactContext): WaveformSeekBar {
    val waveformSeekBar = WaveformSeekBar(reactContext)
    localEventDispatcher = reactContext.getNativeModule(UIManagerModule::class.java)!!.eventDispatcher
    waveformSeekBar.onProgressChanged = object : SeekBarOnProgressChanged {
      override fun onProgressChanged(waveformSeekBar: WaveformSeekBar, progress: Float, fromUser: Boolean) {
        DebugState.debug("onProgressChanged -> progress: $progress, fromUser: $fromUser")
        if (progress in 0f..100f) {
          //val seekTo = ((progress * player.getTotalDuration()) / 100).toLong();
          dispatchJSEvent(OnSeekChangeEvent(waveformSeekBar.id, progress, fromUser))
        }
      }
    }
    return waveformSeekBar
  }

  override fun getDelegate(): ViewManagerDelegate<WaveformSeekBar>? {
    return mDelegate
  }

  override fun getName(): String {
    return NAME
  }

  override fun createShadowNodeInstance(): LayoutShadowNode {
    return WaveformViewShadowNode()
  }

  override fun createShadowNodeInstance(context: ReactApplicationContext): LayoutShadowNode {
    return WaveformViewShadowNode()
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
    return MapBuilder.builder<String, Any>()
      .put(OnSeekChangeEvent.EVENT_NAME, MapBuilder.of("registrationName", OnSeekChangeEvent.EVENT_NAME))
      .build()
  }

  companion object {
    const val NAME = "WaveformView"
  }

  fun dispatchJSEvent(@NonNull event: Event<*>) {
    if(!this::localEventDispatcher.isInitialized) {
      throw Exception(Constant.NOT_INIT_EVENT_DISPATCHER)
    }
    localEventDispatcher.dispatchEvent(event)
  }

  override fun setVisibleProgress(view: WaveformSeekBar, visibleProgress: Float) {
    view.setVisibleProgress(visibleProgress)
    DebugState.debug("setVisibleProgress -> visibleProgress: $visibleProgress")
  }

  override fun setProgress(view: WaveformSeekBar, progress: Float) {
    view.setProgress(progress)
    DebugState.debug("setProgress -> progress: $progress")
  }

  override fun setMaxProgress(view: WaveformSeekBar, maxProgress: Float) {
    view.setMaxProgress(maxProgress)
    DebugState.debug("setMaxProgress -> maxProgress: $maxProgress")
  }

  override fun setWaveWidth(view: WaveformSeekBar, waveWidth: Float) {
    view.setWaveWidth(Utils.dp(view.context, waveWidth))
    DebugState.debug("setWaveWidth -> waveWidth: $waveWidth")
  }

  override fun setGap(view: WaveformSeekBar, gap: Float) {
    view.setWaveGap(Utils.dp(view.context, gap))
    DebugState.debug("setWaveGap -> gap: $gap")
  }

  override fun setMinHeight(view: WaveformSeekBar, minHeight: Float) {
    view.setWaveMinHeight(Utils.dp(view.context, minHeight))
    DebugState.debug("setWaveMinHeight -> minHeight: $minHeight")
  }

  override fun setRadius(view: WaveformSeekBar, radius: Float) {
    view.setWaveCornerRadius(Utils.dp(view.context, radius))
    DebugState.debug("setWaveCornerRadius -> radius: $radius")
  }

  override fun setGravity(view: WaveformSeekBar, gravity: String?) {
    when(gravity) {
      "top" -> view.setWaveGravity(WaveGravity.TOP)
      "center" -> view.setWaveGravity(WaveGravity.CENTER)
      "bottom" -> view.setWaveGravity(WaveGravity.BOTTOM)
      else -> view.setWaveGravity(WaveGravity.CENTER)
    }
    DebugState.debug("setWaveGravity -> gravity: $gravity")
  }

  override fun setBarBgColor(view: WaveformSeekBar, backgroundColor: String?) {
    if(backgroundColor != null) {
      view.setWaveBackgroundColor(Color.parseColor(backgroundColor))
    } else {
      view.setWaveBackgroundColor(Color.BLACK)
    }
    DebugState.debug("setWaveBackgroundColor -> barBgColor: $backgroundColor")
  }

  override fun setBarPgColor(view: WaveformSeekBar, progressColor: String?) {
    if(progressColor != null) {
      view.setWaveProgressColor(Color.parseColor(progressColor))
    } else {
      view.setWaveProgressColor(Color.RED)
    }
    DebugState.debug("setWaveProgressColor -> barPgColor: $progressColor")
  }

  override fun setForRecorder(view: WaveformSeekBar, isRecorder: Boolean) {
    view.setWaveRecorder(isRecorder)
    DebugState.debug("setWaveRecorder -> isRecorder: $isRecorder")
  }
}
