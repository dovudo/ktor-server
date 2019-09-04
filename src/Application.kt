package com.tests

import com.tests.Models.TokenModel
import com.tests.dao.DAOFacadeDatabase
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.response.respond
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.samples.httpbin.HttpBinError
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.error
import org.jetbrains.exposed.sql.Database
import java.text.DateFormat

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.myFirstModule(){

    val jwtIssuer = "localhost:8080"
    val jwtAudience = "jwt-audience"
    val jwtRealm = "ktor sample app"

    /*
    * Install features
    * */
    install(CallLogging)
    install(DefaultHeaders)

        install(Authentication) {
        val jwtVerifier = makeJwtVerifier(jwtIssuer, jwtAudience)
        jwt{
            realm = jwtRealm
            verifier(jwtVerifier)
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
    install(Sessions){
        cookie<TokenModel>("SESSION")
    }
    install(ContentNegotiation){ gson{
        setDateFormat(DateFormat.LONG)
        setPrettyPrinting()
    }}
    install(StatusPages) {
        exception<Throwable> { cause ->
            environment.log.error(cause)
            val error = HttpBinError(code = HttpStatusCode.InternalServerError, request = call.request.local.uri, message = cause.toString(), cause = cause)
            call.respond(error)
        }}
        //install(Authentication)

    @Location("/registration")
    class Auth(val hash: (String) -> String?)


    routing{
        registration()
        route("/auth"){

        }
    }
}

val dao = DAOFacadeDatabase(Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver"))
fun main(args: Array<String>) {
    dao.init()
    //seeding
    dao.seed("test","pass")
    dao.seed("Alex", "Lee")
    dao.seed("Alberto","Ensteino")
    dao.seed("Nikola", "Tesla")
    embeddedServer(Netty, 8080, module = Application::myFirstModule).start()
}

