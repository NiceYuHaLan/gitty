package com.gitty.security

import com.gitty.model.User
import com.gitty.repository.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)

        val githubId = oauth2User.getAttribute<Int>("id")?.toLong()
        val githubLogin = oauth2User.getAttribute<String>("login")
        val githubEmail = oauth2User.getAttribute<String>("email")

        var user = githubId?.let {
            userRepository.findByGitHubId(it).orElse(null)
        }

        if (user == null) {
            user = githubLogin?.let {
                userRepository.findByUsername(it).orElse(null)
            }
        }

        if (user == null && githubLogin != null && githubId != null) {
            user = User(
                gitHubId = githubId,
                username = githubLogin,
                email = githubEmail,
                password = null,
                gitHubAccessToken = userRequest.accessToken.tokenValue
            )
            user = userRepository.save(user)
        }

        return oauth2User
    }
}