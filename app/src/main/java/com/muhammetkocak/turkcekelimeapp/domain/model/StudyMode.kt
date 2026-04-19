package com.muhammetkocak.turkcekelimeapp.domain.model

enum class StudyMode(val raw: String, val labelTr: String, val emoji: String) {
    Flashcard("FLASHCARD", "Flashcard", "🃏"),
    Quiz("QUIZ", "Çoktan Seçmeli", "🎯"),
    Typing("TYPING", "Yazarak", "⌨️"),
    Listening("LISTENING", "Dinleme", "🎧");

    companion object {
        fun fromRaw(raw: String): StudyMode =
            entries.firstOrNull { it.raw == raw } ?: Flashcard
    }
}
