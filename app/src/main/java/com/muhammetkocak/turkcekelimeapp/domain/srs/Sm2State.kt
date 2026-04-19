package com.muhammetkocak.turkcekelimeapp.domain.srs

import com.muhammetkocak.turkcekelimeapp.domain.model.CardMastery

/**
 * SM-2 algoritmasının tek bir kart için durumu.
 *
 * @param easiness Easiness Factor — minimum 1.3, tipik başlangıç 2.5.
 * @param intervalDays Sonraki tekrara kadar geçecek gün sayısı.
 * @param repetition Üst üste başarılı tekrar sayısı (Again sıfırlar).
 * @param dueAt Bir sonraki tekrar zamanı (epoch millis, UTC).
 * @param lastReviewedAt En son tekrar anı (epoch millis) — henüz yapılmadıysa null.
 * @param mastery Kullanıcıya gösterilen özet durum.
 */
data class Sm2State(
    val easiness: Double = 2.5,
    val intervalDays: Int = 0,
    val repetition: Int = 0,
    val dueAt: Long,
    val lastReviewedAt: Long? = null,
    val mastery: CardMastery = CardMastery.New
)
