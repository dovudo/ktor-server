package com.tests

import com.tests.Models.TokenModel
import com.tests.Models.UserModel
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.KtorExperimentalAPI
import java.time.LocalDateTime

@KtorExperimentalAPI
@Suppress("UNREACHABLE_CODE")
fun Route.registration(){

    val salt = "BE or not to BE"

/*
* Route for registration new users
* 8080/auth - PUT
* @param [username]: name
* @param [hash]: user password under SHA1
* token, refresh token and time auto generated
* */
    route("/auth"){
        put{
            val param = call.receive<UserModel>()
            val username = param.username
            val password = param.hash
            val date = LocalDateTime.now().toString()
            val token = getHash(username + password + salt + date)
            val refreshToken = getHash(token + "REFRESH" + date)
            val session = call.sessions.get<TokenModel>() ?: TokenModel(token, refreshToken, date)

            when {
                password.length < 6 -> call.respondText{ "Password should be at least 6 characters long" }
                username.length < 4 -> call.respondText { "Login should be at least 4 characters long" }
                dao.getUserByName(username) != null -> call.respondText {"User with the following login is already registered" }
                else -> {
                  try {
                      dao.createUser(username, password, token, refreshToken, date)
                      call.sessions.set(session)
                      call.respond(
                          "username : $username \n password : $password" +
                                  "\n token gen: " + getHash(password) +
                                  "\n Time: " + LocalDateTime.now() +
                                  "\n Session: " + session
                      )
                    } catch (e: Throwable) {
                        application.log.error("Failed to register user", e)
                        call.respondText {"Failed to register"}
                        }
                  }
                }
            }

        /*
        * Route for login users
        * 8080/auth - POST
        * @param token: generated token by backend, stored in session
        * @param refresh token: token for refreshing main token, stored in session
        * @param date: token lifecycle, time of create token
        *  */
        post{

            val token = (call.sessions.get<TokenModel>()?.token ?: call.respond("Token not found! \n Try to login")) as String
            val refreshToken = call.sessions.get<TokenModel>()?.refreshToken
            when {
                dao.getUserByToken(token) == null -> {
                    call.respond("You are not signed, \n Try to login")
                    call.respondRedirect("/login")
                }
                //TODO make the verify for valid tokens
            }

            try{
                 if(dao.getUserByToken(token) == null)
                     call.respond("Did't found the user")
                val post = call.receive<UserModel>()
                val data = post.data
                dao.updateUserData(post.token, post.data!!)
                call.respond("User was updated")
            }
            catch (e: Throwable){
                call.respond("User not found !")
            }
        }

        /*
        * Getting all users with all data
        * 8080/auth - GET
        * ONLY FOR TESTING
        * */
        get{
            call.respond(dao.getAllUsers())
        }
    }


}