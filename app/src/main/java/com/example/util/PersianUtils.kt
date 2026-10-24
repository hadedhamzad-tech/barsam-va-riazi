package com.example.util

import java.util.Calendar

object PersianUtils {

    /**
     * تبدیل ارقام انگلیسی به ارقام فارسی برای نمایش در رابط کاربری
     */
    fun toPersianDigits(input: Any?): String {
        if (input == null) return ""
        val str = input.toString()
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * قالب‌بندی زمان به صورت دقیقه و ثانیه فارسی (مثلا: ۱۲:۴۵)
     */
    fun formatDuration(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "${toPersianDigits("%02d".format(m))}:${toPersianDigits("%02d".format(s))}"
    }

    /**
     * قالب‌بندی استاندارد درصد با ۱ رقم اعشار گردشده، ممیز فارسی «٫» و علامت درصد «٪» در انتها (مثل ۷۳٫۳٪)
     */
    fun formatPercentage(percentage: Double?): String {
        if (percentage == null || percentage.isNaN() || percentage.isInfinite()) {
            return "۰٫۰٪"
        }
        val rounded = Math.round(percentage * 10.0) / 10.0
        val formatted = String.format(java.util.Locale.US, "%.1f", rounded)
        val withPersianComma = formatted.replace('.', '٫')
        return "${toPersianDigits(withPersianComma)}٪"
    }

    /**
     * پیام خوشامدگویی متناسب با ساعت محلی دستگاه از طرف «برسام»
     */
    fun getBarsamGreeting(): Pair<String, String> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> Pair(
                "صبح بخیر، قهرمان ریاضی!",
                "آماده‌ای با تمرین و تست‌های هدفمند، پایه‌های ریاضی کنکورت رو محکم کنی؟"
            )
            in 12..15 -> Pair(
                "ظهر بخیر، پرتلاش!",
                "وقت یک مرور سریع فرمول‌ها یا حل چند تست تمرکزی برای تثبیت آموخته‌هاست."
            )
            in 16..19 -> Pair(
                "عصر بخیر، آینده‌ساز کنکور!",
                "استمرار در تست‌زنی و یادداشت اشتباهات، مسیر قطعی رتبه برتر شدنه."
            )
            in 20..23 -> Pair(
                "شب بخیر، ریاضیدان پرتلاش!",
                "بیا عملکرد امروزت رو ارزیابی کنیم و برای مباحث فردا برنامه‌ریزی کنیم."
            )
            else -> Pair(
                "بامداد بخیر، همراه کوشا!",
                "ذهن آرام و خواب باکیفیت به اندازه حل تست در یادگیری ریاضی مهمه."
            )
        }
    }

    // ==========================================
    // تفکیک و ایزولاسیون متن‌های ریاضی در محیط RTL (Unicode Isolate LRI/PDI)
    // ==========================================
    const val LRI = '\u2066' // Left-to-Right Isolate
    const val PDI = '\u2069' // Pop Directional Isolate

    private const val MATH_SYMBOLS = "+-−*·×/÷=≠<>≤≥^√π∞→|±∓²³⁴ⁿ⁻⁺⁰¹₂₃₄°~Δαβθλμσω"
    private const val MATH_CLOSING_DELIMITERS = ")]}|'\""

    fun isPersian(c: Char): Boolean {
        val code = c.code
        return (code in 0x0600..0x06FF) || // Arabic/Persian/Digits
               (code in 0x0750..0x077F) || // Arabic Supplement
               (code in 0x08A0..0x08FF) || // Arabic Extended-A
               (code in 0xFB50..0xFDFF) || // Arabic Presentation Forms-A
               (code in 0xFE70..0xFEFF)    // Arabic Presentation Forms-B
    }

    fun isCoreMathChar(c: Char): Boolean {
        return (c in 'a'..'z') || (c in 'A'..'Z') || (c in '0'..'9') || (c in MATH_SYMBOLS)
    }

    fun isMathAllowedChar(c: Char): Boolean {
        return isCoreMathChar(c) || (c in "()[]{}(),.:;!_'\" \t\n")
    }

    fun canStartMath(c: Char): Boolean {
        return isCoreMathChar(c) || (c in "([{|")
    }

    fun canEndMath(c: Char): Boolean {
        return (c in 'a'..'z') || (c in 'A'..'Z') || (c in '0'..'9') ||
                (c in MATH_CLOSING_DELIMITERS) || (c in "²³⁴ⁿ⁻⁺⁰¹₂₃₄°π∞Δαβθλμσω")
    }

    /**
     * متن را به بخش‌های فارسی و ریاضی/لاتین تقسیم می‌کند و هر بخش ریاضی را با نویسه‌های
     * LRI (U+2066) در ابتدا و PDI (U+2069) در انتها محصور می‌کند تا در رابط کاربری راست‌چین
     * چیدمان فرمول‌ها، بازه‌ها و عبارات جبری دقیقاً چپ‌به‌راست و خوانا بماند.
     */
    fun formatMathText(input: String?): String {
        if (input.isNullOrEmpty()) return input ?: ""
        // حذف ایزوله‌های قبلی در صورت فراخوانی مجدد جهت ایدمپوتنت بودن
        val text = input.replace(LRI.toString(), "").replace(PDI.toString(), "")
        val sb = StringBuilder()
        var i = 0
        val n = text.length

        while (i < n) {
            val c = text[i]
            if (canStartMath(c) && !isPersian(c)) {
                var j = i
                var lastValidEnd = -1
                var hasCoreMath = false

                while (j < n) {
                    val current = text[j]
                    if (isPersian(current)) {
                        break
                    }
                    if (!isMathAllowedChar(current)) {
                        break
                    }
                    if (isCoreMathChar(current)) {
                        hasCoreMath = true
                    }
                    if (canEndMath(current) && hasCoreMath) {
                        lastValidEnd = j
                    }
                    j++
                }

                if (lastValidEnd >= i && hasCoreMath) {
                    val mathSegment = text.substring(i, lastValidEnd + 1)
                    sb.append(LRI).append(mathSegment).append(PDI)
                    i = lastValidEnd + 1
                    continue
                }
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }
}
