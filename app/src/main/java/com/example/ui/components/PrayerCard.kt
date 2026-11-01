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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.data.ResolvedStepContent
import com.example.data.SectionItem
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright
import com.example.ui.theme.SacredGoldContainer

@Composable
fun PrayerCard(
    content: ResolvedStepContent,
    fontScale: Float,
    onCardClick: () -> Unit,
    onToggleFontSize: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prayer_card")
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Mystery Fruit Banner + Quick Font Size Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onToggleFontSize != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable(onClick = onToggleFontSize)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "تغيير حجم الخط",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "حجم الخط",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.size(24.dp))
                }

                if (!content.mysteryFruit.isNullOrEmpty()) {
                    Surface(
                        color = SacredGold.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = SacredGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ثمرة السر: ${content.mysteryFruit}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Step Title
            Text(
                text = content.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = (21 * fontScale).sp,
                lineHeight = (28 * fontScale).sp
            )

            // Subtitle
            if (!content.subtitle.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Body Content (Section Items or Main Prayer Text)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.End
            ) {
                if (content.sectionItems != null) {
                    SectionItemsList(items = content.sectionItems, fontScale = fontScale)
                } else if (!content.mainText.isNullOrEmpty()) {
                    Text(
                        text = content.mainText,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (19 * fontScale).sp,
                        lineHeight = (32 * fontScale).sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "المس البطاقة للانتقال للحبة التالية",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SectionItemsList(
    items: List<SectionItem>,
    fontScale: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        items.forEach { item ->
            when (item.type) {
                "title" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontSize = (18 * fontScale).sp
                        )
                    }
                }
                "leader" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right,
                            fontSize = (18 * fontScale).sp,
                            lineHeight = (30 * fontScale).sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "المحتفل: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "response" -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Right,
                            fontSize = (18 * fontScale).sp,
                            lineHeight = (30 * fontScale).sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "الشعب: ",
                            style = MaterialTheme.typography.labelMedium,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "hymn" -> {
                    Surface(
                        color = SacredGoldContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                fontSize = (17 * fontScale).sp,
                                lineHeight = (28 * fontScale).sp,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "ترتيل",
                                tint = SacredGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Right,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (18 * fontScale).sp,
                        lineHeight = (30 * fontScale).sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
