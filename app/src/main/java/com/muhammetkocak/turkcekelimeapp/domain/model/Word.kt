package com.muhammetkocak.turkcekelimeapp.domain.model

data class Word(
    val id: Long,
    val foreignTerm: String,
    val turkishTerm: String,
    val partOfSpeech: String? = null,
    val exampleForeign: String? = null,
    val exampleTurkish: String? = null,
    val ipa: String? = null,
    val categoryId: Long,
    val isUserCreated: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: Long
) {
    fun prompt(direction: LearningDirection): String = when (direction) {
        LearningDirection.ForeignToTurkish -> foreignTerm
        LearningDirection.TurkishToForeign -> turkishTerm
    }

    fun answer(direction: LearningDirection): String = when (direction) {
        LearningDirection.ForeignToTurkish -> turkishTerm
        LearningDirection.TurkishToForeign -> foreignTerm
    }

    fun promptExample(direction: LearningDirection): String? = when (direction) {
        LearningDirection.ForeignToTurkish -> exampleForeign
        LearningDirection.TurkishToForeign -> exampleTurkish
    }

    fun answerExample(direction: LearningDirection): String? = when (direction) {
        LearningDirection.ForeignToTurkish -> exampleTurkish
        LearningDirection.TurkishToForeign -> exampleForeign
    }
}
