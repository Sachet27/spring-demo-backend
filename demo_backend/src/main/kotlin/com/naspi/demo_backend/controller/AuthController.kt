package com.naspi.demo_backend.controller

import com.naspi.demo_backend.security.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    val authService: AuthService
) {
    data class AuthRequest(
        val email: String,
        val password: String
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping("/register")
    fun register(
        @RequestBody user: AuthRequest
    ){
        authService.registerUser(user.email, user.password)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody user: AuthRequest
    ): AuthService.TokenPair{
      return authService.loginUser(user.email, user.password)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody refreshToken: RefreshRequest
    ): AuthService.TokenPair{
        return authService.refresh(refreshToken.refreshToken)
    }
}