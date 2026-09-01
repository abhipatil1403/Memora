package com.memora.feature.onboarding

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: String  // emoji icon for now, would be Lottie in production
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Capture Anything",
        description = "Point your camera at whiteboards, receipts, posters, prescriptions — anything with text or information.",
        icon = "📸"
    ),
    OnboardingPage(
        title = "AI Understands It",
        description = "Memora extracts text, detects dates, contacts, amounts, and organizes everything automatically.",
        icon = "🧠"
    ),
    OnboardingPage(
        title = "Recall Instantly",
        description = "Search by meaning, not keywords. Ask \"What was the doctor's number?\" and Memora finds it.",
        icon = "⚡"
    )
)
