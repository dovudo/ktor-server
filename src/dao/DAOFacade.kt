package com.tests.dao

import com.tests.Models.UserModel
import kotlinx.io.core.Closeable

interface DAOFacade: Closeable{
    fun init()
    fun createUser(username:String, hashPassword:String, token: String?, refresh_token: String?, token_date: String?)
    fun updateUser(username:String, hashPassword:String, token: String?, refresh_token: String?, token_date: String?, data:String?)
    fun updateUserData(token: String?, data: String)
    fun deleteUser(id:Int)
    fun getUser(id:Int): UserModel?
    fun getUserByName(username: String): UserModel?
    fun getUserByToken(token: String): UserModel?
    fun getAllUsers(): List<UserModel>
}