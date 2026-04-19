package com.muhammetkocak.turkcekelimeapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Long,
    val nameTr: String,
    val nameEn: String,
    val emoji: String,
    val colorHex: String,
    val orderIndex: Int
)
