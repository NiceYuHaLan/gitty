package com.gitty.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.gitty.dto.CommitAnalysisResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class ImmersAnalysisService(
    private val webClientBuilder: WebClient.Builder,
    private val objectMapper: ObjectMapper
) {

    @Value("\${immers.api.url}")
    private lateinit var apiUrl: String

    @Value("\${immers.api.token}")
    private lateinit var apiToken: String

    fun analyzeCommit(commitMessage: String): CommitAnalysisResponse? {
        val prompt = """
            Проанализируй следующий коммит и верни результат СТРОГО в формате JSON:
            {
              "summary": "Опиши суть изменений на русском языке, коротко",
              "sentiment": "positive/neutral/negative",
              "tags": "bugfix,feature,documentation,style,refactor,test", 
              "risks": "Опиши возможные риски на русском языке или оставь пустую строку"
            }
            
            Сообщение коммита: $commitMessage
        """.trimIndent()

        val requestBody = mapOf(
            "model" to "gpt-oss-20b",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "Ты — эксперт по анализу кода. Отвечай только JSON."),
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.2,
            "max_tokens" to 500
        )

        return try {
            val response = webClientBuilder.build().post()
                .uri(apiUrl)
                .header("Authorization", "Bearer $apiToken")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            if (response == null) return null

            val rootNode = objectMapper.readTree(response)
            val content = rootNode.path("choices")[0].path("message").path("content").asText()
            val cleaned = content.replace("```json", "").replace("```", "").trim()
            objectMapper.readValue(cleaned, CommitAnalysisResponse::class.java)
        } catch (e: Exception) {
            println("Error analyzing commit: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}