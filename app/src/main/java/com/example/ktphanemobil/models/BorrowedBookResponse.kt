package com.example.ktphanemobil.models

data class BorrowedBookResponse(
    val borrowId: Int,
    val bookId: Int,
    val title: String,
    val author: String,
    val borrowDate: String,
    val returnDate: String?
)
