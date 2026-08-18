package com.example.util

object SpecificityCoach {
    private val vagueKeywords = setOf(
        "work", "website", "project", "code", "coding", "emails", "email",
        "stuff", "things", "task", "tasks", "client", "prep", "study",
        "fixing", "fix bugs", "marketing", "sales", "design", "admin", "app"
    )

    private val vaguePhrases = listOf(
        "work on", "do work", "fixing bugs", "fix bug", "client work",
        "work on website", "work on app", "work on project", "do tasks"
    )

    fun isVague(title: String): Boolean {
        val trimmed = title.trim().lowercase()
        if (trimmed.isEmpty()) return false
        if (trimmed.length < 8) return true

        if (vaguePhrases.any { trimmed.contains(it) }) {
            return true
        }

        val words = trimmed.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        if (words.size <= 4 && words.any { it in vagueKeywords }) {
            return true
        }
        return false
    }

    fun getSuggestion(title: String): String {
        val trimmed = title.trim().lowercase()
        return when {
            trimmed.contains("web") || trimmed.contains("site") ->
                "Make it specific—for example: Complete and test the checkout page."
            trimmed.contains("email") || trimmed.contains("reply") ->
                "Make it specific—for example: Reply to 5 client inquiries and send proposal."
            trimmed.contains("proposal") || trimmed.contains("growth") || trimmed.contains("client") ->
                "Make it specific—for example: Send 5 tailored pitches to prospective clients."
            trimmed.contains("code") || trimmed.contains("bug") || trimmed.contains("fix") ->
                "Make it specific—for example: Fix payment webhook timeout and deploy hotfix."
            else ->
                "Make it specific—for example: Complete and test the checkout page."
        }
    }
}
