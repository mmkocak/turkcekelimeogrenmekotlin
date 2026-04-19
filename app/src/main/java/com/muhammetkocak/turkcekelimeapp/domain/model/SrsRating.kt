package com.muhammetkocak.turkcekelimeapp.domain.model

/**
 * Kullanıcının kart cevabına verdiği kalite puanı. SM-2 algoritması için.
 */
enum class SrsRating(val quality: Int, val labelTr: String) {
    Again(1, "Tekrar"),
    Hard(2, "Zor"),
    Good(4, "İyi"),
    Easy(5, "Kolay")
}
