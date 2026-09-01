package com.memora.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.memora.core.ui.components.MemoraButton
import com.memora.core.ui.components.MemoraOutlinedButton
import com.memora.core.ui.theme.MemoraTheme

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MemoraTheme.colors
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MemoraTheme.spacing.space6),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space12))

        // Header
        Text(
            text = if (state.isLogin) "Welcome back" else "Create account",
            style = MemoraTheme.typography.display,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space2))
        Text(
            text = if (state.isLogin) "Sign in to access your memories"
            else "Start your visual memory journey",
            style = MemoraTheme.typography.body,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space8))

        // Display name field (Register only)
        AnimatedVisibility(
            visible = !state.isLogin,
            enter = fadeIn(tween(200)) + expandVertically(tween(250)),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(250))
        ) {
            Column {
                MemoraTextField(
                    value = state.displayName,
                    onValueChange = { viewModel.onEvent(AuthEvent.DisplayNameChanged(it)) },
                    label = "Full Name",
                    leadingIcon = Icons.Outlined.Person,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))
            }
        }

        // Email field
        MemoraTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
            label = "Email",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space4))

        // Password field
        var passwordVisible by remember { mutableStateOf(false) }
        MemoraTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
            label = "Password",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = colors.textHint
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    viewModel.onEvent(AuthEvent.Submit)
                }
            )
        )

        // Error message
        AnimatedVisibility(
            visible = state.error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            state.error?.let { error ->
                Text(
                    text = error,
                    color = colors.error,
                    style = MemoraTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MemoraTheme.spacing.space3)
                )
            }
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))

        // Submit button
        if (state.isLoading) {
            CircularProgressIndicator(
                color = colors.primary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(48.dp)
            )
        } else {
            MemoraButton(
                onClick = { viewModel.onEvent(AuthEvent.Submit) },
                text = if (state.isLogin) "Sign In" else "Create Account",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
            Text(
                text = "  OR  ",
                style = MemoraTheme.typography.caption,
                color = colors.textHint,
                fontWeight = FontWeight.Medium
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = colors.outline)
        }

        Spacer(modifier = Modifier.height(MemoraTheme.spacing.space6))

        // Google Sign-In Button
        MemoraOutlinedButton(
            onClick = {
                // Triggers Google Sign In flow
            },
            text = "Continue with Google",
            leadingIcon = {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.memora.core.ui.R.drawable.ic_google),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Toggle login / register
        Row(
            modifier = Modifier.padding(vertical = MemoraTheme.spacing.space6),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (state.isLogin) "Don't have an account? " else "Already have an account? ",
                color = colors.textSecondary,
                style = MemoraTheme.typography.body
            )
            Text(
                text = if (state.isLogin) "Sign Up" else "Sign In",
                color = colors.primary,
                style = MemoraTheme.typography.body,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { viewModel.onEvent(AuthEvent.ToggleMode) }
            )
        }
    }
}

@Composable
private fun MemoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val colors = MemoraTheme.colors

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MemoraTheme.typography.label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = colors.textHint,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = MemoraTheme.typography.body.copy(color = colors.textPrimary),
        shape = MemoraTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.textHint,
            cursorColor = colors.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}
