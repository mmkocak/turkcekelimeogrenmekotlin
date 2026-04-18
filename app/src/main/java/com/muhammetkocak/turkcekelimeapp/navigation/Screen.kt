package com.muhammetkocak.turkcekelimeapp.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class Library(val categoryId: Long? = null) : Screen

    @Serializable
    data class WordDetail(val wordId: Long) : Screen

    @Serializable
    data class AddEditWord(val wordId: Long? = null) : Screen

    @Serializable
    data class Study(
        val mode: String,
        val direction: String,
        val categoryId: Long? = null
    ) : Screen

    @Serializable
    data object Stats : Screen

    @Serializable
    data object Settings : Screen
}
