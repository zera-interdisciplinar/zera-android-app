package com.zera.android.view.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zera.android.view.components.texts.LabelText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.components.texts.TitleText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme

/**
 * Card branco com o resumo de um agendamento de descarte: nome da recicladora como
 * sobretítulo, data e horário em destaque e os materiais a descartar como legenda.
 *
 * Usado na tela de confirmação de agendamento
 * ([com.zera.android.view.screens.manager.recycling.SchedulingSuccessScreen]). Diferente
 * do [ApprovedItemCard], não é específico de item: não tem tag de status.
 *
 * @param recyclerName nome da recicladora (exibido em maiúsculas).
 * @param scheduledAt data e horário já formatados por quem chama (ex.: "21 de agosto · 08:00").
 * @param materials materiais a descartar (ex.: "Notebooks e baterias").
 * @param modifier modificador externo opcional. O card já ocupa toda a largura
 *   disponível por padrão ([Modifier.fillMaxWidth]).
 */
@Composable
fun SchedulingSummaryCard(
    recyclerName: String,
    scheduledAt: String,
    materials: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xLarge),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
        ) {
            OverlineText(text = recyclerName, bold = true)
            TitleText(
                text = scheduledAt,
                bold = true,
                color = MaterialTheme.colorScheme.onSurface,
            )
            LabelText(text = materials, bold = true)
        }
    }
}

@Preview
@Composable
private fun SchedulingSummaryCardPreview() {
    ZeraTheme {
        SchedulingSummaryCard(
            recyclerName = "Recicla Tech Moema",
            scheduledAt = "21 de agosto · 08:00",
            materials = "Notebooks e baterias",
            modifier = Modifier.padding(Spacing.medium),
        )
    }
}
