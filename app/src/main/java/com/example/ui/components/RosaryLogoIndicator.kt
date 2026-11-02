package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Step
import com.example.ui.theme.BeadActive
import com.example.ui.theme.BeadCompleted
import com.example.ui.theme.BeadInactiveDark
import com.example.ui.theme.BeadInactiveLight
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright

/**
 * Compact Rosary Logo & Side Bead Indicator:
 * Condenses the Rosary beads into an elegant side logo badge and interactive bead dot strip (~52dp height),
 * freeing up maximum screen space for the prayer text reading area.
 */
@Composable
fun RosaryLogoIndicator(
    currentStep: Step,
    currentStepIndex: Int,
    steps: List<Step>,
    onJumpToStep: (Int) -> Unit,
    onLogoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val currentDecade = when (currentStep) {
        is Step.MysteryHeader -> currentStep.n
        is Step.Prayer -> currentStep.decade
        else -> 0
    }

    val currentBeadNumber = when (currentStep) {
        is Step.Prayer -> if (currentStep.id == "hail_mary") currentStep.bead else 0
        else -> 0
    }

    val isOurFather = currentStep is Step.Prayer && currentStep.id == "our_father"
    val isGloryBe = currentStep is Step.Prayer && currentStep.id == "glory_be"

    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val inactiveDotColor = if (isDark) BeadInactiveDark else BeadInactiveLight

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rosary_logo_indicator"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Decade info badge
            Surface(
                color = SacredGold.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentDecade > 0) "السر $currentDecade" else "صلوات",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = when {
                            isOurFather -> "أبانا"
                            isGloryBe -> "المجد"
                            currentBeadNumber in 1..10 -> "$currentBeadNumber / ١٠"
                            else -> "الوردية"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Center: Interactive 12-Bead Dot Strip (1 Our Father + 10 Hail Mary + 1 Glory Be)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                // Our Father Dot (Cross ✝)
                BeadDotItem(
                    label = "✝",
                    isActive = isOurFather,
                    isCompleted = currentBeadNumber > 0 || isGloryBe,
                    inactiveColor = inactiveDotColor,
                    sizeDp = 22,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val idx = steps.indexOfFirst {
                            it is Step.Prayer && it.id == "our_father" && it.decade == currentDecade
                        }
                        if (idx >= 0) onJumpToStep(idx)
                    }
                )

                Spacer(modifier = Modifier.width(2.dp))

                // 10 Hail Mary Bead Dots
                for (b in 1..10) {
                    val isBeadActive = currentStep is Step.Prayer && currentStep.id == "hail_mary" &&
                            currentStep.decade == currentDecade && currentStep.bead == b
                    val isBeadCompleted = (currentBeadNumber > b) || isGloryBe

                    BeadDotItem(
                        label = "$b",
                        isActive = isBeadActive,
                        isCompleted = isBeadCompleted,
                        inactiveColor = inactiveDotColor,
                        sizeDp = 18,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            val idx = steps.indexOfFirst {
                                it is Step.Prayer && it.id == "hail_mary" && it.decade == currentDecade && it.bead == b
                            }
                            if (idx >= 0) onJumpToStep(idx)
                        }
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // Glory Be Dot (Star ★)
                BeadDotItem(
                    label = "★",
                    isActive = isGloryBe,
                    isCompleted = false,
                    inactiveColor = inactiveDotColor,
                    sizeDp = 22,
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val idx = steps.indexOfFirst {
                            it is Step.Prayer && it.id == "glory_be" && it.decade == currentDecade
                        }
                        if (idx >= 0) onJumpToStep(idx)
                    }
                )
            }

            // Right: Rosary Emblem Logo
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLogoClick)
                    .border(1.5.dp, SacredGold, CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                tonalElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✝",
                            style = MaterialTheme.typography.labelSmall,
                            color = SacredGold,
                            fontSize = 11.sp,
                            lineHeight = 11.sp
                        )
                        Text(
                            text = when {
                                currentBeadNumber in 1..10 -> "$currentBeadNumber"
                                isOurFather -> "١"
                                isGloryBe -> "م"
                                else -> "وردية"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BeadDotItem(
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    inactiveColor: Color,
    sizeDp: Int,
    onClick: () -> Unit
) {
    val dotColor by animateColorAsState(
        targetValue = when {
            isActive -> BeadActive
            isCompleted -> BeadCompleted
            else -> inactiveColor
        },
        label = "dotColor"
    )

    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(dotColor)
            .border(
                width = if (isActive) 2.dp else 0.5.dp,
                color = if (isActive) SacredGoldBright else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = when {
                isActive -> Color.Black
                isCompleted -> Color.White
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (sizeDp > 18) 10.sp else 8.5.sp,
            textAlign = TextAlign.Center
        )
    }
}
