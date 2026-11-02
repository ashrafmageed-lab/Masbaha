package com.example.domain

import java.time.DayOfWeek
import java.time.LocalDate

enum class MysterySet(val key: String, val titleAr: String, val subtitleAr: String) {
    JOYFUL(
        key = "joyful",
        titleAr = "أسرار الفرح",
        subtitleAr = "تُتلى يومي الإثنين والسبت"
    ),
    SORROWFUL(
        key = "sorrowful",
        titleAr = "أسرار الحزن",
        subtitleAr = "تُتلى يومي الثلاثاء والجمعة"
    ),
    GLORIOUS(
        key = "glorious",
        titleAr = "أسرار المجد",
        subtitleAr = "تُتلى يومي الأربعاء والأحد"
    ),
    LUMINOUS(
        key = "luminous",
        titleAr = "أسرار النور",
        subtitleAr = "تُتلى يوم الخميس"
    )
}

/** Thursday follows luminous unless the user overrides it in settings */
fun mysterySetFor(date: LocalDate, thursdayOverride: MysterySet? = null): MysterySet = when (date.dayOfWeek) {
    DayOfWeek.MONDAY, DayOfWeek.SATURDAY -> MysterySet.JOYFUL
    DayOfWeek.TUESDAY, DayOfWeek.FRIDAY -> MysterySet.SORROWFUL
    DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY -> MysterySet.GLORIOUS
    DayOfWeek.THURSDAY -> thursdayOverride ?: MysterySet.LUMINOUS
    null -> MysterySet.JOYFUL
}

sealed interface Step {
    val stepId: String

    data class Section(val id: String) : Step {
        override val stepId: String = "section_$id"
    }

    data class MysteryHeader(val set: MysterySet, val n: Int) : Step {
        override val stepId: String = "mystery_${set.key}_$n"
    }

    data class Prayer(
        val id: String,
        val repeat: Int = 1,
        val bead: Int = 0,
        val decade: Int = 0
    ) : Step {
        override val stepId: String = "prayer_${id}_d${decade}_b$bead"
    }
}

fun buildSequence(set: MysterySet, includeChain: Boolean = true): List<Step> = buildList {
    add(Step.Section("opening"))
    for (decade in 1..5) {
        add(Step.MysteryHeader(set, decade))
        add(Step.Prayer(id = "our_father", repeat = 1, bead = 0, decade = decade))
        for (b in 1..10) {
            add(Step.Prayer(id = "hail_mary", repeat = 1, bead = b, decade = decade))
        }
        add(Step.Prayer(id = "glory_be", repeat = 1, bead = 0, decade = decade))
    }
    add(Step.Section("salve"))
    if (includeChain) {
        add(Step.Section("chain"))
    }
    add(Step.Section("closing"))
}
