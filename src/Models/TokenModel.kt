package com.tests.Models

data class TokenModel(
    val token: String,
    val refreshToken: String?,
    val date: String?
)