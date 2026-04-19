package com.muhammetkocak.turkcekelimeapp.data.local.seed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeedPayload(
    val categories: List<SeedCategory>,
    val words: List<SeedWord>
)

@Serializable
data class SeedCategory(
    val id: Long,
    val nameTr: String,
    val nameEn: String,
    val emoji: String,
    @SerialName("color") val colorHex: String,
    @SerialName("order") val orderIndex: Int
)

@Serializable
data class SeedWord(
    @SerialName("fr") val foreignTerm: String,
    @SerialName("tr") val turkishTerm: String,
    @SerialName("pos") val partOfSpeech: String? = null,
    @SerialName("exFr") val exampleForeign: String? = null,
    @SerialName("exTr") val exampleTurkish: String? = null,
    val ipa: String? = null,
    @SerialName("cat") val categoryId: Long
)
