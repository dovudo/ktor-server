package com.tests

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

    /*
   SHA1 Hashing function for generate new token and refresh token
   @param password: Password or any string
   @return Hashed string */
    fun getHash(password: String): String {
        val hashKey = hex("6819b57a326945c1968f45236589")
        val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")
        val hmac = Mac.getInstance("HmacSHA1")
        hmac.init(hmacKey)
        return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
    }

private val algorithm = Algorithm.HMAC256("99devs")
fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
    .require(algorithm)
    .withAudience(audience)
    .withIssuer(issuer)
    .build()