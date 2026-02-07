package com.example.ktphanemobil.models

data class AddBookRequest(
    val title: String,
    val author: String,
    val isbn: String,
    val libraryId: Int
)
