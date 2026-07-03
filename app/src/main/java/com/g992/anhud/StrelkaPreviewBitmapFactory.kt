package com.g992.anhud

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

internal object StrelkaPreviewBitmapFactory {
    fun create(context: Context): Bitmap {
        val density = context.resources.displayMetrics.density
        val width = (168f * density).toInt().coerceAtLeast(1)
        val height = (72f * density).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#161616")
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F04444")
            style = Paint.Style.STROKE
            strokeWidth = 2f * density
        }
        val limitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
        }
        val limitBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E53935")
            style = Paint.Style.STROKE
            strokeWidth = 5f * density
        }
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 14f * density
            isFakeBoldText = true
        }
        val detailPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#EAEAEA")
            textSize = 13f * density
            isFakeBoldText = true
        }

        val radius = 10f * density
        val backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(backgroundRect, radius, radius, backgroundPaint)
        canvas.drawRoundRect(backgroundRect, radius, radius, borderPaint)

        val circleRadius = 22f * density
        val circleCenterX = 28f * density
        val circleCenterY = height / 2f
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, limitPaint)
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, limitBorderPaint)

        val limitTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f * density
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        val limitBaseline = circleCenterY - ((limitTextPaint.descent() + limitTextPaint.ascent()) / 2f)
        canvas.drawText("90", circleCenterX, limitBaseline, limitTextPaint)

        val titleX = 60f * density
        val titleY = 28f * density
        canvas.drawText("STRELKA", titleX, titleY, titlePaint)
        canvas.drawText("CAMERA 450m", titleX, titleY + (20f * density), detailPaint)

        return bitmap
    }
}
