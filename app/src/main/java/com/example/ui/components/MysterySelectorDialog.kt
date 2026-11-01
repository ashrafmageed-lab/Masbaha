package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.MysterySet
import com.example.ui.theme.SacredGold

@Composable
fun MysterySelectorDialog(
    currentMystery: MysterySet,
    todayMystery: MysterySet,
    onSelectMystery: (MysterySet) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("mystery_selector_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إغلاق",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "اختيار أسرار الوردية",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "تحدد الكنيسة سراً لكل يوم، ويمكنك اختيار أي سر للصلاة في أي وقت:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                MysterySet.entries.forEach { mystery ->
                    val isSelected = mystery == currentMystery
                    val isToday = mystery == todayMystery

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mystery_item_${mystery.key}")
                            .clickable { onSelectMystery(mystery) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        ),
                        border = if (isSelected) {
                            CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                            )
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "محدد",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.size(20.dp))
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (isToday) {
                                        Surface(
                                            color = SacredGold.copy(alpha = 0.18f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "سر اليوم",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SacredGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = mystery.titleAr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = mystery.subtitleAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_mystery_dialog_button")
            ) {
                Text(text = "تم")
            }
        }
    )
}
