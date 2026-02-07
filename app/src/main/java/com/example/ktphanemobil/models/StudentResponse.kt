package com.example.ktphanemobil.models

data class StudentResponse(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val token: String
)