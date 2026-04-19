package com.muhammetkocak.turkcekelimeapp.domain.model

import com.muhammetkocak.turkcekelimeapp.domain.srs.Sm2State

/**
 * SRS algoritması tarafından seçilip ekrana sunulmak üzere hazırlanmış kart.
 */
data class StudyCard(
    val word: Word,
    val direction: LearningDirection,
    val state: Sm2State
) {
    val mastery: CardMastery get() = state.mastery
}
