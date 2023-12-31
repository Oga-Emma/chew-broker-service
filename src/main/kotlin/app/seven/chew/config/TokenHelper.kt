package app.seven.chew.config

import app.seven.chew.features.user.model.User
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class TokenHelper (
    private val jwtDecoder: JwtDecoder,
    private val jwtEncoder: JwtEncoder,
) {
    fun createToken(user: User, ttl: Long, unit: ChronoUnit): String {
        val jwsHeader = JwsHeader.with { "HS256" }.build()
        val claims = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(ttl, ChronoUnit.DAYS))
            .subject(user.name)
            .claim("userId", user.id)
            .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).tokenValue
    }

    fun parseToken(token: String, resolveUser: (userId: UUID) -> User?): User? {
        return try {
            val jwt = jwtDecoder.decode(token)
            val userId = UUID.fromString(jwt.claims["userId"] as String)
            resolveUser(userId)
        } catch (e: Exception) {
            null
        }
    }
}