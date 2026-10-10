package com.zera.android.view.components.outros

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Analisador de frames da câmera (CameraX) que lê QR code e código de barras Code 128
 * com o ML Kit.
 *
 * Cada frame recebido em [analyze] é enviado ao ML Kit; quando algum código é encontrado,
 * o texto dele é entregue em [onBarcodeDetected] (na thread principal). O frame é sempre
 * fechado ao fim do processamento — sem isso, a câmera para de mandar novos frames.
 *
 * O analisador dispara a cada frame em que o código aparece: quem recebe o callback é
 * responsável por ignorar leituras repetidas.
 *
 * Chame [close] quando a câmera for desligada para liberar o scanner do ML Kit.
 *
 * @param onBarcodeDetected recebe o valor bruto (`rawValue`) do código lido.
 */
class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_CODE_128)
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstNotNullOfOrNull { it.rawValue }?.let(onBarcodeDetected)
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    fun close() {
        scanner.close()
    }
}
