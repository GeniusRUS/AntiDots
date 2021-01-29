package com.genius.antidots

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import kotlin.math.min

@Suppress("UNUSED")
class AntiDotsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : View(
    context,
    attributeSet,
    defStyle
) {

    private val paint = Paint()
    private var trackPosition: Float = 0F
    private var dotsCount: Int = 0
    private var dotsSpace: Float = 0F
    private var dotsDrawable: Drawable? = null
    private var trackDrawable: Drawable? = null

    private var dotBitmap: Bitmap? = null
    private var trackBitmap: Bitmap? = null

    private var paddingRect: RectF = RectF(0F, 0F, 0F, 0F)

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.AntiDotsView)
        trackPosition = typedArray.getFloat(R.styleable.AntiDotsView_trackPosition, 0F).coerceAtLeast(
            0F
        )
        dotsCount = typedArray.getInt(R.styleable.AntiDotsView_dotsCount, 0).coerceAtLeast(0)
        dotsDrawable = updateDrawableInternal(
            typedArray.getResourceId(
                R.styleable.AntiDotsView_dotDrawable,
                0
            )
        )
        trackDrawable = updateDrawableInternal(
            typedArray.getResourceId(
                R.styleable.AntiDotsView_trackDrawable,
                0
            )
        )
        dotsSpace = typedArray.getDimension(R.styleable.AntiDotsView_dotsSpace, 1F.toPx)
        typedArray.recycle()

        dotBitmap = dotsDrawable?.toBitmap()
        trackBitmap = trackDrawable?.toBitmap()

        if (isInEditMode) {
            dotsCount = if (dotsCount == 0) 4 else dotsCount
            trackPosition = if (trackPosition == 0F) {
                2F.coerceAtMost(
                    dotsCount.toFloat().minus(1F).coerceAtLeast(0F)
                )
            } else {
                trackPosition.coerceAtMost(dotsCount.toFloat())
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = dotsDrawable?.let { drawable ->
            (drawable.intrinsicWidth + dotsSpace.toInt()) * dotsCount - dotsSpace.toInt()
        } ?: 0
        val desiredHeight = dotsDrawable?.intrinsicHeight ?: 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        paddingRect.set(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            paddingRight.toFloat(),
            paddingBottom.toFloat()
        )

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize) //Can't be bigger than...
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth //Be whatever you want
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize //Must be this size
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize) //Can't be bigger than...
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight //Be whatever you want
        }

        setMeasuredDimension(
            width + paddingRect.left.toInt() + paddingRect.right.toInt(),
            height + paddingRect.top.toInt() + paddingRect.bottom.toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        for (step in 0 until dotsCount) {
            dotBitmap?.apply {
                canvas.drawBitmap(this, paddingRect.left + (width + dotsSpace) * step, paddingRect.top, paint)
            }
        }

        if (dotsCount >= 1) {
            trackBitmap?.apply {
                canvas.drawBitmap(
                    this,
                    paddingRect.left + ((dotBitmap?.width?.toFloat() ?: 0F) + dotsSpace) * trackPosition,
                    paddingRect.top,
                    paint
                )
            }
        }
    }

    private fun updateDotsCountInternal(dotsCount: Int) {
        this.dotsCount = dotsCount.coerceAtLeast(0)
        invalidate()
    }

    private fun updateTrackPositionInternal(position: Float) {
        trackPosition = position
            .coerceAtMost(
                dotsCount.toFloat().minus(1).coerceAtLeast(0F)
            )
            .coerceAtLeast(0F)
        invalidate()
    }

    private fun updateDrawableInternal(@DrawableRes resourceId: Int?): Drawable? {
        return resourceId?.run {
            if (resourceId == 0) return null
            VectorDrawableCompat.create(context.resources, this, context.theme)
        }
    }

    fun updateDotsCount(count: Int) {
        updateDotsCountInternal(count)
    }

    fun updateTrackPosition(position: Float) {
        updateTrackPositionInternal(position)
    }

    fun updateDotDrawable(@DrawableRes resourceId: Int?) {
        dotsDrawable = updateDrawableInternal(resourceId)
        dotBitmap = dotsDrawable?.toBitmap()
        requestLayout()
    }

    fun updateTrackDrawable(@DrawableRes resourceId: Int?) {
        trackDrawable = updateDrawableInternal(resourceId)
        trackBitmap = trackDrawable?.toBitmap()
        requestLayout()
    }

    private val Float.toPx: Float
        get() = this * context.resources.displayMetrics.density
}