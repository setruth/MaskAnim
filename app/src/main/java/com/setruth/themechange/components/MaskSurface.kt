package com.setruth.themechange.components

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.applyCanvas
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * 激活遮罩动画，
 * 动画模式，点击的x坐标，点击的y坐标
 */
typealias MaskAnimActive = (MaskAnimModel,Float,Float)->Unit

@Composable
fun MaskSurface(
    animTime: AnimationSpec<Float> = tween(800),
    maskComplete: (MaskAnimModel) -> Unit ,
    content: @Composable  (MaskAnimActive) -> Unit
) {
    var clickX by  remember { mutableStateOf(0f) }
    var clickY by  remember { mutableStateOf(0f) }
    var viewBounds by remember { mutableStateOf<Rect?>(null) }
    var maskAnimModel by remember { mutableStateOf(MaskAnimModel.EXPEND) }
    var animFinish by remember { mutableStateOf(false) }
    val rootView = LocalView.current.rootView
    var viewScreenshot by remember { mutableStateOf<Bitmap?>(null) }
    val maskRadius by animateFloatAsState(
        targetValue = when (maskAnimModel) {
            MaskAnimModel.EXPEND -> hypot(rootView.width.toFloat(), rootView.height.toFloat())
            MaskAnimModel.SHRINK -> 0f
        },
        animationSpec = animTime,
        label = "遮罩动画",
        finishedListener = {
            viewScreenshot = null
            animFinish = true
        }
    )
    val maskAnimActive: MaskAnimActive = clickEvent@{animModel,x,y->
        viewBounds?.let { bitmapBound ->
            viewScreenshot = Bitmap
                .createBitmap(
                    bitmapBound.width.roundToInt(),
                    bitmapBound.height.roundToInt(),
                    Bitmap.Config.ARGB_8888
                )
                .applyCanvas {
                    translate(-bitmapBound.left, -bitmapBound.top)
                    rootView.draw(this)
                }
            maskAnimModel=animModel
            clickX=x
            clickY=y
            maskComplete(maskAnimModel)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                viewBounds = it.boundsInWindow()
            }
            .drawWithCache {
                onDrawWithContent {
                    clipRect {
                        this@onDrawWithContent.drawContent()
                    }
                    if (viewScreenshot == null) return@onDrawWithContent
                    with(drawContext.canvas.nativeCanvas) {
                        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                        val layer = saveLayer(null, null)
                        when (maskAnimModel) {
                            MaskAnimModel.EXPEND -> {
                                drawBitmap(viewScreenshot!!, 0f, 0f, null)
                                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                                drawCircle(clickX, clickY, maskRadius, paint)
                            }

                            MaskAnimModel.SHRINK -> {
                                drawCircle(clickX, clickY, maskRadius, paint)
                                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                                drawBitmap(viewScreenshot!!, 0f, 0f, paint)
                            }
                        }
                        restoreToCount(layer)
                    }
                }
            }
    ) {
        content(maskAnimActive)
    }
}

enum class MaskAnimModel {
    EXPEND,
    SHRINK
}