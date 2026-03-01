package com.example.ktphanemobil.models

data class UpdateBookRequest(
    val title: String,
    val author: String,
    val isbn: String?,
    val pageCount: Int?,
    val libraryId: Int?
)