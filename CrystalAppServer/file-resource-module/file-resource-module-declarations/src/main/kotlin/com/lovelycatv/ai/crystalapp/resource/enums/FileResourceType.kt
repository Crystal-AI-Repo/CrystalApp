package com.lovelycatv.ai.crystalapp.resource.enums

/**
 * @author lovelycat
 * @since 2025-04-23 18:57
 * @version 1.0
 */
enum class FileResourceType(val typeId: Int) {
    FILE(0),
    USER_AVATAR(1),
    USER_BACKGROUND(2),
    CHARACTER_AVATAR(3),
    CHARACTER_BACKGROUND(4);

    companion object {
        fun getById(id: Int) = entries.find { it.typeId == id }
    }
}