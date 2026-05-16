package win.liuping.photosuit_android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import win.liuping.photosuit_android.domain.model.BorderStyle
import win.liuping.photosuit_android.domain.model.ExifData
import win.liuping.photosuit_android.domain.model.LogoPosition
import win.liuping.photosuit_android.domain.model.WatermarkConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class WatermarkRenderer @Inject constructor() {

    fun render(
        source: Bitmap,
        config: WatermarkConfig,
        exif: ExifData,
        cameraBitmap: Bitmap? = null,
    ): Bitmap {
        val borderH = (source.height * 0.12f * config.paddingScale).roundToInt().coerceAtLeast(80)
        val pad = (borderH * 0.15f).roundToInt()
        val textSize = (borderH * 0.22f * config.fontScale).coerceAtLeast(16f)
        val subTextSize = textSize * 0.78f
        val dividerW = 2f

        val width = source.width
        val totalHeight = when (config.borderStyle) {
            BorderStyle.NONE -> source.height
            BorderStyle.FULL_FRAME -> source.height + borderH * 2
            BorderStyle.FILM_STRIP -> source.height + borderH + pad * 3
            else -> source.height + borderH
        }

        val result = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val bgPaint = Paint().apply { color = config.borderColorValue.toArgb() }

        when (config.borderStyle) {
            BorderStyle.NONE -> canvas.drawBitmap(source, 0f, 0f, null)
            BorderStyle.FULL_FRAME -> {
                canvas.drawRect(0f, 0f, width.toFloat(), totalHeight.toFloat(), bgPaint)
                canvas.drawBitmap(source, 0f, borderH.toFloat(), null)
                drawInfoBar(canvas, config, exif, cameraBitmap, width, totalHeight - borderH, borderH, pad, textSize, subTextSize, dividerW)
            }
            BorderStyle.FILM_STRIP -> {
                canvas.drawRect(0f, 0f, width.toFloat(), totalHeight.toFloat(), bgPaint)
                drawFilmHoles(canvas, config, width, pad)
                canvas.drawBitmap(source, 0f, pad * 2f, null)
                drawInfoBar(canvas, config, exif, cameraBitmap, width, source.height + pad * 2, borderH, pad, textSize, subTextSize, dividerW)
            }
            else -> {
                canvas.drawBitmap(source, 0f, 0f, null)
                canvas.drawRect(0f, source.height.toFloat(), width.toFloat(), totalHeight.toFloat(), bgPaint)
                drawInfoBar(canvas, config, exif, cameraBitmap, width, source.height, borderH, pad, textSize, subTextSize, dividerW)
            }
        }

        return result
    }

    private fun drawInfoBar(
        canvas: Canvas,
        config: WatermarkConfig,
        exif: ExifData,
        cameraBitmap: Bitmap?,
        width: Int,
        barTop: Int,
        barHeight: Int,
        pad: Int,
        textSize: Float,
        subTextSize: Float,
        dividerW: Float,
    ) {
        val centerY = barTop + barHeight / 2f

        val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.accentColorValue.toArgb()
            this.textSize = textSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = config.textColorValue.toArgb()
            this.textSize = subTextSize
        }
        val dividerPaint = Paint().apply {
            color = config.textColorValue.toArgb()
            alpha = 80
            strokeWidth = dividerW
        }

        val logoSize = (barHeight * 0.55f).roundToInt()
        var logoRight = 0f

        if (config.showCameraLogo && cameraBitmap != null) {
            val scaledLogo = Bitmap.createScaledBitmap(cameraBitmap, logoSize, logoSize, true)
            val logoY = centerY - logoSize / 2f

            if (config.logoPosition == LogoPosition.LEFT) {
                canvas.drawBitmap(scaledLogo, pad.toFloat(), logoY, null)
                logoRight = pad + logoSize + pad.toFloat()

                if (config.dividerVisible) {
                    canvas.drawLine(logoRight, barTop + pad.toFloat(), logoRight, barTop + barHeight - pad.toFloat(), dividerPaint)
                    logoRight += pad.toFloat()
                }
            }
        }

        val leftTextX = if (config.logoPosition == LogoPosition.LEFT) logoRight else pad.toFloat()
        val rightX = width - pad.toFloat()

        val cameraName = if (config.showCameraModel) exif.cameraName else ""
        val lensName = if (config.showLens) exif.lensModel ?: "" else ""

        if (cameraName.isNotBlank()) {
            val textBounds = Rect()
            mainPaint.getTextBounds(cameraName, 0, cameraName.length, textBounds)
            val textY = if (lensName.isNotBlank()) {
                centerY - textBounds.height() * 0.1f
            } else {
                centerY + textBounds.height() * 0.35f
            }
            canvas.drawText(cameraName, leftTextX, textY, mainPaint)

            if (lensName.isNotBlank()) {
                canvas.drawText(lensName, leftTextX, textY + subTextSize * 1.3f, subPaint)
            }
        }

        val params = buildList {
            if (config.showFocalLength) exif.focalLength?.let { add(it) }
            if (config.showAperture) exif.aperture?.let { add(it) }
            if (config.showShutterSpeed) exif.shutterSpeed?.let { add(it) }
            if (config.showIso) exif.iso?.let { add("ISO $it") }
        }

        if (params.isNotEmpty()) {
            val paramText = params.joinToString("  ")
            val subBounds = Rect()
            subPaint.getTextBounds(paramText, 0, paramText.length, subBounds)
            val textY = if (config.showDateTime && exif.dateTime != null) {
                centerY - subBounds.height() * 0.1f
            } else {
                centerY + subBounds.height() * 0.35f
            }
            canvas.drawText(paramText, rightX - subBounds.width(), textY, subPaint)

            if (config.showDateTime && exif.dateTime != null) {
                val dateBounds = Rect()
                subPaint.getTextBounds(exif.dateTime!!, 0, exif.dateTime!!.length, dateBounds)
                canvas.drawText(exif.dateTime!!, rightX - dateBounds.width(), textY + subTextSize * 1.3f, subPaint)
            }
        } else if (config.showDateTime && exif.dateTime != null) {
            val dateBounds = Rect()
            subPaint.getTextBounds(exif.dateTime!!, 0, exif.dateTime!!.length, dateBounds)
            canvas.drawText(exif.dateTime!!, rightX - dateBounds.width(), centerY + dateBounds.height() * 0.35f, subPaint)
        }

        if (config.showCameraLogo && cameraBitmap != null && config.logoPosition == LogoPosition.RIGHT) {
            val logoSize2 = (barHeight * 0.55f).roundToInt()
            val scaledLogo = Bitmap.createScaledBitmap(cameraBitmap, logoSize2, logoSize2, true)
            val logoX = width - pad - logoSize2.toFloat()
            val logoY = centerY - logoSize2 / 2f
            canvas.drawBitmap(scaledLogo, logoX, logoY, null)
        }
    }

    private fun drawFilmHoles(canvas: Canvas, config: WatermarkConfig, width: Int, pad: Int) {
        val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLACK
            alpha = 160
        }
        val holeR = pad * 0.6f
        val holeY = pad.toFloat()
        val count = (width / (holeR * 3.5f)).toInt().coerceAtLeast(4)
        val spacing = width.toFloat() / count
        for (i in 0 until count) {
            val cx = spacing * i + spacing / 2f
            canvas.drawRoundRect(cx - holeR, holeY - holeR, cx + holeR, holeY + holeR, holeR * 0.3f, holeR * 0.3f, holePaint)
        }
    }
}
