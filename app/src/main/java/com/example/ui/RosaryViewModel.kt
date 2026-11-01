package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ResolvedStepContent
import com.example.data.RosaryRepository
import com.example.domain.MysterySet
import com.example.domain.Step
import com.example.domain.buildSequence
import com.example.domain.mysterySetFor
import com.example.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

import com.example.data.CompletedPrayerRecord
import com.example.data.PrayerHistoryManager
import com.example.data.RosaryStatistics
import com.example.util.NotificationReminderManager
import com.example.util.SoundHelper

data class RosaryUiState(
    val currentMysterySet: MysterySet,
    val todayMysterySet: MysterySet,
    val todayMysteryInfo: com.example.data.TodayMysteryInfo,
    val currentMysterySetData: com.example.data.MysterySetData?,
    val steps: List<Step>,
    val currentStepIndex: Int,
    val resolvedStep: ResolvedStepContent,
    val fontScale: Float = 1.0f, // 0.9f, 1.0f, 1.15f, 1.35f, 1.55f
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val prayerIntention: String? = null,
    val completedPrayers: List<CompletedPrayerRecord> = emptyList(),
    val streakDays: Int = 0,
    val includeChain: Boolean = true,
    val isCompleted: Boolean = false,
    val showMysterySelector: Boolean = false,
    val showSettings: Boolean = false,
    val showResetDialog: Boolean = false,
    val showTodayMysteriesGuide: Boolean = false,
    val showHistoryDialog: Boolean = false,
    val showIntentionDialog: Boolean = false,
    val showStatsScreen: Boolean = false,
    val statistics: RosaryStatistics? = null,
    val isReminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val elapsedSeconds: Long = 0L,
    val isTimerRunning: Boolean = true,
    val isDarkThemeOverride: Boolean? = null // null: system, true: dark, false: light
)

fun formatDurationAr(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        hrs > 0 -> "$hrs ساعة و $mins دقيقة و $secs ثانية"
        mins > 0 -> "$mins دقيقة و $secs ثانية"
        else -> "$secs ثانية"
    }
}

fun formatStopwatch(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}

class RosaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RosaryRepository(application.applicationContext)
    private val historyManager = PrayerHistoryManager(application.applicationContext)
    private val reminderManager = NotificationReminderManager(application.applicationContext)
    private val today = LocalDate.now()
    private val initialMystery = mysterySetFor(today)

    private val _uiState = MutableStateFlow(createInitialState(initialMystery))
    val uiState: StateFlow<RosaryUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.isTimerRunning && !_uiState.value.isCompleted) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun toggleTimer() {
        _uiState.update { it.copy(isTimerRunning = !it.isTimerRunning) }
    }

    private fun createInitialState(mysterySet: MysterySet, includeChain: Boolean = true): RosaryUiState {
        val sequence = buildSequence(mysterySet, includeChain)
        val initialResolved = repository.resolveStep(
            step = sequence[0],
            currentMysterySet = mysterySet,
            stepIndex = 0,
            totalSteps = sequence.size
        )
        val todayInfo = repository.getTodayMysteryInfo(today)
        val currentSetData = repository.getMysterySetData(mysterySet)
        val savedIntention = historyManager.getSavedIntention()
        val historyList = historyManager.getCompletedPrayers()
        val streak = historyManager.calculateStreakDays()
        val stats = historyManager.getStatistics()
        val isReminderOn = reminderManager.isReminderEnabled()
        val (remHour, remMin) = reminderManager.getReminderTime()

        return RosaryUiState(
            currentMysterySet = mysterySet,
            todayMysterySet = initialMystery,
            todayMysteryInfo = todayInfo,
            currentMysterySetData = currentSetData,
            prayerIntention = savedIntention,
            completedPrayers = historyList,
            streakDays = streak,
            statistics = stats,
            isReminderEnabled = isReminderOn,
            reminderHour = remHour,
            reminderMinute = remMin,
            steps = sequence,
            currentStepIndex = 0,
            resolvedStep = initialResolved,
            includeChain = includeChain
        )
    }

    fun nextStep(context: Context) {
        val currentState = _uiState.value
        val nextIndex = currentState.currentStepIndex + 1

        if (nextIndex < currentState.steps.size) {
            val nextStep = currentState.steps[nextIndex]
            val resolved = repository.resolveStep(
                step = nextStep,
                currentMysterySet = currentState.currentMysterySet,
                stepIndex = nextIndex,
                totalSteps = currentState.steps.size
            )

            // Audio & Haptic Feedback
            val isDecadeEndpoint = nextStep is Step.Prayer && nextStep.id == "glory_be"

            if (currentState.hapticsEnabled) {
                if (isDecadeEndpoint) {
                    HapticHelper.vibrateDecadeComplete(context)
                } else {
                    HapticHelper.vibrateBead(context)
                }
            }

            if (currentState.soundEnabled) {
                if (isDecadeEndpoint) {
                    SoundHelper.playDecadeChime()
                } else {
                    SoundHelper.playBeadChime()
                }
            }

            _uiState.update {
                it.copy(
                    currentStepIndex = nextIndex,
                    resolvedStep = resolved,
                    isCompleted = false
                )
            }
        } else {
            // Reached the end: play celebration chime and save to history
            if (currentState.hapticsEnabled) {
                HapticHelper.vibrateDecadeComplete(context)
            }
            if (currentState.soundEnabled) {
                SoundHelper.playDecadeChime()
            }

            historyManager.saveCompletedPrayer(
                mysterySetKey = currentState.currentMysterySet.key,
                mysteryTitle = currentState.currentMysterySet.titleAr,
                durationSeconds = currentState.elapsedSeconds,
                intention = currentState.prayerIntention
            )

            val updatedHistory = historyManager.getCompletedPrayers()
            val updatedStreak = historyManager.calculateStreakDays()
            val updatedStats = historyManager.getStatistics()

            _uiState.update {
                it.copy(
                    isCompleted = true,
                    completedPrayers = updatedHistory,
                    streakDays = updatedStreak,
                    statistics = updatedStats
                )
            }
        }
    }

    fun prevStep() {
        val currentState = _uiState.value
        if (currentState.isCompleted) {
            _uiState.update { it.copy(isCompleted = false) }
            return
        }
        val prevIndex = currentState.currentStepIndex - 1
        if (prevIndex >= 0) {
            val prevStep = currentState.steps[prevIndex]
            val resolved = repository.resolveStep(
                step = prevStep,
                currentMysterySet = currentState.currentMysterySet,
                stepIndex = prevIndex,
                totalSteps = currentState.steps.size
            )
            _uiState.update {
                it.copy(
                    currentStepIndex = prevIndex,
                    resolvedStep = resolved,
                    isCompleted = false
                )
            }
        }
    }

    fun jumpToStep(index: Int, context: Context? = null) {
        val currentState = _uiState.value
        if (index in currentState.steps.indices) {
            val step = currentState.steps[index]
            val resolved = repository.resolveStep(
                step = step,
                currentMysterySet = currentState.currentMysterySet,
                stepIndex = index,
                totalSteps = currentState.steps.size
            )
            if (currentState.hapticsEnabled && context != null) {
                HapticHelper.vibrateBead(context)
            }
            _uiState.update {
                it.copy(
                    currentStepIndex = index,
                    resolvedStep = resolved,
                    isCompleted = false
                )
            }
        }
    }

    fun selectMystery(set: MysterySet) {
        val includeChain = _uiState.value.includeChain
        val sequence = buildSequence(set, includeChain)
        val initialResolved = repository.resolveStep(
            step = sequence[0],
            currentMysterySet = set,
            stepIndex = 0,
            totalSteps = sequence.size
        )
        val currentSetData = repository.getMysterySetData(set)
        _uiState.update {
            it.copy(
                currentMysterySet = set,
                currentMysterySetData = currentSetData,
                steps = sequence,
                currentStepIndex = 0,
                resolvedStep = initialResolved,
                isCompleted = false,
                showMysterySelector = false,
                elapsedSeconds = 0L,
                isTimerRunning = true
            )
        }
        startTimer()
    }

    fun setShowTodayMysteriesGuide(show: Boolean) {
        _uiState.update { it.copy(showTodayMysteriesGuide = show) }
    }

    fun setIncludeChain(include: Boolean) {
        val currentSet = _uiState.value.currentMysterySet
        val sequence = buildSequence(currentSet, include)
        val newIndex = _uiState.value.currentStepIndex.coerceAtMost(sequence.size - 1)
        val resolved = repository.resolveStep(
            step = sequence[newIndex],
            currentMysterySet = currentSet,
            stepIndex = newIndex,
            totalSteps = sequence.size
        )
        _uiState.update {
            it.copy(
                includeChain = include,
                steps = sequence,
                currentStepIndex = newIndex,
                resolvedStep = resolved
            )
        }
    }

    fun setFontScale(scale: Float) {
        _uiState.update { it.copy(fontScale = scale) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(hapticsEnabled = enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setPrayerIntention(intention: String?) {
        historyManager.saveIntention(intention)
        _uiState.update { it.copy(prayerIntention = intention, showIntentionDialog = false) }
    }

    fun setShowHistoryDialog(show: Boolean) {
        _uiState.update { it.copy(showHistoryDialog = show) }
    }

    fun setShowStatsScreen(show: Boolean) {
        val stats = if (show) historyManager.getStatistics() else _uiState.value.statistics
        _uiState.update { it.copy(showStatsScreen = show, statistics = stats) }
    }

    fun getStatistics(): RosaryStatistics {
        return historyManager.getStatistics()
    }

    fun setReminderEnabled(enabled: Boolean) {
        val hour = _uiState.value.reminderHour
        val minute = _uiState.value.reminderMinute
        if (enabled) {
            reminderManager.scheduleReminder(hour, minute)
        } else {
            reminderManager.cancelReminder()
        }
        _uiState.update { it.copy(isReminderEnabled = enabled) }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        if (_uiState.value.isReminderEnabled) {
            reminderManager.scheduleReminder(hour, minute)
        }
        _uiState.update { it.copy(reminderHour = hour, reminderMinute = minute) }
    }

    fun sendTestNotification(): Boolean {
        return reminderManager.sendTestNotification()
    }

    fun setShowIntentionDialog(show: Boolean) {
        _uiState.update { it.copy(showIntentionDialog = show) }
    }

    fun setThemeOverride(isDark: Boolean?) {
        _uiState.update { it.copy(isDarkThemeOverride = isDark) }
    }

    fun setShowMysterySelector(show: Boolean) {
        _uiState.update { it.copy(showMysterySelector = show) }
    }

    fun setShowSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }

    fun setShowResetDialog(show: Boolean) {
        _uiState.update { it.copy(showResetDialog = show) }
    }

    fun resetPrayer() {
        val currentState = _uiState.value
        val resolved = repository.resolveStep(
            step = currentState.steps[0],
            currentMysterySet = currentState.currentMysterySet,
            stepIndex = 0,
            totalSteps = currentState.steps.size
        )
        _uiState.update {
            it.copy(
                currentStepIndex = 0,
                resolvedStep = resolved,
                isCompleted = false,
                showResetDialog = false,
                elapsedSeconds = 0L,
                isTimerRunning = true
            )
        }
        startTimer()
    }
}
