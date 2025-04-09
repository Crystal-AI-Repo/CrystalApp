package com.lovelycatv.ai.crystalapp.common.i18n

/**
 * @author lovelycat
 * @since 2025-04-08 20:08
 * @version 1.0
 */
class I18nMap<T: Any>(
    initialSupportedLocales: Iterable<String>,
    private val currentLocaleProvider: () -> String
) {
    private val locales = mutableListOf(*initialSupportedLocales.toList().toTypedArray())
    private val translationMap = mutableMapOf(
        *initialSupportedLocales.toList().map {
            it to mutableMapOf<String, Any>()
        }.toTypedArray()
    )

    fun addLocale(localeName: String) {
        this.locales.add(localeName)
        this.translationMap[localeName] = mutableMapOf()
    }

    fun getTranslations(localeName: String): Map<String, Any> {
        return this.getMutableTranslations(localeName)
    }

    fun translate(path: String): T? {
        return translate(this.currentLocaleProvider.invoke(), path)
    }

    fun translate(localeName: String, path: String): T? {
        return translate(
            map = this.getTranslations(localeName),
            path = if (path.contains("."))
                path.split(".").toList()
            else
                listOf(path)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun translate(map: Map<String, Any>, path: List<String>): T? {
        return if (path.size == 1) {
            map[path[0]] as? T?
        } else {
            val p1 = path[0]
            val innerMap = map[p1]
            if (innerMap == null || innerMap !is Map<*, *>) {
                null
            } else {
                val realInnerMap = innerMap as? Map<String, Any>
                if (realInnerMap == null) {
                    null
                } else {
                    translate(realInnerMap, path.drop(1))
                }
            }
        }
    }

    private fun getMutableTranslations(localeName: String): MutableMap<String, Any> {
        return translationMap[localeName] ?: throw IllegalStateException("Locale $localeName not exist")
    }

    fun addTranslation(localeName: String, ops: TranslationMapEditor<T>.() -> Unit) {
        val translations = getMutableTranslations(localeName)
        ops.invoke(TranslationMapEditor(translations))
    }

    class TranslationMapEditor<T: Any>(
        private val map: MutableMap<String, Any>
    ) {
        fun putValue(key: String, value: T) {
            if (key.contains(".")) {
                val path = key.split(".")
                path.forEach {
                    putMap(it) {
                        putValue(path.drop(1).joinToString("."), value)
                    }
                }
            } else {
                this.map[key] = value
            }
        }

        fun putMap(key: String, builder: TranslationMapEditor<T>.() -> Unit) {
            val editor = TranslationMapEditor<T>(mutableMapOf())
            builder.invoke(editor)
            this.map[key] = editor.getMap()
        }

        private fun getMap(): Map<String, Any> = this.map
    }
}