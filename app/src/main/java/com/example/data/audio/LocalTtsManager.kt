package com.example.data.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.Locale

class LocalTtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _isArabicSupported = MutableStateFlow(false)
    val isArabicSupported: StateFlow<Boolean> = _isArabicSupported

    private val _isEnglishSupported = MutableStateFlow(false)
    val isEnglishSupported: StateFlow<Boolean> = _isEnglishSupported

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    var currentPitch: Float = 1.0f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var currentSpeed: Float = 1.0f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val arStatus = tts?.isLanguageAvailable(Locale("ar")) ?: TextToSpeech.LANG_NOT_SUPPORTED
            _isArabicSupported.value = arStatus != TextToSpeech.LANG_NOT_SUPPORTED && arStatus != TextToSpeech.LANG_MISSING_DATA

            val enStatus = tts?.isLanguageAvailable(Locale.US) ?: TextToSpeech.LANG_NOT_SUPPORTED
            _isEnglishSupported.value = enStatus != TextToSpeech.LANG_NOT_SUPPORTED && enStatus != TextToSpeech.LANG_MISSING_DATA

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            _isInitialized.value = true
        } else {
            _isInitialized.value = false
        }
    }

    fun speak(text: String, languageCode: String = "ar") {
        val engine = tts ?: return
        if (!_isInitialized.value) return

        val locale = if (languageCode == "en") Locale.US else Locale("ar")
        engine.language = locale
        engine.setPitch(currentPitch)
        engine.setSpeechRate(currentSpeed)

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "anime_preview_tts")
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, "anime_preview_tts")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun synthesizeToFile(text: String, outputFile: File, languageCode: String = "ar", onComplete: (Boolean) -> Unit) {
        val engine = tts
        if (engine == null || !_isInitialized.value) {
            onComplete(false)
            return
        }

        val locale = if (languageCode == "en") Locale.US else Locale("ar")
        engine.language = locale
        engine.setPitch(currentPitch)
        engine.setSpeechRate(currentSpeed)

        val utteranceId = "synth_${System.currentTimeMillis()}"
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(id: String?) {}
            override fun onDone(id: String?) {
                if (id == utteranceId) onComplete(true)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(id: String?) {
                if (id == utteranceId) onComplete(false)
            }
        })

        val result = engine.synthesizeToFile(text, params, outputFile, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            onComplete(false)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
