package com.muhammetkocak.turkcekelimeapp.domain.model

/**
 * Yön: kartın ön yüzünde hangi dilin olduğu.
 *
 * - [ForeignToTurkish]: Ön yüz İngilizce (yabancı), arka yüz Türkçe.
 *   Türk kullanıcı İngilizce öğreniyor senaryosu.
 * - [TurkishToForeign]: Ön yüz Türkçe, arka yüz İngilizce.
 *   Yabancı kullanıcı Türkçe öğreniyor senaryosu.
 */
enum class LearningDirection(val raw: String) {
    ForeignToTurkish("FOREIGN_TO_TURKISH"),
    TurkishToForeign("TURKISH_TO_FOREIGN");

    companion object {
        fun fromRaw(raw: String): LearningDirection =
            entries.firstOrNull { it.raw == raw } ?: ForeignToTurkish
    }
}
