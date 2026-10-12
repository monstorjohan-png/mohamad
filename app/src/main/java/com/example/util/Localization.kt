package com.example.util

object Localization {
    fun get(isArabic: Boolean, ar: String, en: String): String {
        return if (isArabic) ar else en
    }
}
