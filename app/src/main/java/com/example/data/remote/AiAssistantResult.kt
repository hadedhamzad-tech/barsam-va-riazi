package com.example.data.remote

/**
 * مدل خروجی درخواست به دستیار درسی برسام جهت مدیریت امن و شفاف حالات مختلف
 */
sealed class AiAssistantResult {
    data class Success(val reply: String, val modelUsed: String) : AiAssistantResult()
    data class Disabled(val message: String) : AiAssistantResult()
    data class Offline(val message: String) : AiAssistantResult()
    data class Error(val userMessage: String, val technicalDetail: String? = null) : AiAssistantResult()
}
