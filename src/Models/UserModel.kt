package com.tests.Models

data class UserModel (
    val userId: Int?,
    val username: String,
    val hash: String,
    val token: String?,
    val refreshTokenModel: String?,
    val date: String?,
    var data: String?
)