package com.example.ktphanemobil.models

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)