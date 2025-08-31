package com.example.mynotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Note(
    val id: Long = 0,
    var title: String,
    var content: String,
    val createdAt: Date = Date(),
    var updatedAt: Date = Date(),
    var images: List<String> = emptyList()
) : Parcelable