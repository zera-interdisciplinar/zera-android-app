package com.zera.android.view.components.outros

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zera.android.view.theme.ZeraColorFamily

/**
 * Status de um item, espelhando (parcialmente) o enum do backend. Cada valor já
 * carrega o [label] e a [colorFamily] exibidos no chip — quem for desenhar o chip
 * só precisa passar o [ItemStatus] (ver [ItemStatusTag]), sem duplicar texto/cor
 * soltos em cada tela.
 *
 * O backend também tem `DRAFT` e `REMOVED`, que ainda não têm chip definido — por
 * isso não estão modelados aqui. Adicione quando esses estados também precisarem
 * aparecer em algum chip.
 */
enum class ItemStatus(val label: String, val colorFamily: ZeraColorFamily) {
    /** Aguardando o gestor aprovar o cadastro feito pelo operário. */
    PendingApproval(label = "Pendente", colorFamily = ZeraColorFamily.Yellow),

    /** Reprovado pelo gestor; pode ser corrigido e reenviado. */
    Rejected(label = "Recusado", colorFamily = ZeraColorFamily.Red),

    /** Aprovado, disponível no estoque. */
    InStock(label = "Aprovado", colorFamily = ZeraColorFamily.Green),

    /** Em manutenção. */
    InMaintenance(label = "Em manutenção", colorFamily = ZeraColorFamily.Yellow),

    /** Manutenção concluída, esperando alguém avaliar em que condição o item voltou. */
    AwaitingEvaluation(label = "Em aprovação", colorFamily = ZeraColorFamily.Yellow),

    /** Descartado. */
    Disposed(label = "Descartado", colorFamily = ZeraColorFamily.Red),
}

/** [Tag] pré-preenchida com o [ItemStatus.label] e [ItemStatus.colorFamily] de [status]. */
@Composable
fun ItemStatusTag(status: ItemStatus, modifier: Modifier = Modifier) {
    Tag(text = status.label, style = status.colorFamily, modifier = modifier)
}
