package me.qingshu.latin.extensions

import me.qingshu.latin.data.PlantType
import me.qingshu.latin.database.Gold
import me.qingshu.latin.database.Plant
import me.qingshu.latin.database.Problem

fun String.preProcess(symbol:String):List<String> = split(symbol).toMutableList().also {
    it.removeFirst()
}

fun String.toGold():Gold{
    val (_,author,subtitle,sentence) = split(",")
    return Gold(
        id = 0,
        author = author,
        sentence = sentence,
        subtitle = subtitle
    )
}

fun String.toLatinDefinition(type: PlantType):Plant{
    val (id,chinese,latin,family,category,definition) = split(",")
    return Plant(
        id = 0,
        chinese = chinese,
        latin = latin,
        family = family,
        category = category,
        definition = definition.replace("##","\n"),
        resId = id,
        type = type,
        lastCompetedTime = 0L
    )
}

fun String.toProblem(type:PlantType):Problem{
    val (id,detail,a,b,c,d,answer) = split(",")
    return Problem(
        id = 0,
        resId = id,
        problemDetail = detail,
        a = a,
        b = b,
        c = c,
        d = d,
        answer = answer,
        type = type
    )
}

private operator fun <E> List<E>.component6(): E = this[5]

private operator fun <E> List<E>.component7(): E = this[6]
