package com.kio.controllers

import com.kio.dto.request.auth.LoginDTO
import com.kio.dto.request.auth.TokenResponseDTO
import com.kio.services.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController (private val authenticationService: AuthenticationService) {

    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDTO: LoginDTO): ResponseEntity<TokenResponseDTO> {
        val tokenResponse = authenticationService.login(LoginDTO("glaze", "password"))
        return ResponseEntity.status(HttpStatus.OK)
            .body(tokenResponse)
    }

    @PostMapping("/token")
    fun getTokens(@RequestParam(name = "refresh_token") refreshToken: String): ResponseEntity<TokenResponseDTO> {
        val tokenResponse = authenticationService.getTokenPair(refreshToken)
        return ResponseEntity.status(HttpStatus.OK)
            .body(tokenResponse)
    }

}