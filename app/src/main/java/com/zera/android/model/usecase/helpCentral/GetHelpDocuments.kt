package com.zera.android.model.usecase.helpCentral

import com.zera.android.model.config.AppConfig
import com.zera.android.model.entity.helpCentral.HelpDocument
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.text.Collator
import java.util.Locale

/**
 * Documentos da Central de Ajuda, lidos das flags do Scrapy já carregadas em [AppConfig.flags]
 * (o `LoadFlags` as busca ao entrar no app). Não faz chamada de rede.
 *
 * @param readFlags fonte das flags; o padrão é [AppConfig.flags].
 */
class GetHelpDocuments(
    private val readFlags: () -> JsonObject = { AppConfig.flags },
) {
    /** Todos os documentos, ordenados pelo título. */
    fun execute(): List<HelpDocument> = documentsFrom(readFlags())

    /** O documento da chave [key], ou `null` se ele não estiver nas flags. */
    fun find(key: String): HelpDocument? = execute().firstOrNull { it.key == key }

    companion object {
        private val titleCollator = Collator.getInstance(Locale.forLanguageTag("pt-BR")).apply {
            strength = Collator.PRIMARY
        }

        /** Entries com [HelpDocument.KEY_PREFIX] e valor string; as demais são ignoradas. */
        internal fun documentsFrom(flags: JsonObject): List<HelpDocument> {
            return flags
                .filterKeys { it.startsWith(HelpDocument.KEY_PREFIX) }
                .mapNotNull { (key, value) ->
                    val text = (value as? JsonPrimitive)?.takeIf { it.isString }?.content
                    text?.let { HelpDocument.fromEntry(key, it) }
                }
                .sortedWith(compareBy(titleCollator) { it.title })
        }
    }
}
