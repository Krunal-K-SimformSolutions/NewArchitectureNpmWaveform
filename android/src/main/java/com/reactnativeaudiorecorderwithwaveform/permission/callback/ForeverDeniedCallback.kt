package com.reactnativeaudiorecorderwithwaveform.permission.callback

import java.util.ArrayList
import com.reactnativeaudiorecorderwithwaveform.permission.RuntimePermission

class ForeverDeniedCallback(
    private val runtimePermission: RuntimePermission,
    foreverDenied: List<String>?
) {
    private val foreverDenied = ArrayList<String>()

    init {
        if (foreverDenied != null) {
            this.foreverDenied.addAll(foreverDenied)
        }
    }

    fun goToSettings() {
        runtimePermission.goToSettings()
    }

    fun hasForeverDenied(): Boolean {
        return foreverDenied.isNotEmpty()
    }

    fun getForeverDenied(): List<String> {
        return foreverDenied
    }
}
