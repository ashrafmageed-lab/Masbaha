package com.example.data

import android.content.Context
import com.example.domain.MysterySet
import com.example.domain.Step
import org.json.JSONObject

data class SectionItem(
    val type: String, // "text", "leader", "response", "title", "hymn"
    val text: String
)

data class SectionData(
    val id: String,
    val title: String,
    val items: List<SectionItem>
)

data class MysteryDetail(
    val n: Int,
    val text: String,
    val fruit: String
)

data class MysterySetData(
    val key: String,
    val title: String,
    val intro: String,
    val mysteries: List<MysteryDetail>
)

data class TodayMysteryInfo(
    val dayNameAr: String,
    val fullDateFormattedAr: String,
    val mysterySet: MysterySet,
    val introText: String,
    val mysteries: List<MysteryDetail>
)

data class ResolvedStepContent(
    val step: Step,
    val stepIndex: Int,
    val totalSteps: Int,
    val title: String,
    val subtitle: String?,
    val mysteryFruit: String?,
    val mainText: String?,
    val sectionItems: List<SectionItem>?,
    val currentDecade: Int, // 1..5, or 0 if outside decades
    val currentBead: Int,   // 1..10 for Hail Mary, or 0
    val prayerKind: String  // "section", "mystery_header", "our_father", "hail_mary", "glory_be"
)

class RosaryRepository(private val context: Context) {

    private val prayersMap = mutableMapOf<String, String>()
    private val sectionsMap = mutableMapOf<String, SectionData>()
    private val mysterySetsMap = mutableMapOf<String, MysterySetData>()

    init {
        loadData()
    }

    private fun loadData() {
        try {
            val jsonString = context.assets.open("prayers_ar.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Prayers
            val prayersObj = root.optJSONObject("prayers")
            if (prayersObj != null) {
                for (key in prayersObj.keys()) {
                    prayersMap[key] = prayersObj.getString(key)
                }
            }

            // Sections
            val sectionsObj = root.optJSONObject("sections")
            if (sectionsObj != null) {
                for (secKey in sectionsObj.keys()) {
                    val arr = sectionsObj.getJSONArray(secKey)
                    val items = mutableListOf<SectionItem>()
                    for (i in 0 until arr.length()) {
                        val itemObj = arr.getJSONObject(i)
                        items.add(
                            SectionItem(
                                type = itemObj.optString("type", "text"),
                                text = itemObj.optString("text", "")
                            )
                        )
                    }
                    val secTitle = getSectionTitle(secKey)
                    sectionsMap[secKey] = SectionData(secKey, secTitle, items)
                }
            }

            // Mystery sets
            val mysterySetsObj = root.optJSONObject("mystery_sets")
            if (mysterySetsObj != null) {
                for (mKey in mysterySetsObj.keys()) {
                    val mObj = mysterySetsObj.getJSONObject(mKey)
                    val title = mObj.optString("title", "")
                    val intro = mObj.optString("intro", "")
                    val mysteriesArr = mObj.getJSONArray("mysteries")
                    val mysteriesList = mutableListOf<MysteryDetail>()
                    for (i in 0 until mysteriesArr.length()) {
                        val mItem = mysteriesArr.getJSONObject(i)
                        mysteriesList.add(
                            MysteryDetail(
                                n = mItem.getInt("n"),
                                text = mItem.getString("text"),
                                fruit = mItem.getString("fruit")
                            )
                        )
                    }
                    mysterySetsMap[mKey] = MysterySetData(mKey, title, intro, mysteriesList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSectionTitle(id: String): String = when (id) {
        "opening" -> "صلوات البداية"
        "salve" -> "الصلاة الختامية للمسبحة"
        "chain" -> "صلاة السلسلة"
        "closing" -> "صلاة الختام"
        else -> id
    }

    private fun getDecadeArabicOrdinal(n: Int): String = when (n) {
        1 -> "السر الأول"
        2 -> "السر الثاني"
        3 -> "السر الثالث"
        4 -> "السر الرابع"
        5 -> "السر الخامس"
        else -> "السر رقم $n"
    }

    fun getPrayer(key: String): String? = prayersMap[key]

    fun getMysteryDetail(set: MysterySet, decade: Int): MysteryDetail? {
        val setData = mysterySetsMap[set.key] ?: return null
        return setData.mysteries.firstOrNull { it.n == decade }
    }

    fun getMysterySetData(set: MysterySet): MysterySetData? = mysterySetsMap[set.key]
    
    fun getClosingPrayers(): List<SectionData> = listOfNotNull(
        sectionsMap["salve"],
        sectionsMap["chain"],
        sectionsMap["closing"]
    )

    fun getTodayMysteryInfo(
        date: java.time.LocalDate = java.time.LocalDate.now(),
        overrideSet: MysterySet? = null
    ): TodayMysteryInfo {
        val detectedSet = overrideSet ?: com.example.domain.mysterySetFor(date)
        val dayName = when (date.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "الإثنين"
            java.time.DayOfWeek.TUESDAY -> "الثلاثاء"
            java.time.DayOfWeek.WEDNESDAY -> "الأربعاء"
            java.time.DayOfWeek.THURSDAY -> "الخميس"
            java.time.DayOfWeek.FRIDAY -> "الجمعة"
            java.time.DayOfWeek.SATURDAY -> "السبت"
            java.time.DayOfWeek.SUNDAY -> "الأحد"
            null -> "اليوم"
        }
        val setData = mysterySetsMap[detectedSet.key]
        val formattedDate = "$dayName، ${date.dayOfMonth} / ${date.monthValue} / ${date.year}"
        return TodayMysteryInfo(
            dayNameAr = dayName,
            fullDateFormattedAr = formattedDate,
            mysterySet = detectedSet,
            introText = setData?.intro ?: "",
            mysteries = setData?.mysteries ?: emptyList()
        )
    }

    fun resolveStep(
        step: Step,
        currentMysterySet: MysterySet,
        stepIndex: Int,
        totalSteps: Int
    ): ResolvedStepContent {
        return when (step) {
            is Step.Section -> {
                val secData = sectionsMap[step.id]
                ResolvedStepContent(
                    step = step,
                    stepIndex = stepIndex,
                    totalSteps = totalSteps,
                    title = secData?.title ?: getSectionTitle(step.id),
                    subtitle = "صلوات المسبحة",
                    mysteryFruit = null,
                    mainText = null,
                    sectionItems = secData?.items ?: emptyList(),
                    currentDecade = 0,
                    currentBead = 0,
                    prayerKind = "section"
                )
            }
            is Step.MysteryHeader -> {
                val mysteryDetail = getMysteryDetail(step.set, step.n)
                val ordinal = getDecadeArabicOrdinal(step.n)
                val setData = mysterySetsMap[step.set.key]
                ResolvedStepContent(
                    step = step,
                    stepIndex = stepIndex,
                    totalSteps = totalSteps,
                    title = "$ordinal من ${setData?.title ?: step.set.titleAr}",
                    subtitle = "تأمل في السر والفضيلة المطلوبة",
                    mysteryFruit = mysteryDetail?.fruit,
                    mainText = mysteryDetail?.text ?: "",
                    sectionItems = null,
                    currentDecade = step.n,
                    currentBead = 0,
                    prayerKind = "mystery_header"
                )
            }
            is Step.Prayer -> {
                val mysteryDetail = getMysteryDetail(currentMysterySet, step.decade)
                val ordinal = getDecadeArabicOrdinal(step.decade)
                when (step.id) {
                    "our_father" -> {
                        ResolvedStepContent(
                            step = step,
                            stepIndex = stepIndex,
                            totalSteps = totalSteps,
                            title = "الصلاة الربية (أبانا الذي في السماوات)",
                            subtitle = "$ordinal: ${currentMysterySet.titleAr}",
                            mysteryFruit = mysteryDetail?.fruit,
                            mainText = prayersMap["our_father"] ?: "",
                            sectionItems = null,
                            currentDecade = step.decade,
                            currentBead = 0,
                            prayerKind = "our_father"
                        )
                    }
                    "hail_mary" -> {
                        ResolvedStepContent(
                            step = step,
                            stepIndex = stepIndex,
                            totalSteps = totalSteps,
                            title = "السلام الملائكي (حبة ${step.bead} من 10)",
                            subtitle = "$ordinal: ${currentMysterySet.titleAr}",
                            mysteryFruit = mysteryDetail?.fruit,
                            mainText = prayersMap["hail_mary"] ?: "",
                            sectionItems = null,
                            currentDecade = step.decade,
                            currentBead = step.bead,
                            prayerKind = "hail_mary"
                        )
                    }
                    "glory_be" -> {
                        ResolvedStepContent(
                            step = step,
                            stepIndex = stepIndex,
                            totalSteps = totalSteps,
                            title = "المجدلة (المجد للآب)",
                            subtitle = "$ordinal: ${currentMysterySet.titleAr}",
                            mysteryFruit = mysteryDetail?.fruit,
                            mainText = prayersMap["glory_be"] ?: "",
                            sectionItems = null,
                            currentDecade = step.decade,
                            currentBead = 0,
                            prayerKind = "glory_be"
                        )
                    }
                    else -> {
                        ResolvedStepContent(
                            step = step,
                            stepIndex = stepIndex,
                            totalSteps = totalSteps,
                            title = step.id,
                            subtitle = null,
                            mysteryFruit = null,
                            mainText = prayersMap[step.id] ?: "",
                            sectionItems = null,
                            currentDecade = step.decade,
                            currentBead = step.bead,
                            prayerKind = "prayer"
                        )
                    }
                }
            }
        }
    }
}
