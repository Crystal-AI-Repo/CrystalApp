package com.lovelycatv.ai.crystalapp.data

/**
 * @author lovelycat
 * @since 2025-04-19 03:33
 * @version 1.0
 */
data class BranchPath(
    val path: String,
    val separator: String = ","
) {
    constructor(list: List<Number>, separator: String = ",") : this(list.joinToString(separator = separator), separator)

    val branchIndexes = if (this.path.isBlank())
        listOf()
    else if (!path.contains(this.separator))
        listOf(this.path.toInt())
    else
        this.path.trim().split(",").map { it.toInt() }
}