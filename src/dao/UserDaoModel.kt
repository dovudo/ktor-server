package com.tests.dao

import org.jetbrains.exposed.sql.Table


object Users : Table() {
    val id  = integer("id").autoIncrement().primaryKey()
    val username = varchar("username", 32).uniqueIndex()
    val hash = varchar("password_hash", 256)
    val token = varchar("token", 256).nullable()
    val refreshToken = varchar ("refresh_token", 256).nullable()
    val dateToken = varchar("token_date", 54).nullable()
    val data = varchar("data",1028).nullable()
}