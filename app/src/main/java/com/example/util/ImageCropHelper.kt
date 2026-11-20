package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.util.UUID
import kotlin.math.min
import kotlin.math.roundToInt

object ImageCropHelper {

    fun decodeOrientedBitmap(context: Context, uri: Uri, maxDimension: Int = 1440): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            var sampleSize = 1
            while ((bounds.outWidth / sampleSize) > maxDimension || (bounds.outHeight / sampleSize) > maxDimension) {
                sampleSize *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val rawBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOpts)
            } ?: return null

            val orientation = context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL

            val matrix = Matrix()
            var needsTransform = true
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.postRotate(90f)
                    matrix.preScale(-1f, 1f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.postRotate(270f)
                    matrix.preScale(-1f, 1f)
                }
                else -> needsTransform = false
            }

            if (!needsTransform) {
                rawBitmap
            } else {
                val transformed = Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
                if (transformed !== rawBitmap) rawBitmap.recycle()
                transformed
            }
        } catch (_: Exception) {
            null
        }
    }

    fun rotateBitmap(source: Bitmap, degrees: Int): Bitmap {
        val norm = ((degrees % 360) + 360) % 360
        if (norm == 0) return source
        val matrix = Matrix().apply { postRotate(norm.toFloat()) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    /**
     * Crops a square region matching the centered circular cropper viewport.
     *
     * @param zoom Zoom factor >= 1f (1f fits the shorter side to the viewport).
     * @param panNormX Horizontal pan normalized by viewport width (+ moves image right, shifting crop left).
     * @param panNormY Vertical pan normalized by viewport height (+ moves image down, shifting crop up).
     */
    fun cropSquareBitmap(
        source: Bitmap,
        zoom: Float,
        panNormX: Float,
        panNormY: Float,
        outputSizePx: Int = 512
    ): Bitmap {
        val safeZoom = zoom.coerceIn(1f, 5f)
        val srcW = source.width.toFloat().coerceAtLeast(1f)
        val srcH = source.height.toFloat().coerceAtLeast(1f)
        val cropSide = min(srcW, srcH) / safeZoom

        val maxPanNormX = ((srcW / cropSide) - 1f).coerceAtLeast(0f) / 2f
        val maxPanNormY = ((srcH / cropSide) - 1f).coerceAtLeast(0f) / 2f
        val clampedPanX = panNormX.coerceIn(-maxPanNormX, maxPanNormX)
        val clampedPanY = panNormY.coerceIn(-maxPanNormY, maxPanNormY)

        val left = (srcW / 2f - cropSide / 2f - clampedPanX * cropSide)
            .coerceIn(0f, (srcW - cropSide).coerceAtLeast(0f))
        val top = (srcH / 2f - cropSide / 2f - clampedPanY * cropSide)
            .coerceIn(0f, (srcH - cropSide).coerceAtLeast(0f))

        val cropX = left.roundToInt().coerceIn(0, (source.width - 1).coerceAtLeast(0))
        val cropY = top.roundToInt().coerceIn(0, (source.height - 1).coerceAtLeast(0))
        val cropSize = cropSide.roundToInt()
            .coerceIn(1, min(source.width - cropX, source.height - cropY).coerceAtLeast(1))

        val square = Bitmap.createBitmap(source, cropX, cropY, cropSize, cropSize)
        return if (square.width != outputSizePx || square.height != outputSizePx) {
            val scaled = Bitmap.createScaledBitmap(square, outputSizePx, outputSizePx, true)
            if (scaled !== square) square.recycle()
            scaled
        } else {
            square
        }
    }

    fun saveCroppedAvatar(context: Context, bitmap: Bitmap): String? {
        return try {
            val dir = File(context.filesDir, "avatars").apply { mkdirs() }
            val dest = File(dir, "avatar_${UUID.randomUUID().toString().take(10)}.jpg")
            dest.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }
            Uri.fromFile(dest).toString()
        } catch (_: Exception) {
            null
        }
    }
}
