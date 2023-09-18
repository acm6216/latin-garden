package me.qingshu.latin.extensions

import me.qingshu.latin.data.Completion
import me.qingshu.latin.database.Plant
import java.util.Calendar
import kotlin.random.Random

const val ONE_DAY = 24 * 60 * 60 * 1000

fun Long.intervalDay(): Int {
    if(this==0L) return 0
    val timestampSecond = System.currentTimeMillis()
    val calendarFirst = Calendar.getInstance().apply {
        timeInMillis = this@intervalDay
        clearHourMinSecInfo()
    }
    val calendarSecond = Calendar.getInstance().apply {
        timeInMillis = timestampSecond
        clearHourMinSecInfo()
    }
    return ((calendarSecond.timeInMillis - calendarFirst.timeInMillis) / ONE_DAY).toInt()
}

fun Calendar.clearHourMinSecInfo() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
fun Plant.chineseWithFamily(random: Random):Completion{
    return when (random.nextInt(3)) {
        0 -> Completion(
            resId = resId,
            detail = "$latin 对应的中文名为 ____ ，为 ____ 科 ____ 属。",
            answer = "$chinese;$family;$category"
        )
        1 -> Completion(
            resId = resId,
            detail = "$latin 对应的中文名为 ____ ，为 $family ____ 属。",
            answer = "$chinese;$category"
        )
        else -> Completion(
            resId = resId,
            detail = "$latin 对应的中文名为 ____ ，为 ____ 科 ${category}。",
            answer = "$chinese;$family;"
        )
    }
}
fun Plant.latinWithFamily(random: Random):Completion{
    return when (random.nextInt(3)) {
        0 -> Completion(
            resId = resId,
            detail = "$chinese 对应的拉丁名为 ____ ，为 ____ 科 ____ 属。",
            answer = "$latin;$family;$category"
        )
        1 -> Completion(
            resId = resId,
            detail = "$chinese 对应的拉丁名为 ____ ，为 $family ____ 属。",
            answer = "$latin;$category"
        )
        else -> Completion(
            resId = resId,
            detail = "$chinese 对应的拉丁名为 ____ ，为 ____ 科 ${category}。",
            answer = "$latin;$family;"
        )
    }
}
fun Plant.chineseType(): Completion {
    return Completion(
        resId = resId,
        detail = "$latin 对应的中文名为 ______ 。",
        answer = chinese
    )
}
fun Plant.familyType(random: Random): Completion {
    return if(!chinese.contains("属")) {
        val (detail,ans) = when(random.nextInt(3)){
            0 -> "，属于____ 科，____ 属。#$family; $category"
            1 -> "，属于${family}，____ 属。#$category"
            else -> "，属于____ 科，$category。#$family"
        }.split("#")
        Completion(
            resId = resId,
            detail = "$chinese，$latin$detail",
            answer = ans
        )
    } else Completion(
        resId = resId,
        detail = "$chinese，$latin，属于____ 科，${category}。",
        answer = family
    )
}
fun Plant.latinType(random: Random): Completion {
    val target = latin.trimEnd().trimStart()
    if(target.indexOf(" ")!=-1){
        val temp = latin.split(" ").let {
            it[random.nextInt(it.size)]
        }
        val detail = "补全 $chinese 对应的拉丁名，${latin.replace(temp,"_____")}。"
        return Completion(
            resId = resId,
            detail = detail,
            answer = temp
        )
    }else{
        return Completion(
            resId = resId,
            detail = "$chinese 对应的拉丁名为：______ 。",
            answer = latin
        )
    }
}