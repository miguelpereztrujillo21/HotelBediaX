package com.mpt.hotelbediax.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destination")
data class Destination(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val countryCode: String,
    val type: String,
    val lastModify: String
)