package com.example.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * اینترفیس پایش وضعیت اتصال به اینترنت جهت امکان آزمون‌پذیری و کنترل در تست‌ها
 */
fun interface NetworkChecker {
    fun isNetworkAvailable(): Boolean
}

/**
 * پیاده‌سازی رسمی پایش شبکه با استفاده از ConnectivityManager اندروید
 */
class AndroidNetworkChecker(private val context: Context) : NetworkChecker {
    override fun isNetworkAvailable(): Boolean {
        return NetworkUtils.isNetworkAvailable(context)
    }
}

/**
 * ابزار کمکی برای بررسی وضعیت واقعی اتصال به اینترنت
 */
object NetworkUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            false
        }
    }
}
