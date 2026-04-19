package com.muhammetkocak.turkcekelimeapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    indices = [
        Index(value = ["wordId"]),
        Index(value = ["reviewedAt"]),
        Index(value = ["sessionId"])
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
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val wordId: Long,
    val direction: String,
    val rating: Int,
    val reviewedAt: Long,
    val previousIntervalDays: Int,
    val nextIntervalDays: Int,
    val sessionId: Long? = null
)
