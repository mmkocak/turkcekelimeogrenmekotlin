package com.muhammetkocak.turkcekelimeapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["foreignTerm", "turkishTerm"], unique = true),
        Index(value = ["isFavorite"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
)
