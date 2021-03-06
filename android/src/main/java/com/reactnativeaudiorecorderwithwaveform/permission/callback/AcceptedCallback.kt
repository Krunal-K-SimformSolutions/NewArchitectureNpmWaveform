package com.reactnativeaudiorecorderwithwaveform.permission.callback

import java.util.ArrayList

class AcceptedCallback(
    accepted: List<String>?
) {
    private val accepted = ArrayList<String>()

    init {
        if (accepted != null) {
            this.accepted.addAll(accepted)
        }
    }

    fun hasAccepted(): Boolean {
        return accepted.isNotEmpty()
    }

    fun getAccepted(): List<String> {
        return accepted
    }
}
