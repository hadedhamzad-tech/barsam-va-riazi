package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ماژول مدیریت صدای پاسخ‌های آزمون
 * ویژگی‌ها:
 * ۱. پخش دو صدای تفکیک‌شده برای پاسخ صحیح و غلط با استفاده از ToneGenerator استاندارد اندروید
 * ۲. احترام کامل به تنظیمات درون‌برنامه‌ای (isSoundEnabled)
 * ۳. احترام کامل به وضعیت بی‌صدا و لرزش سیستم (AudioManager.ringerMode)
 * ۴. آزادسازی امن منابع صوتی و مدیریت خطاهای احتمالی
 */
class SoundManager(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        initToneGenerator()
    }

    private fun initToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (e: Exception) {
            Log.e("SoundManager", "Error initializing ToneGenerator: ${e.message}")
            toneGenerator = null
        }
    }

    /**
     * بررسی اینکه آیا در شرایط فعلی دستگاه و برنامه، اجازه پخش صدا وجود دارد یا خیر
     */
    fun canPlayAudio(): Boolean {
        if (!isSoundEnabled) return false

        // بررسی وضعیت سایلنت یا ویبره گوشی
        val ringerMode = audioManager?.ringerMode ?: AudioManager.RINGER_MODE_NORMAL
        return ringerMode == AudioManager.RINGER_MODE_NORMAL
    }

    /**
     * پخش صدای پاسخ صحیح (تن تایید صعودی دلپذیر)
     */
    fun playCorrectSound() {
        if (!canPlayAudio()) return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                if (toneGenerator == null) initToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 200)
            } catch (e: Exception) {
                Log.e("SoundManager", "Failed to play correct tone: ${e.message}")
            }
        }
    }

    /**
     * پخش صدای پاسخ غلط (تن هشدار ملایم)
     */
    fun playWrongSound() {
        if (!canPlayAudio()) return

        CoroutineScope(Dispatchers.Default).launch {
            try {
                if (toneGenerator == null) initToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 220)
            } catch (e: Exception) {
                Log.e("SoundManager", "Failed to play wrong tone: ${e.message}")
            }
        }
    }

    /**
     * آزادسازی منابع صوتی در چرخه عمر
     */
    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SoundManager", "Error releasing ToneGenerator: ${e.message}")
        }
    }
}
