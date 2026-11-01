package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.example.data.TodayMysteryInfo
import com.example.domain.MysterySet
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright

@Composable
fun TodayMysteriesBanner(
    todayInfo: TodayMysteryInfo,
    activeMysterySet: MysterySet,
    onJumpToDecade: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val isCurrentSetMatchingToday = todayInfo.mysterySet == activeMysterySet

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("today_mysteries_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isCurrentSetMatchingToday) SacredGold.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Detection Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        isExpanded = !isExpanded
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expand Icon & Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "إخفاء التأملات" else "عرض التأملات",
                        tint = SacredGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (isExpanded) "إخفاء" else "تأملات الأسرار (٥)",
                        style = MaterialTheme.typography.labelSmall,
                        color = SacredGold,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Date & Automatic Detection Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = SacredGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "كشف تلقائي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SacredGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "اليوم ${todayInfo.dayNameAr}: ${todayInfo.mysterySet.titleAr}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = todayInfo.mysterySet.subtitleAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }

                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Explanatory Intro Text
            if (todayInfo.introText.isNotEmpty()) {
                Text(
                    text = todayInfo.introText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Expanded 5 Mysteries List with Explanatory Texts and Fruits
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = spring()) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring()) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        thickness = 1.dp
                    )

                    todayInfo.mysteries.forEach { mystery ->
                        val ordinalName = when (mystery.n) {
                            1 -> "السر الأول"
                            2 -> "السر الثاني"
                            3 -> "السر الثالث"
                            4 -> "السر الرابع"
                            5 -> "السر الخامس"
                            else -> "السر ${mystery.n}"
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Action to jump to this decade
                                    OutlinedButton(
                                        onClick = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onJumpToDecade(mystery.n)
                                        },
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                            horizontal = 8.dp,
                                            vertical = 2.dp
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(text = "ابدأ به", fontSize = 10.sp)
                                    }

                                    // Mystery Number & Fruit Badge
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            color = SacredGold.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "الفضيلة: ${mystery.fruit}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SacredGold,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 11.sp
                                            )
                                        }

                                        Text(
                                            text = ordinalName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Explanatory Text of the Mystery
                                Text(
                                    text = mystery.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Right,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
