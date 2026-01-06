package com.example.ktphanemobil.models

import java.io.Serializable

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val isbn: String?,
    val libraryName: String?
) : Serializable