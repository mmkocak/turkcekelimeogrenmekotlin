package com.muhammetkocak.turkcekelimeapp.domain.model

data class Category(
    val id: Long,
    val nameTr: String,
    val nameEn: String,
    val emoji: String,
    val colorHex: String,
    val orderIndex: Int
)
