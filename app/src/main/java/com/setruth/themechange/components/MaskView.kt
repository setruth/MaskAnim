package com.setruth.themechange.components

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import com.setruth.themechange.model.MaskAnimModel
import kotlin.math.hypot

fun Context.activeMaskView(
    animModel: MaskAnimModel,
    clickX: Float,
    clickY: Float,
    animTime: Long = 800,
    maskComplete: () -> Unit,
    maskAnimFinish: () -> Unit
) {
    val rootView = (this as Activity).window.decorView.rootView as ViewGroup
    val bitmap = rootView.getScreenshot()
    val maskView = MaskView(animModel, Pair(clickX, clickY), this, bitmap)
    rootView.addView(maskView)
    maskComplete()
    maskView.animActive(animTime) {
        rootView.removeView(maskView)
        maskAnimFinish()
    }
}

// TODO 自定义XML的实现
@SuppressLint("ViewConstructor")
private class MaskView(
    private val maskAnimModel: MaskAnimModel,
    private val clickPosition: Pair<Float, Float> = Pair(0f, 0f),
    context: Context,
    private var bitmap: Bitmap
) : View(context) {
    private var maskRadius = 0f
    private val paint = Paint(ANTI_ALIAS_FLAG)

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) = with(canvas) {

        val layer = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        when (maskAnimModel) {

            MaskAnimModel.EXPEND -> {
                drawBitmap(bitmap, 0f, 0f, null)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                drawCircle(clickPosition.first, clickPosition.second, maskRadius, paint)
            }

            MaskAnimModel.SHRINK -> {
                drawCircle(clickPosition.first, clickPosition.second, maskRadius, paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                drawBitmap(bitmap, 0f, 0f, paint)
            }
        }
        paint.xfermode = null
        restoreToCount(layer)
    }

    fun animActive(animTime: Long, animFinish: () -> Unit) {
        val radiusRange = when (maskAnimModel) {
            MaskAnimModel.EXPEND -> Pair(
                0f,
                hypot(rootView.width.toFloat(), rootView.height.toFloat())
            )

            MaskAnimModel.SHRINK -> Pair(
                hypot(rootView.width.toFloat(), rootView.height.toFloat()),
                0f
            )
        }
        ValueAnimator.ofFloat(radiusRange.first, radiusRange.second).apply {
            duration = animTime
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                maskRadius = valueAnimator.animatedValue as Float
                invalidate()
            }
            addListener (onEnd = {
                    animFinish()
            })
        }.start()
    }
}



private fun View.getScreenshot(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}