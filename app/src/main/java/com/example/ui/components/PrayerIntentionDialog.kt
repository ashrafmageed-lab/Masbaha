package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SacredGold

@Composable
fun PrayerIntentionDialog(
    currentIntention: String?,
    onSaveIntention: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var textValue by remember { mutableStateOf(currentIntention ?: "") }

    val suggestions = listOf(
        "من أجل السلام في العالم وإيقاف الحروب",
        "من أجل شفاء المرضى وتعزية المتألمين",
        "من أجل حماية العائلة وبركة الأبناء",
        "من أجل راحة نفوس جميع الموتى المؤمنين",
        "شكر وحمد لله على عطاياه ونعمه الغزيرة"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("prayer_intention_dialog"),
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "نية الصلاة وتكريس الوردية",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = SacredGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "حدد نيتك الخاصة لتقديم هذه الوردية المقدسة من أجلها:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("intention_text_field"),
                    label = { Text("اكتب نية الصلاة هنا...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    text = "مقترحات سريعة:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    suggestions.forEach { suggestion ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { textValue = suggestion }
                        ) {
                            Text(
                                text = "• $suggestion",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveIntention(textValue.trim().ifEmpty { null }) },
                modifier = Modifier.testTag("save_intention_button")
            ) {
                Text(text = "حفظ النية")
            }
        },
        dismissButton = {
            if (!currentIntention.isNullOrBlank()) {
                OutlinedButton(
                    onClick = { onSaveIntention(null) },
                    modifier = Modifier.testTag("clear_intention_button")
                ) {
                    Text(text = "مسح النية")
                }
            }
        }
    )
}
