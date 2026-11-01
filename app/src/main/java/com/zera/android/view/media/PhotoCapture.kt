package com.zera.android.view.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

/** Apoio à captura de foto com a câmera do sistema (`TakePicture`). */
object PhotoCapture {
    private const val Directory = "captured_photos"
    private const val MaxSidePx = 1920
    private const val JpegQuality = 85

    const val MimeType = "image/jpeg"

    /** Cria o arquivo temporário (no cache) que a câmera vai preencher e a `Uri` para entregá-lo a ela. */
    fun newTarget(context: Context): Pair<File, Uri> {
        val dir = File(context.cacheDir, Directory).apply { mkdirs() }
        val file = File.createTempFile("photo_", ".jpg", dir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return file to uri
    }

    /**
     * Lê a foto capturada, reduz o lado maior para [MaxSidePx] e regrava como JPEG,
     * aplicando a rotação do EXIF (a câmera grava os pixels sem girar). `null` se não for uma imagem válida.
     * Trabalho pesado: chamar fora da thread principal.
     */
    fun readCompressed(file: File): ByteArray? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MaxSidePx) sample *= 2
        val decoded = BitmapFactory.decodeFile(
            file.path,
            BitmapFactory.Options().apply { inSampleSize = sample },
        ) ?: return null

        val degrees = when (
            ExifInterface(file.path).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL,
            )
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val upright = if (degrees == 0f) {
            decoded
        } else {
            Bitmap.createBitmap(
                decoded, 0, 0, decoded.width, decoded.height,
                Matrix().apply { postRotate(degrees) },
                true,
            ).also { decoded.recycle() }
        }
        return ByteArrayOutputStream().use { out ->
            upright.compress(Bitmap.CompressFormat.JPEG, JpegQuality, out)
            upright.recycle()
            out.toByteArray()
        }
    }
}
