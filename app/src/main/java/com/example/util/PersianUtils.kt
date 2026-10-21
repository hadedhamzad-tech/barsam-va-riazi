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
}
