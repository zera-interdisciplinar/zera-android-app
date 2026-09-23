package com.zera.android.view.theme.icons

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.zera.android.R

/**
 * Catálogo de ícones do app.
 *
 * Cada entrada aponta para um drawable vetorial em `res/drawable`. Prefira usar
 * este enum em vez de `R.drawable.xxx` solto: o autocomplete lista os ícones
 * disponíveis e um rename no recurso quebra a compilação num lugar só.
 *
 * Para desenhar, use o composable `ZeraIcon(...)` (ver `ZeraIcons.kt`):
 *
 * ```
 * ZeraIcon(ZeraIcon.Bell, contentDescription = "Notificações")
 * ZeraIcon(ZeraIcon.Check, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
 * ```
 */
enum class ZeraIcon(@DrawableRes val resId: Int) {
    Bell(R.drawable.bell),
    Box(R.drawable.box),
    Chatbot(R.drawable.chatbot),
    Check(R.drawable.check),
    Close(R.drawable.close),
    Crate(R.drawable.crate),
    DownArrow(R.drawable.down_arrow),
    Download(R.drawable.download),
    Edit(R.drawable.edit),
    Exclamation(R.drawable.exclamation),
    Gear(R.drawable.gear),
    Graph(R.drawable.graph),
    Group(R.drawable.group),
    Home(R.drawable.home),
    InfoCircle(R.drawable.info_circle),
    LeftArrow(R.drawable.left_arrow),
    RightArrow(R.drawable.right_arrow),
    LeftUTurn(R.drawable.left_u_turn),
    Location(R.drawable.location),
    MagnifyingGlass(R.drawable.magnifying_glass),
    Megaphone(R.drawable.megaphone),
    Placeholder(R.drawable.placeholder),
    Plus(R.drawable.plus),
    ProceedArrow(R.drawable.proceed_arrow),
    Profile(R.drawable.profile),
    QrCode(R.drawable.qr_code),
    Recycle(R.drawable.recycle),
    RightUTurn(R.drawable.right_u_turn),
    Screen(R.drawable.screen),
    Send(R.drawable.send),
    Truck(R.drawable.truck),
    Wrench(R.drawable.wrench),
    Menu(R.drawable.menu),

    BriefCase(R.drawable.brief_case);

    /** `Painter` do ícone, para composables que pedem `painter` em vez de aceitar [ZeraIcon]. */
    @Composable
    fun painter(): Painter = painterResource(resId)
}
