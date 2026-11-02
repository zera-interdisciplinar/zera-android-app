package com.zera.android.view.navigation

import android.content.Intent
import com.zera.android.model.local.SqliteManager

/** Deep link `zera://item?id=<itemId>` para o detalhe de um item. */
object PendingItemDeepLink {
    @Volatile
    private var itemId: String? = null

    fun stash(intent: Intent?) {
        val uri = intent?.data ?: return
        val id = itemIdFromDeepLink(uri.scheme, uri.host, uri.getQueryParameter("id")) ?: return
        itemId = id
    }

    fun peek(): String? = itemId

    fun takeItemId(): String? {
        val current = itemId
        itemId = null
        return current
    }
}

/** O `id` do item quando o link é `zera://item?id=...` e o `id` não é vazio; senão `null`. */
internal fun itemIdFromDeepLink(scheme: String?, host: String?, id: String?): String? {
    if (scheme != "zera" || host != "item") return null
    return id?.trim()?.takeIf { it.isNotEmpty() }
}

/**
 * Abre o detalhe do item do link pendente. Sem sessão o link continua pendente, para ser aberto
 * depois do login (Splash, login e cadastro chamam esta função ao entrar).
 */
fun consumePendingItemDeepLink() {
    if (PendingItemDeepLink.peek() == null) return
    if (SqliteManager.getAccessToken().isNullOrBlank()) return
    val itemId = PendingItemDeepLink.takeItemId() ?: return
    ZeraNavigator.push(Route.ItemDetails(itemId = itemId))
}
