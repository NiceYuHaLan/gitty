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

        val githubId = oauth2User.getAttribute<Int>("id")?.toString()
        val githubLogin = oauth2User.getAttribute<String>("login")
        val githubEmail = oauth2User.getAttribute<String>("email")
        val githubAvatar = oauth2User.getAttribute<String>("avatar_url")

        var user: User? = githubId?.let {
            userRepository.findByGithubId(it).orElse(null)
        }

        if (user == null) {
            user = githubLogin?.let {
                userRepository.findByUsername(it).orElse(null)
            }
        }

        if (user == null) {
            user = User(
                username = githubLogin!!,
                password = null,
                email = githubEmail,
                githubId = githubId,
                githubAvatar = githubAvatar,
                provider = "github"
            )
            user = userRepository.save(user)
        }

        return oauth2User
    }
}