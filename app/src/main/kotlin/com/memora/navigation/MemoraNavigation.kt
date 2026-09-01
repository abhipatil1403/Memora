package com.memora.navigation

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.memora.feature.auth.AuthScreen
import com.memora.feature.capture.CaptureScreen
import com.memora.feature.collections.CollectionsScreen
import com.memora.feature.home.HomeScreen
import com.memora.feature.library.LibraryScreen
import com.memora.feature.memory.detail.MemoryDetailScreen
import com.memora.feature.onboarding.OnboardingScreen
import com.memora.feature.processing.ProcessingScreen
import com.memora.feature.profile.ProfileScreen
import com.memora.feature.reminders.RemindersScreen
import com.memora.feature.result.ResultScreen
import com.memora.feature.search.SearchScreen
import com.memora.feature.settings.SettingsScreen
import com.memora.feature.splash.SplashScreen

@Composable
fun MemoraNavigation(startIntentAction: String? = null) {
    val backStack = rememberNavBackStack(SplashKey)

    // Handle deep links from shortcuts on initial launch
    androidx.compose.runtime.LaunchedEffect(startIntentAction) {
        if (startIntentAction == "com.memora.action.CAPTURE") {
            backStack.clear()
            backStack.add(HomeKey)
            backStack.add(CaptureKey)
        } else if (startIntentAction == "com.memora.action.SEARCH") {
            backStack.clear()
            backStack.add(HomeKey)
            backStack.add(SearchKey)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            // ── Splash ──
            entry<SplashKey> {
                SplashScreen(
                    onNavigateToOnboarding = {
                        backStack.clear()
                        backStack.add(AuthKey)
                    },
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(HomeKey)
                    }
                )
            }

            // ── Onboarding ──
            entry<OnboardingKey> {
                OnboardingScreen(
                    onOnboardingComplete = {
                        backStack.clear()
                        backStack.add(AuthKey)
                    }
                )
            }

            // ── Auth ──
            entry<AuthKey> {
                AuthScreen(
                    onAuthSuccess = {
                        backStack.clear()
                        backStack.add(HomeKey)
                    }
                )
            }

            // ── Home ──
            entry<HomeKey> {
                HomeScreen(
                    onNavigateToCapture = { backStack.add(CaptureKey) },
                    onNavigateToMemoryDetail = { id -> backStack.add(MemoryDetailKey(id)) },
                    onNavigateToLibrary = { backStack.add(LibraryKey) },
                    onNavigateToSearch = { backStack.add(SearchKey) },
                    onNavigateToProfile = { backStack.add(ProfileKey) },
                    modifier = Modifier.safeDrawingPadding()
                )
            }

            // ── Capture ──
            entry<CaptureKey> {
                CaptureScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToProcessing = { imageId ->
                        backStack.removeLastOrNull()
                        backStack.add(ProcessingKey(imageId))
                    }
                )
            }

            // ── Processing ──
            entry<ProcessingKey> {
                ProcessingScreen(
                    onNavigateToResult = { memoryId ->
                        backStack.removeLastOrNull()
                        backStack.add(ResultKey(memoryId))
                    }
                )
            }

            // ── Result ──
            entry<ResultKey> {
                ResultScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(HomeKey)
                    }
                )
            }

            // ── Library ──
            entry<LibraryKey> {
                LibraryScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToMemoryDetail = { id -> backStack.add(MemoryDetailKey(id)) }
                )
            }

            // ── Memory Detail ──
            entry<MemoryDetailKey> {
                MemoryDetailScreen(
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            // ── Collections ──
            entry<CollectionsKey> {
                CollectionsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onCollectionClick = { /* navigate to filtered library */ }
                )
            }

            // ── Search ──
            entry<SearchKey> {
                SearchScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onMemoryClick = { id -> backStack.add(MemoryDetailKey(id)) }
                )
            }

            // ── Reminders ──
            entry<RemindersKey> {
                RemindersScreen(
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }

            // ── Profile ──
            entry<ProfileKey> {
                ProfileScreen(
                    onNavigateBack = { backStack.removeLastOrNull() },
                    onNavigateToSettings = { backStack.add(SettingsKey) },
                    onNavigateToReminders = { backStack.add(RemindersKey) },
                    onNavigateToCollections = { backStack.add(CollectionsKey) },
                    onSignOut = {
                        backStack.clear()
                        backStack.add(AuthKey)
                    }
                )
            }

            // ── Settings ──
            entry<SettingsKey> {
                SettingsScreen(
                    onNavigateBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
