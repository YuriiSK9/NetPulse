package com.yurii.netpulse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography = Typography(
    titleLarge = TextStyle(
        fontSize = 64.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    bodyMedium = TextStyle(
        fontSize = 24.sp
    )
)