package org.example.project.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
// Atenție: Asigură-te că importul Res este cel corect generat de proiectul tău!
import kotlinproject.composeapp.generated.resources.* @Composable
fun NunitoSansFontFamily() = FontFamily(
        Font(Res.font.nunito_sans, weight = FontWeight.Light),
        Font(Res.font.nunito_sans, weight = FontWeight.Normal),
        Font(Res.font.nunito_sans, weight = FontWeight.Bold),
        Font(Res.font.nunito_sans, weight = FontWeight.ExtraBold)
    )

// --- MAGIA PENTRU FONT GLOBAL ---
@Composable
fun AppTypography(): Typography {
    val nunito = NunitoSansFontFamily()
    val defaultTypography = Typography()

    // Suprascriem TOATE stilurile de text să folosească Nunito
    return Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = nunito),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = nunito),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = nunito),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = nunito),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = nunito),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = nunito),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = nunito),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = nunito),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = nunito),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = nunito),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = nunito),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = nunito),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = nunito),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = nunito),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = nunito)
    )
}