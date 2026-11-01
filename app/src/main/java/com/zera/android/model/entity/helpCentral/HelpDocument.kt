package com.zera.android.model.entity.helpCentral

/**
 * Documento da Central de Ajuda, montado a partir de uma entry de conteúdo do Scrapy.
 *
 * @param key chave da entry no Scrapy (ex.: `category_vidro`), identificador estável do documento.
 * @param title título, exibido na lista e no detalhe.
 * @param subtitle subtítulo, exibido na lista. Pode ser vazio.
 * @param body corpo em markdown, exibido no detalhe. Pode ser vazio.
 */
data class HelpDocument(
    val key: String,
    val title: String,
    val subtitle: String,
    val body: String,
) {
    companion object {
        /** Prefixo das chaves do Scrapy que são documentos da Central de Ajuda. */
        const val KEY_PREFIX = "category_"

        /**
         * Monta o documento a partir do markdown da entry: 1ª linha = título, 2ª = subtítulo,
         * resto = corpo. Linhas em branco antes do título e do subtítulo são puladas, e um `#`
         * de cabeçalho no começo deles é removido. Sem título, devolve `null`.
         */
        fun fromEntry(key: String, value: String): HelpDocument? {
            val lines = value.replace("\r\n", "\n").lines()
            val titleIndex = lines.indexOfFirst { it.isNotBlank() }
            if (titleIndex < 0) return null
            val title = headingText(lines[titleIndex])
            if (title.isEmpty()) return null
            val subtitleIndex = (titleIndex + 1 until lines.size)
                .firstOrNull { lines[it].isNotBlank() }
                ?: lines.size

            return HelpDocument(
                key = key,
                title = title,
                subtitle = lines.getOrNull(subtitleIndex)?.let(::headingText).orEmpty(),
                body = lines.drop(subtitleIndex + 1).joinToString("\n").trim(),
            )
        }

        private fun headingText(line: String): String = line.trim().trimStart('#').trim()
    }
}
