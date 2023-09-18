package me.qingshu.latin.extensions

fun <T : Enum<T>> findSet(values: Array<T>, indexes: Set<String>, defaultValue: T): Set<T> {
    val results: Set<T> = indexes.map { findOne(values, it.toInt(), defaultValue) }.toSet()
    return results.ifEmpty { values.toSet() }
}

fun <T : Enum<T>> findOne(values: Array<T>, index: Int, defaultValue: T): T =
        if (index in values.indices) values[index] else defaultValue

fun <T : Enum<T>> ordinals(values: Array<T>): Set<String> =
        ordinals(values.toSet())

fun <T : Enum<T>> ordinals(values: Set<T>): Set<String> =
        values.map { it.ordinal.toString() }.toSet()
