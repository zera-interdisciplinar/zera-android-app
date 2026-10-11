package com.zera.android.view.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zera.android.view.components.buttons.ZeraButton
import com.zera.android.view.components.buttons.ZeraButtonType
import com.zera.android.view.components.texts.BodyText
import com.zera.android.view.components.texts.CaptionText
import com.zera.android.view.components.texts.OverlineText
import com.zera.android.view.theme.Radius
import com.zera.android.view.theme.Spacing
import com.zera.android.view.theme.ZeraTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/**
 * Campo de data padrão do app: mesma estrutura visual do [ZeraTextInput], mas o toque
 * abre um [DatePickerDialog] em vez do teclado, evitando datas digitadas inválidas.
 *
 * @param onValueChange chamado com a data escolhida, formatada como `dd/MM/yyyy`.
 * @param modifier modificador externo opcional.
 * @param value data atual no formato `dd/MM/yyyy` (estado controlado pelo chamador).
 * @param label rótulo exibido acima do campo. Omitido quando vazio.
 * @param placeholder texto de dica exibido quando [value] está vazio.
 * @param enabled habilita ou desabilita a interação.
 * @param isError quando `true`, destaca o campo com a cor de erro.
 * @param errorMessage mensagem exibida abaixo do campo quando [isError] for `true`.
 * @param width largura fixa. Quando `null`, o campo ocupa toda a largura disponível.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZeraDateInput(
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    width: Dp? = null,
) {
    var showDialog by remember { mutableStateOf(false) }

    val widthModifier =
        if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()

    val borderColor =
        if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline

    Column(modifier = modifier.then(widthModifier)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(Radius.large),
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(Radius.large),
                )
                .clickable(enabled = enabled) { showDialog = true }
                .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        ) {
            if (label.isNotEmpty()) {
                OverlineText(text = label, bold = true)
                Spacer(modifier = Modifier.height(Spacing.micro))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                BodyText(
                    text = value.ifEmpty { placeholder },
                    modifier = Modifier.weight(1f),
                    alpha = if (value.isEmpty()) 0.5f else 1f,
                )
            }
        }
        if (isError && !errorMessage.isNullOrEmpty()) {
            CaptionText(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(
                    start = Spacing.small,
                    top = Spacing.small,
                ),
            )
        }
    }

    if (showDialog) {
        val initialMillis = runCatching {
            LocalDate.parse(value, DATE_FORMATTER)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                ZeraButton(
                    text = "Confirmar",
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            onValueChange(date.format(DATE_FORMATTER))
                        }
                        showDialog = false
                    },
                    type = ZeraButtonType.Tertiary,
                    modifier = Modifier.padding(end = Spacing.small, bottom = Spacing.small),
                )
            },
            dismissButton = {
                ZeraButton(
                    text = "Cancelar",
                    onClick = { showDialog = false },
                    type = ZeraButtonType.Tertiary,
                    modifier = Modifier.padding(bottom = Spacing.small),
                )
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ZeraDateInputPreview() {
    ZeraTheme {
        var date by remember { mutableStateOf("") }
        Column(modifier = Modifier.padding(Spacing.medium)) {
            ZeraDateInput(
                label = "Data de aquisição",
                placeholder = "DD/MM/AAAA",
                value = date,
                onValueChange = { date = it },
            )
        }
    }
}
