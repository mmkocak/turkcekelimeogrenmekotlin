package com.muhammetkocak.turkcekelimeapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "card_state",
    primaryKeys = ["wordId", "direction"],
    indices = [
        Index(value = ["dueAt"]),
        Index(value = ["direction"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardStateEntity(
    val wordId: Long,
    val direction: String,
    val easiness: Double = 2.5,
    val intervalDays: Int = 0,
    val repetition: Int = 0,
    val dueAt: Long,
    val lastReviewedAt: Long? = null,
    val mastery: String = "NEW"
)
