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
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
    val windows=(this as Activity).window
    val rootView = windows.decorView.rootView as ViewGroup
    captureView(rootView,windows){
        val bitmap = it
        val maskView = MaskView(animModel, Pair(clickX, clickY), this, bitmap)
        rootView.addView(maskView)
        maskComplete()
        maskView.animActive(animTime) {
            rootView.removeView(maskView)
            maskAnimFinish()
        }
    }
}
private fun captureView(view: View, window: Window, bitmapCallback: (Bitmap)->Unit) {
    // Above Android O, use PixelCopy
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val location = IntArray(2)
    view.getLocationInWindow(location)
    PixelCopy.request(window,
        Rect(location[0], location[1], location[0] + view.width, location[1] + view.height),
        bitmap,
        {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        },
        Handler(Looper.getMainLooper())
    )
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
            addListener(onEnd = {
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