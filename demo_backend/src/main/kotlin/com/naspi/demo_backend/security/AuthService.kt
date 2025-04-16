package com.naspi.demo_backend.security

import com.naspi.demo_backend.database.model.RefreshToken
import com.naspi.demo_backend.database.model.User
import com.naspi.demo_backend.database.repository.RefreshTokenRepository
import com.naspi.demo_backend.database.repository.UserRepository

import org.bson.types.ObjectId
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val jwtService: JWTService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    fun registerUser(email: String, password: String): User {
        return userRepository.save(
            User(
                email= email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    fun loginUser(email: String, password: String): TokenPair{
        val user= userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials")
        if(!hashEncoder.checkMatch(password, user.hashedPassword)){
            throw BadCredentialsException("Invalid Credentials")
        }
        val newAccessToken= jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken= jwtService.generateRefreshToken(user.id.toHexString())
        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(accessToken = newAccessToken, refreshToken= newRefreshToken)
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)){
            throw IllegalArgumentException("Invalid refresh token")
        }
        val userId= jwtService.getUserIdFromToken(refreshToken)
        val user= userRepository.findById(ObjectId(userId)).orElseThrow{
            IllegalArgumentException("Invalid Refresh Token")
        }
        val hashed= hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?:throw IllegalArgumentException("RefreshToken not recognized")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken= jwtService.generateAccessToken(userId)
        val newRefreshToken= jwtService.generateRefreshToken(userId)
        storeRefreshToken(user.id, newRefreshToken)
        return TokenPair(newAccessToken, newRefreshToken)
    }

    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String){
        val hashedToken= hashToken(rawRefreshToken)
        val expiryMs= jwtService.refreshTokenValidityMs
        val expiresAt= Instant.now().plusMillis(expiryMs)
        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken= hashedToken
            )
        )
    }

    private fun hashToken(token: String): String{
        val digest= MessageDigest.getInstance("SHA-256")
        val hashedBytes= digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashedBytes)
    }


}