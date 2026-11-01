package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator

object SoundHelper {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 45)
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    fun playBeadChime() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) {
            // Ignore
        }
    }

    fun playDecadeChime() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 180)
        } catch (_: Exception) {
            // Ignore
        }
    }
}
