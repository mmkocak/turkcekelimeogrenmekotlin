package com.muhammetkocak.turkcekelimeapp.domain.model

enum class CardMastery(val raw: String) {
    New("NEW"),
    Learning("LEARNING"),
    Review("REVIEW"),
    Mastered("MASTERED");

    companion object {
        fun fromRaw(raw: String): CardMastery =
            entries.firstOrNull { it.raw == raw } ?: New
    }
}
