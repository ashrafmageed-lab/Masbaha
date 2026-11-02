package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.History
import com.example.domain.Step
import com.example.ui.components.CircularRosaryCounter
import com.example.ui.components.ClosingPrayersDialog
import com.example.ui.components.FontSizeControlPanel
import com.example.ui.components.MysterySelectorDialog
import com.example.ui.components.PrayerCard
import com.example.ui.components.PrayerHistoryDialog
import com.example.ui.components.PrayerIntentionDialog
import com.example.ui.components.PrayerStatsScreen
import com.example.ui.components.RosaryBeadsVisualizer
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TodayMysteriesBanner
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright

enum class CounterDisplayMode {
    CIRCULAR, // عداد الحبات الدائري
    LINEAR    // المسبحة الخطية
}

/**
 * Main PrayerScreen containing:
 * - Circular Rosary Bead Counter (عداد الحبات الدائري)
 * - Navigation buttons between beads (أزرار التنقل بين الحبات)
 * - Tactile feedback upon every bead press via LocalHapticFeedback and device Vibrator
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    var displayMode by rememberSaveable { mutableStateOf(CounterDisplayMode.CIRCULAR) }
    var showFontSizePanel by rememberSaveable { mutableStateOf(false) }

    // If Stats Screen is requested, render full-screen PrayerStatsScreen
    if (uiState.showStatsScreen) {
        val stats = uiState.statistics ?: viewModel.getStatistics()
        PrayerStatsScreen(
            statistics = stats,
            onNavigateBack = { viewModel.setShowStatsScreen(false) },
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_screen"),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "المسبحة الوردية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Live Prayer Timer Pill (Clickable to pause/resume)
                            Surface(
                                color = if (uiState.isTimerRunning) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                                else SacredGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .clickable {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.toggleTimer()
                                    }
                                    .testTag("prayer_timer_pill")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isTimerRunning) Icons.Default.Timer else Icons.Default.Pause,
                                        contentDescription = if (uiState.isTimerRunning) "المؤقت يعمل - اضغط للإيقاف المؤقت" else "المؤقت متوقف مؤقتاً - اضغط للاستئناف",
                                        tint = if (uiState.isTimerRunning) MaterialTheme.colorScheme.primary else SacredGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = formatStopwatch(uiState.elapsedSeconds),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isTimerRunning) MaterialTheme.colorScheme.primary else SacredGold,
                                        fontSize = 11.sp
                                    )
                                    if (!uiState.isTimerRunning && !uiState.isCompleted) {
                                        Text(
                                            text = "(إيقاف)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SacredGold,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowMysterySelector(true)
                            },
                            modifier = Modifier.testTag("mystery_selector_button")
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "تغيير السر",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = uiState.currentMysterySet.titleAr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // Closing Prayers Quick Access Button (صلوات الختام: السلسلة وصلاة الختام)
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowClosingPrayersDialog(true)
                            },
                            modifier = Modifier.testTag("closing_prayers_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "صلوات الختام (السلسلة والختام)",
                                tint = SacredGold
                            )
                        }

                        // Prayer Intention Button
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowIntentionDialog(true)
                            },
                            modifier = Modifier.testTag("prayer_intention_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "نية الصلاة",
                                tint = if (uiState.prayerIntention != null) SacredGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Prayer History & Streak Button
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowHistoryDialog(true)
                            },
                            modifier = Modifier.testTag("prayer_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "سجل صلواتي",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Prayer Statistics & Consistency Button (مخطط بياني للمواظبة)
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowStatsScreen(true)
                            },
                            modifier = Modifier.testTag("prayer_stats_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "إحصائيات المواظبة",
                                tint = SacredGold
                            )
                        }

                        // Toggle Font Size Slider Panel
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showFontSizePanel = !showFontSizePanel
                            },
                            modifier = Modifier.testTag("font_size_toggle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "التحكم في حجم الخط",
                                tint = if (showFontSizePanel) SacredGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Toggle between Circular and Linear display mode
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                displayMode = if (displayMode == CounterDisplayMode.CIRCULAR) {
                                    CounterDisplayMode.LINEAR
                                } else {
                                    CounterDisplayMode.CIRCULAR
                                }
                            },
                            modifier = Modifier.testTag("toggle_counter_mode_button")
                        ) {
                            Icon(
                                imageVector = if (displayMode == CounterDisplayMode.CIRCULAR) {
                                    Icons.Default.LinearScale
                                } else {
                                    Icons.Default.Circle
                                },
                                contentDescription = if (displayMode == CounterDisplayMode.CIRCULAR) "عرض شريطي" else "عداد دائري",
                                tint = SacredGold
                            )
                        }

                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowSettings(true)
                            },
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setShowResetDialog(true)
                            },
                            modifier = Modifier.testTag("reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "إعادة الصلاة",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Sequence progress indicator
                val progress = if (uiState.steps.isNotEmpty()) {
                    (uiState.currentStepIndex + 1).toFloat() / uiState.steps.size.toFloat()
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = SacredGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        },
        bottomBar = {
            // Bead Navigation Controls with Haptic Feedback
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Bead Button (السابق)
                        OutlinedButton(
                            onClick = {
                                if (uiState.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                viewModel.prevStep()
                            },
                            enabled = uiState.currentStepIndex > 0 || uiState.isCompleted,
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("prev_step_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "الحبة السابقة"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "السابق",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Bead status pill in center
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val beadLabel = when (val step = uiState.resolvedStep.step) {
                                is Step.Prayer -> when (step.id) {
                                    "hail_mary" -> "حبة ${step.bead} من ١٠"
                                    "our_father" -> "صلاة أبانا"
                                    "glory_be" -> "صلاة المجدلة"
                                    else -> "صلاة"
                                }
                                is Step.MysteryHeader -> "تأمل السر ${step.n}"
                                else -> "صلاة الوردية"
                            }
                            Text(
                                text = if (uiState.isCompleted) "اكتملت الصلاة" else beadLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = SacredGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = formatStopwatch(uiState.elapsedSeconds),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }

                                if (uiState.hapticsEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.Vibration,
                                        contentDescription = null,
                                        tint = SacredGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Text(
                                    text = "${uiState.currentStepIndex + 1} / ${uiState.steps.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Next Bead Navigation Button (زر التنقل بين الحبات)
                        Button(
                            onClick = {
                                // Trigger Compose HapticFeedback directly
                                if (uiState.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                viewModel.nextStep(context)
                            },
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("next_step_button"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = if (uiState.currentStepIndex == uiState.steps.size - 1) "خاتمة الصلاة" else "الحبة التالية",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "الحبة التالية"
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Auto-detected Today's Rosary Mysteries Banner with explanatory texts
            TodayMysteriesBanner(
                todayInfo = uiState.todayMysteryInfo,
                activeMysterySet = uiState.currentMysterySet,
                onJumpToDecade = { decade ->
                    val targetIndex = uiState.steps.indexOfFirst {
                        it is Step.MysteryHeader && it.n == decade
                    }
                    if (targetIndex >= 0) {
                        viewModel.jumpToStep(targetIndex, context)
                    }
                }
            )

            // Active Prayer Intention Banner
            if (!uiState.prayerIntention.isNullOrBlank()) {
                Surface(
                    color = SacredGold.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setShowIntentionDialog(true) }
                        .testTag("prayer_intention_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تعديل النية",
                            style = MaterialTheme.typography.labelSmall,
                            color = SacredGold,
                            fontSize = 11.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "نية الصلاة: ${uiState.prayerIntention}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = SacredGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Animated Font Size Control Panel (Slider + A-/A+ buttons + Presets)
            AnimatedVisibility(
                visible = showFontSizePanel,
                enter = androidx.compose.animation.expandVertically() + fadeIn(),
                exit = androidx.compose.animation.shrinkVertically() + fadeOut()
            ) {
                FontSizeControlPanel(
                    fontScale = uiState.fontScale,
                    onFontScaleChange = { viewModel.setFontScale(it) },
                    onClose = { showFontSizePanel = false }
                )
            }

            // Main Active Prayer Card or Completion Screen
            if (uiState.isCompleted) {
                CompletionCard(
                    mysterySet = uiState.currentMysterySet,
                    elapsedSeconds = uiState.elapsedSeconds,
                    onPrayAgain = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.resetPrayer()
                    },
                    onChangeMystery = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.setShowMysterySelector(true)
                    },
                    onViewStats = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.setShowStatsScreen(true)
                    },
                    onOpenClosingPrayers = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.setShowClosingPrayersDialog(true)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                when (displayMode) {
                    CounterDisplayMode.CIRCULAR -> {
                        // 1. Circular Beads Counter or Compact Strip
                        if (!uiState.isExpandedReadingMode) {
                            CircularRosaryCounter(
                                currentStep = uiState.resolvedStep.step,
                                currentStepIndex = uiState.currentStepIndex,
                                steps = uiState.steps,
                                onJumpToStep = { index ->
                                    if (uiState.hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    viewModel.jumpToStep(index, context)
                                },
                                onCenterClick = {
                                    if (uiState.hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    viewModel.nextStep(context)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.toggleReadingMode()
                                    }
                                    .testTag("compact_beads_bar"),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "المس لإظهار العداد الدائري",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SacredGold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "وضع القراءة الموسع (حبة ${uiState.resolvedStep.currentBead} من ١٠)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // 2. Prayer Card below the circular counter (taking maximum vertical space)
                        PrayerCard(
                            content = uiState.resolvedStep,
                            fontScale = uiState.fontScale,
                            onCardClick = {
                                if (uiState.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                viewModel.nextStep(context)
                            },
                            onToggleFontSize = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showFontSizePanel = !showFontSizePanel
                            },
                            onToggleReadingMode = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleReadingMode()
                            },
                            isReadingModeExpanded = uiState.isExpandedReadingMode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    CounterDisplayMode.LINEAR -> {
                        // 1. Horizontal Rosary Decades & Beads Visualizer
                        if (!uiState.isExpandedReadingMode) {
                            RosaryBeadsVisualizer(
                                currentStep = uiState.resolvedStep.step,
                                currentStepIndex = uiState.currentStepIndex,
                                steps = uiState.steps,
                                onJumpToStep = { index ->
                                    if (uiState.hapticsEnabled) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                    viewModel.jumpToStep(index, context)
                                }
                            )
                        }

                        // 2. Expanded Prayer Card
                        PrayerCard(
                            content = uiState.resolvedStep,
                            fontScale = uiState.fontScale,
                            onCardClick = {
                                if (uiState.hapticsEnabled) {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                viewModel.nextStep(context)
                            },
                            onToggleFontSize = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                showFontSizePanel = !showFontSizePanel
                            },
                            onToggleReadingMode = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleReadingMode()
                            },
                            isReadingModeExpanded = uiState.isExpandedReadingMode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (uiState.showMysterySelector) {
        MysterySelectorDialog(
            currentMystery = uiState.currentMysterySet,
            todayMystery = uiState.todayMysterySet,
            onSelectMystery = { mystery ->
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.selectMystery(mystery)
            },
            onDismiss = { viewModel.setShowMysterySelector(false) }
        )
    }

    if (uiState.showSettings) {
        SettingsDialog(
            fontScale = uiState.fontScale,
            onFontScaleChange = { viewModel.setFontScale(it) },
            hapticsEnabled = uiState.hapticsEnabled,
            onHapticsChange = { viewModel.setHapticsEnabled(it) },
            soundEnabled = uiState.soundEnabled,
            onSoundChange = { viewModel.setSoundEnabled(it) },
            includeChain = uiState.includeChain,
            onIncludeChainChange = { viewModel.setIncludeChain(it) },
            darkThemeOverride = uiState.isDarkThemeOverride,
            onDarkThemeChange = { viewModel.setThemeOverride(it) },
            reminderEnabled = uiState.isReminderEnabled,
            onReminderEnabledChange = { viewModel.setReminderEnabled(it) },
            reminderHour = uiState.reminderHour,
            reminderMinute = uiState.reminderMinute,
            onReminderTimeChange = { h, m -> viewModel.setReminderTime(h, m) },
            onSendTestNotification = { viewModel.sendTestNotification() },
            onDismiss = { viewModel.setShowSettings(false) }
        )
    }

    if (uiState.showHistoryDialog) {
        PrayerHistoryDialog(
            historyList = uiState.completedPrayers,
            streakDays = uiState.streakDays,
            onDismiss = { viewModel.setShowHistoryDialog(false) },
            onOpenStats = { viewModel.setShowStatsScreen(true) }
        )
    }

    if (uiState.showIntentionDialog) {
        PrayerIntentionDialog(
            currentIntention = uiState.prayerIntention,
            onSaveIntention = { viewModel.setPrayerIntention(it) },
            onDismiss = { viewModel.setShowIntentionDialog(false) }
        )
    }

    if (uiState.showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowResetDialog(false) },
            title = {
                Text(
                    text = "إعادة بدء الصلاة؟",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "هل ترغب في العودة إلى أول صلاة والبدء من جديد؟",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.resetPrayer()
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text(text = "نعم، ابدأ من جديد")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowResetDialog(false) }) {
                    Text(text = "إلغاء")
                }
            }
        )
    }

    if (uiState.showClosingPrayersDialog) {
        ClosingPrayersDialog(
            closingPrayers = viewModel.getClosingPrayers(),
            fontScale = uiState.fontScale,
            onDismiss = { viewModel.setShowClosingPrayersDialog(false) }
        )
    }
}

/** Backward-compatibility alias for RosaryScreen */
@Composable
fun RosaryScreen(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    PrayerScreen(viewModel = viewModel, modifier = modifier)
}

@Composable
private fun CompletionCard(
    mysterySet: com.example.domain.MysterySet,
    elapsedSeconds: Long,
    onPrayAgain: () -> Unit,
    onChangeMystery: () -> Unit,
    onViewStats: () -> Unit = {},
    onOpenClosingPrayers: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("completion_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(76.dp),
                shape = RoundedCornerShape(38.dp),
                color = SacredGold.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "اكتملت الصلاة",
                        tint = SacredGold,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "تقبل الله صلاتك",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "لقد أكملت صلاة الوردية المقدسة مع تأملات ${mysterySet.titleAr}.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "«السلام عليكِ يا ممتلئة نعمة، الرب معكِ»",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = SacredGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prominent Elapsed Duration Card
            Surface(
                color = SacredGold.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "المدة المستغرقة",
                        tint = SacredGold,
                        modifier = Modifier.size(28.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "المدة المستغرقة في الصلاة",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatDurationAr(elapsedSeconds),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Closing Prayers Primary Action Button
            Button(
                onClick = onOpenClosingPrayers,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("completion_closing_prayers_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SacredGold
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تلاوة صلوات الختام (السلسلة والختام)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onChangeMystery,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "سر آخر", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = onPrayAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "إعادة الصلاة", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onViewStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("completion_view_stats_button")
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "عرض إحصائيات المواظبة", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
