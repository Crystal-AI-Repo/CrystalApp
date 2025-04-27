package com.lovelycatv.ai.crystalapp.resource.enums

/**
 * @author lovelycat
 * @since 2025-04-23 18:57
 * @version 1.0
 */
enum class FileResourceStorageType(val typeId: Int) {
    LOCAL(0);

    companion object {
        fun getById(id: Int) = entries.find { it.typeId == id }
    }
}