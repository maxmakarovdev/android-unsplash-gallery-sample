package com.maxmakarov.feature.view.image

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Matrix.MSCALE_X
import android.graphics.Matrix.MSCALE_Y
import android.graphics.Matrix.MTRANS_X
import android.graphics.Matrix.MTRANS_Y
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ScaleGestureDetectorCompat
import kotlin.math.abs

class StretchableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), OnScaleGestureListener {

    private val imgMatrix = Matrix()
    private var startMatrix = Matrix()
    private val matrixValues = FloatArray(9)
    private var startValues: FloatArray? = null

    private var doubleTapDetected = false
    private var singleTapDetected = false
    private var calculatedMinScale = MIN_SCALE
    private var calculatedMaxScale = MAX_SCALE
    private val bounds = RectF()
    private val last = PointF(0f, 0f)
    private var startScaleType: ScaleType
    private var startScale = 1f
    private var scaleBy = 1f
    private var currentScaleFactor = 1f
    private var previousPointerCount = 1
    private var currentPointerCount = 0
    private var scaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)
    private var resetAnimator: ValueAnimator? = null
    private var startY = 0f
    private var skip = false
    private var gestureDetector: GestureDetector

    var dragDownListener: DragDownListener? = null

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                doubleTapDetected = e.action == ACTION_UP
                singleTapDetected = false
                return false
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                singleTapDetected = true
                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                singleTapDetected = false
                return false
            }

            override fun onDown(e: MotionEvent) = true
        })
        ScaleGestureDetectorCompat.setQuickScaleEnabled(scaleDetector, false)
        startScaleType = scaleType
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != null) {
            super.setScaleType(scaleType)
            startScaleType = scaleType
            startValues = null
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        scaleType = startScaleType
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        scaleType = startScaleType
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        scaleType = startScaleType
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        scaleType = startScaleType
    }

    /**
     * Update the bounds of the displayed image based on the current matrix.
     */
    private fun updateBounds(values: FloatArray) {
        if (drawable != null) {
            bounds[values[MTRANS_X], values[MTRANS_Y], drawable.intrinsicWidth * values[MSCALE_X] + values[MTRANS_X]] =
                drawable.intrinsicHeight * values[MSCALE_Y] + values[MTRANS_Y]
        }
    }

    private fun currentDisplayedWidth() = (drawable?.intrinsicWidth ?: 0) * matrixValues[MSCALE_X]
    private fun currentDisplayedHeight() = (drawable?.intrinsicHeight ?: 0) * matrixValues[MSCALE_Y]

    /**
     * Remember our starting values so we can animate our image back to its original position.
     */
    private fun setStartValues() {
        startValues = FloatArray(9)
        startMatrix = Matrix(imageMatrix)
        startMatrix.getValues(startValues)
        calculatedMinScale = MIN_SCALE * startValues!![MSCALE_X]
        calculatedMaxScale = MAX_SCALE * startValues!![MSCALE_X]
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable && isEnabled) {
            if (scaleType != ScaleType.MATRIX) {
                super.setScaleType(ScaleType.MATRIX)
            }
            if (startValues == null) {
                setStartValues()
            }
            currentPointerCount = event.pointerCount

            //get the current state of the image matrix, its values, and the bounds of the drawn bitmap
            imgMatrix.set(imageMatrix)
            imgMatrix.getValues(matrixValues)
            updateBounds(matrixValues)
            scaleDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            if (doubleTapDetected) {
                doubleTapDetected = false
                singleTapDetected = false
                if (matrixValues[MSCALE_X] != startValues!![MSCALE_X]) {
                    reset(true)
                } else {
                    val zoomMatrix = Matrix(imgMatrix)
                    zoomMatrix.postScale(
                        DOUBLE_TAP_SCALE_FACTOR,
                        DOUBLE_TAP_SCALE_FACTOR,
                        scaleDetector.focusX,
                        scaleDetector.focusY
                    )
                    animateScaleAndTranslationToMatrix(zoomMatrix)
                }
                return true
            } else if (!singleTapDetected) {
                /* if the event is a down touch, or if the number of touch points changed,
                 * we should reset our start point, as event origins have likely shifted to a
                 * different part of the screen*/
                if (event.actionMasked == ACTION_DOWN || currentPointerCount != previousPointerCount) {
                    last[scaleDetector.focusX] = scaleDetector.focusY
                    skip = if (currentPointerCount == 1) event.y < bounds.top else true
                    startY = event.y
                } else if (event.actionMasked == ACTION_MOVE) {
                    val focusX = scaleDetector.focusX
                    val focusY = scaleDetector.focusY
                    if (allowTranslate()) {
                        val xdistance = getXDistance(focusX, last.x)
                        val ydistance = getYDistance(focusY, last.y)
                        imgMatrix.postTranslate(xdistance, ydistance)
                    }

                    //zoom
                    imgMatrix.postScale(scaleBy, scaleBy, focusX, focusY)
                    currentScaleFactor = matrixValues[MSCALE_X] / startValues!![MSCALE_X]
                    imageMatrix = imgMatrix
                    last[focusX] = focusY

                    if (!skip && currentScaleFactor == 1f) {
                        dragDownListener?.onDragDown(abs(event.y - startY))
                    }
                }
                if (event.actionMasked == ACTION_UP || event.actionMasked == ACTION_CANCEL) {
                    scaleBy = 1f
                    resetImage()

                    if (!skip && currentScaleFactor == 1f) {
                        dragDownListener?.onDragEnded(abs(event.y - startY))
                    }
                }
            }
            parent.requestDisallowInterceptTouchEvent(disallowParentTouch())

            previousPointerCount = currentPointerCount
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun disallowParentTouch() = currentPointerCount > 1 || currentScaleFactor > 1.0f || isAnimating()

    private fun allowTranslate() = currentScaleFactor >= 1.0f

    private fun isAnimating() = resetAnimator?.isRunning ?: false

    private fun resetImage() {
        if (matrixValues[MSCALE_X] <= startValues!![MSCALE_X]) {
            reset(true)
        } else {
            center()
        }
    }

    private fun center() {
        animateTranslationX()
        animateTranslationY()
    }

    fun reset(animate: Boolean) {
        if (animate) {
            animateToStartMatrix()
        } else {
            imageMatrix = startMatrix
        }
    }

    private fun animateToStartMatrix() {
        animateScaleAndTranslationToMatrix(startMatrix)
    }

    private fun animateScaleAndTranslationToMatrix(targetMatrix: Matrix) {
        val targetValues = FloatArray(9)
        targetMatrix.getValues(targetValues)
        val beginMatrix = Matrix(imageMatrix)
        beginMatrix.getValues(matrixValues)

        val xsDiff = targetValues[MSCALE_X] - matrixValues[MSCALE_X]
        val ysDiff = targetValues[MSCALE_Y] - matrixValues[MSCALE_Y]
        val xtDiff = targetValues[MTRANS_X] - matrixValues[MTRANS_X]
        val ytDiff = targetValues[MTRANS_Y] - matrixValues[MTRANS_Y]
        resetAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = RESET_DURATION.toLong()

            addUpdateListener(object : AnimatorUpdateListener {
                val activeMatrix = Matrix(imageMatrix)
                val values = FloatArray(9)
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    val value = animation.animatedValue as Float
                    activeMatrix.set(beginMatrix)
                    activeMatrix.getValues(values)
                    values[MTRANS_X] = values[MTRANS_X] + xtDiff * value
                    values[MTRANS_Y] = values[MTRANS_Y] + ytDiff * value
                    values[MSCALE_X] = values[MSCALE_X] + xsDiff * value
                    values[MSCALE_Y] = values[MSCALE_Y] + ysDiff * value
                    activeMatrix.setValues(values)
                    imageMatrix = activeMatrix
                }
            })
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    imageMatrix = targetMatrix
                }
            })

            start()
        }
    }

    private fun animateTranslationX() {
        if (currentDisplayedWidth() > width) {
            when {
                bounds.left > 0 -> animateMatrixIndex(MTRANS_X, 0f)
                bounds.right < width -> animateMatrixIndex(MTRANS_X, bounds.left + width - bounds.right)
            }
        } else {
            when {
                bounds.left < 0 -> animateMatrixIndex(MTRANS_X, 0f)
                bounds.right > width -> animateMatrixIndex(MTRANS_X, bounds.left + width - bounds.right)
            }
        }
    }

    private fun animateTranslationY() {
        if (currentDisplayedHeight() > height) {
            when {
                bounds.top > 0 -> animateMatrixIndex(MTRANS_Y, 0f)
                bounds.bottom < height -> animateMatrixIndex(MTRANS_Y, bounds.top + height - bounds.bottom)
            }
        } else {
            animateMatrixIndex(MTRANS_Y, height / 2f - currentDisplayedHeight() / 2f)
        }
    }

    private fun animateMatrixIndex(index: Int, to: Float) {
        ValueAnimator.ofFloat(matrixValues[index], to).apply {
            duration = RESET_DURATION.toLong()

            addUpdateListener(object : AnimatorUpdateListener {
                val values = FloatArray(9)
                var current = Matrix()
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    current.set(imageMatrix)
                    current.getValues(values)
                    values[index] = animation.animatedValue as Float
                    current.setValues(values)
                    imageMatrix = current
                }
            })

            start()
        }
    }

    private fun getXDistance(toX: Float, fromX: Float): Float {
        var xdistance = toX - fromX
        when {
            bounds.right + xdistance < 0 -> xdistance = -bounds.right
            bounds.left + xdistance > width -> xdistance = width - bounds.left
        }
        return xdistance
    }

    private fun getYDistance(toY: Float, fromY: Float): Float {
        var ydistance = toY - fromY
        when {
            bounds.bottom + ydistance < 0 -> ydistance = -bounds.bottom
            bounds.top + ydistance > height -> ydistance = height - bounds.top
        }
        return ydistance
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        //calculate value we should scale by, ultimately the scale will be startScale*scaleFactor
        scaleBy = startScale * detector.scaleFactor / matrixValues[MSCALE_X]

        //what the scaling should end up at after the transformation
        val projectedScale = scaleBy * matrixValues[MSCALE_X]

        //clamp to the min/max if it's going over
        when {
            projectedScale < calculatedMinScale -> scaleBy = calculatedMinScale / matrixValues[MSCALE_X]
            projectedScale > calculatedMaxScale -> scaleBy = calculatedMaxScale / matrixValues[MSCALE_X]
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        startScale = matrixValues[MSCALE_X]
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        scaleBy = 1f
    }

    interface DragDownListener {
        fun onDragDown(distance: Float)
        fun onDragEnded(distance: Float)
    }

    companion object {
        private const val MIN_SCALE = 0.6f
        private const val MAX_SCALE = 8f
        private const val RESET_DURATION = 150
        private const val DOUBLE_TAP_SCALE_FACTOR = 3f
    }
}