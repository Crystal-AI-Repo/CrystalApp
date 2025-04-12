package com.lovelycatv.ai.crystalapp.common

/**
 * @author lovelycat
 * @since 2025-04-13 03:46
 * @version 1.0
 */
data class PagedData<T>(
    val total: Long,
    val pages: Long,
    val current: Long,
    val records: List<T>
)

fun <T, R> PagedData<T>.mapRecords(fx: (T) -> R): PagedData<R> {
    return PagedData(
        total = this.total,
        pages = this.pages,
        current = this.current,
        records = this.records.map(fx)
    )
}