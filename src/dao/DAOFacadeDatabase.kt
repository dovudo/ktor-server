package com.tests.dao

import com.tests.Models.UserModel
import com.tests.dao.Users.id
import com.tests.getHash
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime


class DAOFacadeDatabase(val db: Database): DAOFacade {


    override fun init() = transaction(db) {
        val log: Logger = LoggerFactory.getLogger("DataBase")
        log.info("Database initialisation")
        SchemaUtils.create(Users)
    }

    override fun createUser(
        username: String,
        hashPassword: String,
        token: String?,
        refresh_token: String?,
        token_date: String?
    )= transaction(db) {
        Users.insert {
            it[Users.username] = username;
            it[Users.hash] = hashPassword;
            if (token != null) {
                it[Users.token] = token
                it[Users.refreshToken] = refresh_token
                it[Users.dateToken] = token_date
                it[Users.data] = "empty"
            }
        }
        Unit
    }

    override fun updateUser(
        username: String,
        hashPassword: String,
        token: String?,
        refresh_token: String?,
        token_date: String?,
        data: String?
    ): Unit = transaction(db) {
        Users.update({ Users.id eq id }) {
            it[Users.username] = username
            it[Users.hash] = hashPassword
            if (token != null) {
                it[Users.token] = token
                it[Users.refreshToken] = refresh_token
                it[Users.dateToken] = token_date
                it[Users.data] = data
            }
        }
    }

    override fun updateUserData(token: String?, data: String): Unit = transaction(db){
        Users.update({Users.token eq token}){
                println(data)
                println(it)
                it[Users.data] = data
        }
    }

    override fun deleteUser(id: Int): Unit = transaction(db) {
        Users.deleteWhere { Users.id eq id }
    }


    override fun getUser(id: Int) = transaction(db) {
        Users.select { Users.id eq id }.map {
            UserModel(it[Users.id], it[Users.username], it[Users.hash],it[Users.token], it[Users.refreshToken],it[Users.dateToken],it[Users.data])
        }.singleOrNull()
    }

    override fun getUserByName(username: String) = transaction(db) {
        Users.select { Users.username eq username }.map {
            UserModel(it[Users.id], it[Users.username], it[Users.hash],it[Users.token], it[Users.refreshToken],it[Users.dateToken],it[Users.data])
        }.singleOrNull()
    }

    override fun getUserByToken(token: String) = transaction(db) {
        Users.select { Users.token eq token}.map {
            UserModel(it[Users.id], it[Users.username], it[Users.hash],it[Users.token], it[Users.refreshToken],it[Users.dateToken],it[Users.data])
        }.singleOrNull()
    }

    override fun getAllUsers() = transaction(db) {
        Users.selectAll().map {
            UserModel(it[Users.id], it[Users.username], it[Users.hash],it[Users.token], it[Users.refreshToken],it[Users.dateToken],it[Users.data])
        }
    }

    override fun close() { }

    //TODO complete that function
    fun seed(username: String, password: String){
        val salt = "BE or not to BE"
        val date = LocalDateTime.now().toString()
        val token = getHash(username + password + salt + date)
        val refreshToken = getHash(token + "REFRESH" + date)
        createUser(username,password,token,refreshToken,date)
    }
}
