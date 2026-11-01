package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Step
import com.example.ui.theme.BeadActive
import com.example.ui.theme.BeadCompleted
import com.example.ui.theme.BeadInactiveDark
import com.example.ui.theme.BeadInactiveLight
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Circular Rosary Bead Counter for a Rosary Decade:
 * Arranges Our Father, 10 Hail Mary beads, and Glory Be in a circular rosary garland.
 */
@Composable
fun CircularRosaryCounter(
    currentStep: Step,
    currentStepIndex: Int,
    steps: List<Step>,
    onJumpToStep: (Int) -> Unit,
    onCenterClick: () -> Unit,
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
    val inactiveColor = if (isDark) BeadInactiveDark else BeadInactiveLight
    val cordColor = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        modifier = modifier
            .testTag("circular_rosary_counter"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 3.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val sizePx = constraints.maxWidth.coerceAtMost(constraints.maxHeight)
            val density = LocalDensity.current
            val radiusPx = (sizePx / 2f) * 0.72f
            val radiusDp = with(density) { radiusPx.toDp() }

            // Connecting Circular Cord and Active Progress Arc
            val primaryColor = MaterialTheme.colorScheme.primary
            val activeGold = SacredGoldBright

            // Progress angle
            val progressSweepAngle = when {
                isOurFather -> 30f
                currentBeadNumber in 1..10 -> 30f + (currentBeadNumber * 27.5f)
                isGloryBe -> 330f
                currentDecade > 0 -> 360f
                else -> 0f
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerOffset = Offset(size.width / 2f, size.height / 2f)
                
                // Rosary Cord
                drawCircle(
                    color = cordColor,
                    radius = radiusPx,
                    center = centerOffset,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Gold Progress Arc
                if (progressSweepAngle > 0f) {
                    drawArc(
                        color = activeGold,
                        startAngle = -90f,
                        sweepAngle = progressSweepAngle,
                        useCenter = false,
                        topLeft = Offset(centerOffset.x - radiusPx, centerOffset.y - radiusPx),
                        size = androidx.compose.ui.geometry.Size(radiusPx * 2, radiusPx * 2),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Central Interactive Hub / Bead Counter Display
            CenterDisplayHub(
                currentDecade = currentDecade,
                currentBeadNumber = currentBeadNumber,
                isOurFather = isOurFather,
                isGloryBe = isGloryBe,
                currentStep = currentStep,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCenterClick()
                }
            )

            // Pulse animation for the active bead
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.18f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            // 12 Beads around the circle (30 degrees each)
            // 0: Our Father (-90 deg, top)
            // 1..10: Hail Mary 1..10 (-60 to 210 deg)
            // 11: Glory Be (240 deg)
            for (i in 0..11) {
                val angleDeg = (i * 30.0) - 90.0
                val angleRad = Math.toRadians(angleDeg)
                val xOffsetDp = (radiusDp.value * cos(angleRad)).dp
                val yOffsetDp = (radiusDp.value * sin(angleRad)).dp

                val isThisOurFather = i == 0
                val isThisGloryBe = i == 11
                val hailMaryIndex = if (i in 1..10) i else 0

                // Determine active & completed state
                val isBeadActive = when {
                    isThisOurFather -> isOurFather
                    isThisGloryBe -> isGloryBe
                    else -> currentStep is Step.Prayer && currentStep.id == "hail_mary" && currentStep.decade == currentDecade && currentStep.bead == hailMaryIndex
                }

                val isBeadCompleted = when {
                    isThisOurFather -> (currentBeadNumber > 0 || isGloryBe)
                    isThisGloryBe -> false
                    else -> (currentBeadNumber > hailMaryIndex) || isGloryBe
                }

                Box(
                    modifier = Modifier
                        .offset(x = xOffsetDp, y = yOffsetDp)
                        .testTag("circular_bead_$i"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularBeadItem(
                        index = i,
                        label = when {
                            isThisOurFather -> "✝"
                            isThisGloryBe -> "★"
                            else -> "$hailMaryIndex"
                        },
                        isSpecial = isThisOurFather || isThisGloryBe,
                        isActive = isBeadActive,
                        isCompleted = isBeadCompleted,
                        pulseScale = if (isBeadActive) pulseScale else 1f,
                        inactiveColor = inactiveColor,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Find corresponding step in the sequence
                            val targetIndex = when {
                                isThisOurFather -> steps.indexOfFirst {
                                    it is Step.Prayer && it.id == "our_father" && it.decade == currentDecade
                                }
                                isThisGloryBe -> steps.indexOfFirst {
                                    it is Step.Prayer && it.id == "glory_be" && it.decade == currentDecade
                                }
                                else -> steps.indexOfFirst {
                                    it is Step.Prayer && it.id == "hail_mary" && it.decade == currentDecade && it.bead == hailMaryIndex
                                }
                            }
                            if (targetIndex >= 0) {
                                onJumpToStep(targetIndex)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterDisplayHub(
    currentDecade: Int,
    currentBeadNumber: Int,
    isOurFather: Boolean,
    isGloryBe: Boolean,
    currentStep: Step,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .testTag("circular_center_hub"),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold.copy(alpha = 0.6f)),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            when {
                currentStep is Step.Section -> {
                    Text(
                        text = "صلوات",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "المسبحة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "المس للتقدم",
                        style = MaterialTheme.typography.labelSmall,
                        color = SacredGold,
                        fontSize = 10.sp
                    )
                }
                isOurFather -> {
                    Text(
                        text = "السر $currentDecade",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "أبانا",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "الصلاة الربية",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp
                    )
                }
                isGloryBe -> {
                    Text(
                        text = "السر $currentDecade",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "المجد",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "المجد للآب",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp
                    )
                }
                currentBeadNumber in 1..10 -> {
                    Text(
                        text = "السر $currentDecade",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$currentBeadNumber",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 28.sp
                    )
                    Text(
                        text = "من ١٠ حبات",
                        style = MaterialTheme.typography.labelSmall,
                        color = SacredGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                else -> {
                    Text(
                        text = "تأمل السر",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "السر $currentDecade",
                        style = MaterialTheme.typography.labelSmall,
                        color = SacredGold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CircularBeadItem(
    index: Int,
    label: String,
    isSpecial: Boolean,
    isActive: Boolean,
    isCompleted: Boolean,
    pulseScale: Float,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val beadColor by animateColorAsState(
        targetValue = when {
            isActive -> BeadActive
            isCompleted -> BeadCompleted
            isSpecial -> SacredGold.copy(alpha = 0.5f)
            else -> inactiveColor
        },
        label = "beadColor"
    )

    val targetSize = when {
        isActive -> 32.dp
        isSpecial -> 26.dp
        else -> 22.dp
    }

    Box(
        modifier = Modifier
            .size(38.dp) // Accessibility touch envelope
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(targetSize)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(beadColor)
                .border(
                    width = if (isActive) 2.5.dp else 1.dp,
                    color = if (isActive) SacredGoldBright else Color.Transparent,
                    shape = CircleShape
                ),
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
                fontSize = if (isSpecial) 12.sp else 10.sp
            )
        }
    }
}
