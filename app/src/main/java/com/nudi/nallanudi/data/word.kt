package com.nudi.nallanudi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val englishTerm: String,
    val kannadaTerm: String,
    val explanation: String,
    val example: String,
    val subject: String,
    val isBookmarked: Boolean = false
)