package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright
import kotlin.math.roundToInt

@Composable
fun FontSizeControlPanel(
    fontScale: Float,
    onFontScaleChange: (Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("font_size_control_panel"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = SacredGold.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClose()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_font_panel_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق شريط الخط",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = SacredGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${(fontScale * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "التحكم في حجم خط القراءة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Slider with A- and A+ Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // A- Button (Decrease)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            val newScale = (fontScale - 0.15f).coerceAtLeast(0.85f)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFontScaleChange(newScale)
                        }
                        .testTag("decrease_font_size_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A-",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Interactive Slider
                Slider(
                    value = fontScale,
                    onValueChange = { newScale ->
                        onFontScaleChange(newScale)
                    },
                    onValueChangeFinished = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    valueRange = 0.85f..1.65f,
                    steps = 7,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("font_size_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = SacredGoldBright,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // A+ Button (Increase)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            val newScale = (fontScale + 0.15f).coerceAtMost(1.65f)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onFontScaleChange(newScale)
                        }
                        .testTag("increase_font_size_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A+",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Quick Preset Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    0.85f to "صغير",
                    1.0f to "عادي",
                    1.2f to "كبير",
                    1.45f to "كبير جداً"
                ).forEach { (scale, label) ->
                    val isSelected = kotlin.math.abs(fontScale - scale) < 0.08f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onFontScaleChange(scale)
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
