package com.gitty.security

import com.gitty.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oauth2User = authentication.principal as OAuth2User
        val githubLogin = oauth2User.getAttribute<String>("login")

        val user = githubLogin?.let {
            userRepository.findByUsername(it).orElse(null)
        }

        if (user != null) {
            val token = jwtUtil.generateToken(user.username)

            val redirectUrl = "http://localhost:5173/auth/callback?token=${URLEncoder.encode(token, "UTF-8")}&username=${URLEncoder.encode(user.username, "UTF-8")}&userId=${user.id}"

            response.sendRedirect(redirectUrl)
        } else {
            response.sendRedirect("http://localhost:5173/login?error=oauth_user_not_found")
        }
    }
}