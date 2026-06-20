package org.example.zitadellogin.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.zitadellogin.core.localization.FaStrings

private val LoginButtonShape = RoundedCornerShape(18.dp)
private val LoginButtonHeight = 56.dp

@Composable
fun LoginHeroContent(
    modifier: Modifier = Modifier,
    errorMessage: String?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = FaStrings.appTitle,
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = FaStrings.loginWelcome,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = FaStrings.loginTitle,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = FaStrings.loginBrandTagline,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
            textAlign = TextAlign.Center,
        )
        Text(
            text = FaStrings.loginSubtitleNative,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp),
        )
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut(),
        ) {
            errorMessage?.let { LoginErrorBanner(message = it) }
        }
    }
}

@Composable
fun LoginBottomActions(
    modifier: Modifier = Modifier,
    isBusy: Boolean,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onLoginWithKeyClock: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LoginPrimaryButton(
            text = FaStrings.loginButton,
            onClick = onLogin,
            loading = isBusy,
        )
        LoginPrimaryButton(
            text = FaStrings.loginWithKeyClockButton,
            onClick = onLoginWithKeyClock,
            loading = isBusy,
        )
        LoginSecondaryButton(
            text = FaStrings.registerButton,
            onClick = onRegister,
            enabled = !isBusy,
        )
    }
}

@Composable
private fun LoginPrimaryButton(
    text: String,
    onClick: () -> Unit,
    loading: Boolean,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(LoginButtonHeight)
            .shadow(
                elevation = 10.dp,
                shape = LoginButtonShape,
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
            ),
        enabled = !loading,
        shape = LoginButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(end = 10.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            ),
        )
    }
}

@Composable
private fun LoginSecondaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(LoginButtonHeight),
        enabled = enabled,
        shape = LoginButtonShape,
        border = BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            ),
        )
    }
}

@Composable
private fun LoginErrorBanner(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
        tonalElevation = 2.dp,
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun LoginReadabilityOverlay(
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.surface
    val primaryTint = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to surface.copy(alpha = 0.45f),
                        0.30f to surface.copy(alpha = 0.68f),
                        0.55f to surface.copy(alpha = 0.84f),
                        0.80f to surface.copy(alpha = 0.93f),
                        1.0f to surface.copy(alpha = 0.97f),
                    ),
                ),
            )
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to primaryTint.copy(alpha = 0.07f),
                        0.5f to Color.Transparent,
                        1.0f to primaryTint.copy(alpha = 0.05f),
                    ),
                ),
            ),
    )
}
