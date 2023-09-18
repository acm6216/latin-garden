package me.qingshu.latin.data

import me.qingshu.latin.database.Plant

data class ListItem(
    val data:Plant = Plant.EMPTY,
    val sortBy: SortBy = SortBy.CHINESE
)