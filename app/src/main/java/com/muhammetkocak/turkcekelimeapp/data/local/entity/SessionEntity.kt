package com.muhammetkocak.turkcekelimeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val correctCount: Int = 0,
    val wrongCount: Int = 0
)
