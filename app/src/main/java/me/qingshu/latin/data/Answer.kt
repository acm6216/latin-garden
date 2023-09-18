package me.qingshu.latin.data

import me.qingshu.latin.database.Problem

data class Answer(
    val answer:String,
    val problem: Problem,
    val isTesting:Boolean = true
)