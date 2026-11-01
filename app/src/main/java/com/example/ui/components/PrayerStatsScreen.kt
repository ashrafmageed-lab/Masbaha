package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DayOfWeekStat
import com.example.data.RosaryStatistics
import com.example.ui.formatDurationAr
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldBright
import java.time.DayOfWeek

enum class StatsTimeframe {
    THIS_WEEK,
    ALL_TIME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerStatsScreen(
    statistics: RosaryStatistics,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hardware back navigation
    BackHandler(onBack = onNavigateBack)

    var timeframe by remember { mutableStateOf(StatsTimeframe.THIS_WEEK) }
    var selectedDay by remember {
        mutableStateOf(statistics.dayStats.find { it.isToday } ?: statistics.dayStats.firstOrNull())
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("prayer_stats_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "إحصائيات المواظبة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "متابعة صلوات الوردية عبر أيام الأسبوع",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("stats_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "العودة إلى الصلاة",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeframe Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stats_timeframe_selector"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = timeframe == StatsTimeframe.THIS_WEEK,
                    onClick = { timeframe = StatsTimeframe.THIS_WEEK },
                    label = {
                        Text(
                            text = "هذا الأسبوع",
                            fontWeight = if (timeframe == StatsTimeframe.THIS_WEEK) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        if (timeframe == StatsTimeframe.THIS_WEEK) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("timeframe_this_week"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                FilterChip(
                    selected = timeframe == StatsTimeframe.ALL_TIME,
                    onClick = { timeframe = StatsTimeframe.ALL_TIME },
                    label = {
                        Text(
                            text = "جميع الأوقات",
                            fontWeight = if (timeframe == StatsTimeframe.ALL_TIME) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        if (timeframe == StatsTimeframe.ALL_TIME) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    modifier = Modifier.testTag("timeframe_all_time"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Streak & Encouragement Card
            StreakMotivationCard(
                streakDays = statistics.streakDays,
                activeDaysThisWeek = statistics.activeDaysThisWeek,
                totalThisWeek = statistics.totalThisWeek
            )

            // Weekly Bar Chart Card
            WeeklyBarChartCard(
                dayStats = statistics.dayStats,
                timeframe = timeframe,
                selectedDay = selectedDay,
                onSelectDay = { selectedDay = it }
            )

            // Key Summary Metrics Grid
            SummaryMetricsSection(
                statistics = statistics,
                timeframe = timeframe
            )

            // Mystery Breakdown Card
            MysteryBreakdownCard(
                mysteryCounts = statistics.mysteryCounts,
                totalPrayers = statistics.totalAllTime
            )

            // Spiritual Quote Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SacredGold.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(SacredGold.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SacredGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "«المسبحة هي سلاح هذه الأزمنة، ومفتاح الدخول إلى عمق أسرار حب المسيح.»",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— القديس بادري بيو",
                            style = MaterialTheme.typography.labelSmall,
                            color = SacredGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StreakMotivationCard(
    streakDays: Int,
    activeDaysThisWeek: Int,
    totalThisWeek: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_motivation_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(SacredGoldBright, SacredGold)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "سلسلة الصلاة",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "سلسلة المواظبة اليومية",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (streakDays > 0) "$streakDays ${if (streakDays == 1) "يوم متواصل" else if (streakDays == 2) "يومان متواصلان" else "أيام متتالية"}" else "ابدأ سلسلتك اليوم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SacredGold.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$totalThisWeek صلوات هذا الأسبوع",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Weekly adherence meter
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "معدل المواظبة الأسبوعية: $activeDaysThisWeek من 7 أيام",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(activeDaysThisWeek * 100) / 7}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LinearProgressIndicator(
                    progress = { (activeDaysThisWeek / 7f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SacredGold,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

            // Motivational text based on streak
            val motivationMessage = when {
                streakDays >= 7 -> "«يا لها من نعمة عظيمة! لقد أتممت أسبوعاً كاملاً من الصلاة المتواصلة. استمر في رفع صلواتك مع مريم العذراء.»"
                streakDays >= 3 -> "«مواظبة مباركة! إن صلاتك المستمرة تثمر سلاماً داخلياً ونعماً وفيرة.»"
                streakDays in 1..2 -> "«بداية طيبة جداً! واصل الصلاة غداً للحفاظ على سلسلة مواظبتك.»"
                else -> "«ابدأ صلاة الوردية اليوم لتضع أول حبة في سلسلة مواظبتك الروحية المباركة.»"
            }

            Text(
                text = motivationMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun WeeklyBarChartCard(
    dayStats: List<DayOfWeekStat>,
    timeframe: StatsTimeframe,
    selectedDay: DayOfWeekStat?,
    onSelectDay: (DayOfWeekStat) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxCount = remember(dayStats, timeframe) {
        val max = dayStats.maxOfOrNull {
            if (timeframe == StatsTimeframe.THIS_WEEK) it.countThisWeek else it.countAllTime
        } ?: 1
        if (max > 0) max else 1
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_bar_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (timeframe == StatsTimeframe.THIS_WEEK) "مرات إتمام الوردية هذا الأسبوع" else "توزيع إتمام الوردية (كافة الأوقات)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "الأحد - السبت",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // The Chart Canvas / Column of Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("weekly_bar_chart"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dayStats.forEach { stat ->
                    val count = if (timeframe == StatsTimeframe.THIS_WEEK) stat.countThisWeek else stat.countAllTime
                    val isSelected = selectedDay?.dayOfWeek == stat.dayOfWeek
                    val isHighest = count > 0 && count == maxCount

                    DayBarItem(
                        stat = stat,
                        count = count,
                        maxCount = maxCount,
                        isSelected = isSelected,
                        isHighest = isHighest,
                        onClick = { onSelectDay(stat) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // Inspector detail for selected day
            if (selectedDay != null) {
                val selectedCount = if (timeframe == StatsTimeframe.THIS_WEEK) selectedDay.countThisWeek else selectedDay.countAllTime
                SelectedDayDetailCard(
                    dayStat = selectedDay,
                    count = selectedCount,
                    timeframe = timeframe
                )
            }
        }
    }
}

@Composable
private fun DayBarItem(
    stat: DayOfWeekStat,
    count: Int,
    maxCount: Int,
    isSelected: Boolean,
    isHighest: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeightDp = remember(count, maxCount) {
        if (count == 0) {
            8.dp
        } else {
            val fraction = (count.toFloat() / maxCount.toFloat()).coerceIn(0.12f, 1f)
            (fraction * 110).dp
        }
    }

    val animatedHeight by animateDpAsState(
        targetValue = barHeightDp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "barHeight_${stat.dayOfWeek}"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .testTag("bar_day_${stat.dayOfWeek.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Star or Crown for highest day
        if (isHighest) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "الأكثر نشاطاً",
                tint = SacredGold,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Count Label above bar
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Animated Bar
        Box(
            modifier = Modifier
                .width(if (isSelected) 24.dp else 20.dp)
                .height(animatedHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(
                    brush = when {
                        stat.isToday -> Brush.verticalGradient(
                            listOf(SacredGoldBright, SacredGold)
                        )
                        count > 0 -> Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                        else -> Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                )
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = if (stat.isToday) SacredGold else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                        )
                    } else Modifier
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Day Name
        Text(
            text = stat.dayNameShort,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (stat.isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
            color = if (stat.isToday) SacredGold else if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        // Today Indicator Badge
        if (stat.isToday) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(5.dp)
                    .background(SacredGold, CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.height(7.dp))
        }
    }
}

@Composable
private fun SelectedDayDetailCard(
    dayStat: DayOfWeekStat,
    count: Int,
    timeframe: StatsTimeframe,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = dayStat.dayNameFull,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (dayStat.isToday) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SacredGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "اليوم",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "السر المخصص: ${dayStat.mysteryName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (count > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = if (count > 0) "$count ${if (count == 1) "صلاة مكتملة" else if (count == 2) "صلاتان مكتملتان" else "صلوات مكتملة"}" else "لم تُتلَ بعد",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (count > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetricsSection(
    statistics: RosaryStatistics,
    timeframe: StatsTimeframe,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "ملخص المواظبة الروحية",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "إجمالي الورديات",
                value = if (timeframe == StatsTimeframe.THIS_WEEK) statistics.totalThisWeek.toString() else statistics.totalAllTime.toString(),
                unit = "وردية",
                icon = Icons.Default.CheckCircle,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "أكثر الأيام نشاطاً",
                value = statistics.bestDayName ?: "—",
                unit = if (statistics.bestDayName != null) "في الصلاة" else "لا بيانات",
                icon = Icons.Default.EmojiEvents,
                accentColor = SacredGold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "سلسلة الأيام",
                value = "${statistics.streakDays}",
                unit = "أيام متتالية",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = SacredGold,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "إجمالي وقت الصلاة",
                value = formatSummaryDuration(statistics.totalDurationSeconds),
                unit = "لحظات مقدسة",
                icon = Icons.Default.Schedule,
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun MysteryBreakdownCard(
    mysteryCounts: Map<String, Int>,
    totalPrayers: Int,
    modifier: Modifier = Modifier
) {
    val joyfulCount = mysteryCounts["joyful"] ?: 0
    val sorrowfulCount = mysteryCounts["sorrowful"] ?: 0
    val gloriousCount = mysteryCounts["glorious"] ?: 0
    val luminousCount = mysteryCounts["luminous"] ?: 0

    val maxVal = maxOf(joyfulCount, sorrowfulCount, gloriousCount, luminousCount, 1)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mystery_breakdown_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "توزيع أسرار الوردية المصلّاة",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            MysteryProgressRow(
                title = "أسرار الفرح (الاثنين والسبت)",
                count = joyfulCount,
                maxCount = maxVal,
                color = MaterialTheme.colorScheme.primary
            )

            MysteryProgressRow(
                title = "أسرار الحزن (الثلاثاء والجمعة)",
                count = sorrowfulCount,
                maxCount = maxVal,
                color = Color(0xFF8E24AA) // Purple
            )

            MysteryProgressRow(
                title = "أسرار المجد (الأربعاء والأحد)",
                count = gloriousCount,
                maxCount = maxVal,
                color = SacredGold
            )

            MysteryProgressRow(
                title = "أسرار النور (الخميس)",
                count = luminousCount,
                maxCount = maxVal,
                color = Color(0xFF00897B) // Teal
            )
        }
    }
}

@Composable
private fun MysteryProgressRow(
    title: String,
    count: Int,
    maxCount: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$count ${if (count == 1) "وردية" else "ورديات"}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = { if (maxCount > 0) (count.toFloat() / maxCount.toFloat()).coerceIn(0f, 1f) else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

private fun formatSummaryDuration(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    return when {
        hrs > 0 -> "$hrs س و $mins د"
        mins > 0 -> "$mins دقيقة"
        seconds > 0 -> "$seconds ثانية"
        else -> "0 دقيقة"
    }
}
