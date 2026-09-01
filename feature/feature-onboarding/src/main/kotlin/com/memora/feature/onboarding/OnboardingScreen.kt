package com.memora.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memora.core.ui.components.MemoraButton
import com.memora.core.ui.theme.MemoraTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val pages = onboardingPages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val colors = MemoraTheme.colors
    val isLastPage = pagerState.currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = MemoraTheme.spacing.space6)
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MemoraTheme.spacing.space4),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onOnboardingComplete) {
                Text(
                    text = "Skip",
                    color = colors.onSurfaceVariant,
                    style = MemoraTheme.typography.body
                )
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(page = pages[page])
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = MemoraTheme.spacing.space6),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "indicator"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) colors.primary
                            else colors.outline
                        )
                )
                if (index < pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Action button
        AnimatedContent(
            targetState = isLastPage,
            transitionSpec = {
                (fadeIn(tween(200)) + slideInHorizontally { it / 2 })
                    .togetherWith(fadeOut(tween(200)) + slideOutHorizontally { -it / 2 })
            },
            label = "button"
        ) { lastPage ->
            MemoraButton(
                onClick = {
                    if (lastPage) {
                        onOnboardingComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                text = if (lastPage) "Get Started" else "Continue",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MemoraTheme.spacing.space8)
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val colors = MemoraTheme.colors

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Text(
            text = page.icon,
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space10))

        // Title
        Text(
            text = page.title,
            style = MemoraTheme.typography.display,
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))

        // Description
        Text(
            text = page.description,
            style = MemoraTheme.typography.body,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = MemoraTheme.spacing.space4)
        )
    }
}
