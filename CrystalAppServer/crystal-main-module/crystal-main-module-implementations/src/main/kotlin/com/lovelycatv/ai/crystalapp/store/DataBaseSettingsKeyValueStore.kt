package com.lovelycatv.ai.crystalapp.store

import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueStore
import com.lovelycatv.ai.crystalapp.entity.SettingEntity
import com.lovelycatv.ai.crystalapp.service.SettingService

/**
 * @author lovelycat
 * @since 2025-04-13 04:46
 * @version 1.0
 */
class DataBaseSettingsKeyValueStore(
    private val settingService: SettingService
) : KeyValueStore<String, String> {
    override fun get(key: String): String? {
        return settingService.getById(key)?.value
    }

    override fun set(key: String, value: String) {
        val exist = settingService.getById(key)
        if (exist != null) {
            settingService.updateById(exist.apply { this.value = value })
        } else {
            settingService.save(SettingEntity(key, key, "", value))
        }
    }

    override fun remove(key: String): String? {
        return if (settingService.removeById(key)) key else null
    }

    override fun containsKey(key: String): Boolean {
        return this.get(key) != null
    }
}