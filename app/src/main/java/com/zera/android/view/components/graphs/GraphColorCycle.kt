package com.zera.android.view.components.graphs

import com.zera.android.view.theme.ZeraColorFamily

/**
 * Ciclo de cores compartilhado pelos gráficos de barra deste pacote
 * ([HorizontalBarGraph], [VerticalBarGraph]): 1º valor mais alto = azul, 2º = amarelo,
 * 3º = verde, 4º = azul de novo...
 */
internal val BarGraphColorCycle = listOf(ZeraColorFamily.Blue, ZeraColorFamily.Yellow, ZeraColorFamily.Green)

/**
 * Atribui uma [ZeraColorFamily] a cada item de [items] pelo ranking decrescente de
 * [valueOf], ciclando por [BarGraphColorCycle].
 *
 * @return mapa do **índice original** em [items] (não o rank) para a cor correspondente
 *   — a ordem de [items] continua livre para definir só a ordem de exibição.
 */
internal fun <T> rankedBarGraphColors(items: List<T>, valueOf: (T) -> Float): Map<Int, ZeraColorFamily> =
    items
        .withIndex()
        .sortedByDescending { (_, item) -> valueOf(item) }
        .mapIndexed { rank, (originalIndex, _) -> originalIndex to BarGraphColorCycle[rank % BarGraphColorCycle.size] }
        .toMap()
