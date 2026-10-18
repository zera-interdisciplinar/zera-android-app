package com.zera.android.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Cores do app que não têm um papel equivalente no [androidx.compose.material3.ColorScheme].
 * Acesse por [zeraColors].
 *
 * @param navContainer fundo das superfícies de navegação (sidebar, tela de configurações).
 *   No claro é o `DarkBlue25`; no escuro é o `background`, o mesmo fundo da home.
 * @param onNavContainer texto e ícones sobre [navContainer]. Também serve de fundo do item
 *   selecionado da sidebar, que usa [navContainer] como conteúdo.
 * @param onBlueContainer texto e ícones sobre o container da [ZeraColorFamily.Blue]
 *   (`primaryContainer`). Não dá para usar o `onPrimaryContainer` do tema, que no escuro
 *   é o mesmo azul de [navContainer] e some sobre o container.
 * @param brandContainer fundo azul de destaque: o [com.zera.android.view.components.containers.ZeraBox]
 *   azul sólido e o cabeçalho das telas de conversa. No claro é o `primary`; no escuro é o
 *   `surface`, a mesma cor do fundo do `ShortcutCard` (o `primary` do escuro é um azul claro).
 * @param onBrandContainer texto e ícones sobre [brandContainer].
 */
@Immutable
data class ZeraColors(
    val navContainer: Color,
    val onNavContainer: Color,
    val onBlueContainer: Color,
    val brandContainer: Color,
    val onBrandContainer: Color,
)

internal val LightZeraColors = ZeraColors(
    navContainer = DarkBlue25,
    onNavContainer = White100,
    onBlueContainer = DarkBlue25,
    brandContainer = DarkBlue25,
    onBrandContainer = White100,
)

internal val DarkZeraColors = ZeraColors(
    navContainer = DarkBlue15,
    onNavContainer = White95,
    onBlueContainer = LightPastelBlue90,
    brandContainer = DarkBlue20,
    onBrandContainer = White95,
)

internal val LocalZeraColors = staticCompositionLocalOf { LightZeraColors }

/** Cores do app fornecidas pelo [ZeraTheme]. */
val MaterialTheme.zeraColors: ZeraColors
    @Composable
    get() = LocalZeraColors.current
