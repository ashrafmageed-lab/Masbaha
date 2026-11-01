package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Step
import com.example.ui.theme.BeadActive
import com.example.ui.theme.BeadCompleted
import com.example.ui.theme.BeadInactiveDark
import com.example.ui.theme.BeadInactiveLight
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright

@Composable
fun RosaryBeadsVisualizer(
    currentStep: Step,
    currentStepIndex: Int,
    steps: List<Step>,
    onJumpToStep: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDecade = when (currentStep) {
        is Step.MysteryHeader -> currentStep.n
        is Step.Prayer -> currentStep.decade
        else -> 0
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rosary_beads_visualizer"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Decades Selector / Progress Bar (1 to 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (decade in 1..5) {
                    val isDecadeActive = currentDecade == decade
                    val isDecadeCompleted = currentDecade > decade || (currentDecade == 0 && currentStepIndex > 10)
                    DecadePill(
                        decadeNumber = decade,
                        isActive = isDecadeActive,
                        isCompleted = isDecadeCompleted,
                        onClick = {
                            // Find the first step of this decade (its MysteryHeader)
                            val headerIndex = steps.indexOfFirst {
                                it is Step.MysteryHeader && it.n == decade
                            }
                            if (headerIndex >= 0) {
                                onJumpToStep(headerIndex)
                            }
                        }
                    )
                }
            }

            // If we are currently inside one of the 5 decades, show the 10 beads of Hail Mary + Our Father + Glory Be
            if (currentDecade in 1..5) {
                DecadeBeadsRow(
                    decade = currentDecade,
                    currentStep = currentStep,
                    steps = steps,
                    onJumpToStep = onJumpToStep
                )
            } else {
                // Intro / Outro Section Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentStepIndex == 0) "صلوات افتتاح المسبحة" else "الصلوات الختامية للوردية",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DecadePill(
    decadeNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isActive -> MaterialTheme.colorScheme.primary
            isCompleted -> SacredGold.copy(alpha = 0.25f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "pillBg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isActive -> MaterialTheme.colorScheme.onPrimary
            isCompleted -> SacredGold
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "pillContent"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "مكتمل",
                    tint = SacredGold,
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = "السر $decadeNumber",
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun DecadeBeadsRow(
    decade: Int,
    currentStep: Step,
    steps: List<Step>,
    onJumpToStep: (Int) -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val inactiveBeadColor = if (isDark) BeadInactiveDark else BeadInactiveLight

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Our Father bead
        val isOurFatherActive = currentStep is Step.Prayer && currentStep.id == "our_father" && currentStep.decade == decade
        val isOurFatherCompleted = currentStep is Step.Prayer && (currentStep.id == "hail_mary" || currentStep.id == "glory_be") && currentStep.decade == decade
        
        SpecialBead(
            label = "أبانا",
            isActive = isOurFatherActive,
            isCompleted = isOurFatherCompleted,
            onClick = {
                val index = steps.indexOfFirst {
                    it is Step.Prayer && it.id == "our_father" && it.decade == decade
                }
                if (index >= 0) onJumpToStep(index)
            }
        )

        // 10 Hail Mary beads
        for (beadNum in 1..10) {
            val isCurrentBead = currentStep is Step.Prayer && currentStep.id == "hail_mary" && currentStep.decade == decade && currentStep.bead == beadNum
            val isPassedBead = currentStep is Step.Prayer && currentStep.decade == decade && (
                (currentStep.id == "hail_mary" && currentStep.bead > beadNum) ||
                currentStep.id == "glory_be"
            )

            SingleHailMaryBead(
                beadNum = beadNum,
                isActive = isCurrentBead,
                isCompleted = isPassedBead,
                inactiveColor = inactiveBeadColor,
                onClick = {
                    val index = steps.indexOfFirst {
                        it is Step.Prayer && it.id == "hail_mary" && it.decade == decade && it.bead == beadNum
                    }
                    if (index >= 0) onJumpToStep(index)
                }
            )
        }

        // Glory Be bead
        val isGloryBeActive = currentStep is Step.Prayer && currentStep.id == "glory_be" && currentStep.decade == decade
        SpecialBead(
            label = "المجد",
            isActive = isGloryBeActive,
            isCompleted = false,
            onClick = {
                val index = steps.indexOfFirst {
                    it is Step.Prayer && it.id == "glory_be" && it.decade == decade
                }
                if (index >= 0) onJumpToStep(index)
            }
        )
    }
}

@Composable
private fun SingleHailMaryBead(
    beadNum: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    val beadSize by animateDpAsState(
        targetValue = if (isActive) 26.dp else 19.dp,
        animationSpec = spring(),
        label = "beadSize"
    )

    val beadColor by animateColorAsState(
        targetValue = when {
            isActive -> BeadActive
            isCompleted -> BeadCompleted
            else -> inactiveColor
        },
        label = "beadColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.padding(horizontal = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp), // Touch target envelope >= 34 with container padding
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(beadSize)
                    .clip(CircleShape)
                    .background(beadColor)
                    .border(
                        width = if (isActive) 2.dp else 0.dp,
                        color = if (isActive) SacredGoldBright else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    Text(
                        text = "$beadNum",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialBead(
    label: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val beadColor by animateColorAsState(
        targetValue = when {
            isActive -> SacredGoldBright
            isCompleted -> SacredGold.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        },
        label = "specialBeadColor"
    )

    Box(
        modifier = Modifier
            .size(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (isActive) 26.dp else 22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(beadColor)
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) SacredGold else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (label == "أبانا") "✝" else "★",
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) Color.Black else Color.White,
                fontSize = 12.sp
            )
        }
    }
}
