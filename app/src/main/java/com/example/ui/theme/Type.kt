package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val AmiriFontFamily = FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 19.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    )
)
