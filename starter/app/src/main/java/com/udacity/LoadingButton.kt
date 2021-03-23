package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

private const val TEXT_SIZE = 60f
private const val CIRCLE_MARGIN = 40
private const val RADIUS_HEIGHT_RATIO = 0.2f
private const val TAG = "LoadingButton"

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var backColor = 0
    private var frontColor = 0

    private var backRect = RectF()
    private var backPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.CYAN
    }

    private var frontRect = RectF()
    private var frontPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.CYAN
    }

    private var text = "Download"
    private var boundsRect = Rect()
    private var textPaint = Paint().apply {
        textSize = TEXT_SIZE
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }

    private var radius = 0f
    private val circleRect = RectF()
    private val circlePath = Path()
    private val circlePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = context.getColor(R.color.amberYellow)
    }

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                text = "We are loading"
                textPaint.getTextBounds(text, 0, text.length, boundsRect)
                startAnimation()
            }
            ButtonState.Completed -> {
                text = "Download"
                textPaint.getTextBounds(text, 0, text.length, boundsRect)
                animator.end()
            }
        }
    }

    private val animator = ValueAnimator()

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            frontColor = getColor(R.styleable.LoadingButton_loadingBarColor, 0)
            text = getString(R.styleable.LoadingButton_text) ?: "Download"
        }
        backPaint.color = backColor
        frontPaint.color = frontColor
        textPaint.getTextBounds(text, 0, text.length, boundsRect)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(backRect, backPaint)

        if (buttonState == ButtonState.Loading) {
            canvas.drawRect(frontRect, frontPaint)
            canvas.drawPath(circlePath, circlePaint)
        }
        val xText = (width / 2).toFloat()
        val yText = ((height + boundsRect.height())/ 2).toFloat()
        canvas.drawText(text, xText, yText, textPaint)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        backRect.set(0f,0f, w.toFloat(), h.toFloat())
        radius = RADIUS_HEIGHT_RATIO * h.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startAnimation() {
        animator.setFloatValues(0f, 1f)
        animator.apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = 4_000
        }
        animator.addUpdateListener {
            val progress = animator.animatedValue as Float
            val barProgress = progress * width
            val circleProgress = progress * 360

            // Loading Bar
            frontRect.set(0f,0f, barProgress, height.toFloat())

            // Circle
            val rectLeft = ((width/2) + boundsRect.width()/2 + CIRCLE_MARGIN).toFloat()
            val rectTop = (height/2) - radius
            val rectRight = rectLeft + radius * 2
            val rectBottom = rectTop + radius * 2

            circleRect.set(rectLeft, rectTop, rectRight, rectBottom)

            circlePath.reset()

            circlePath.moveTo(rectLeft + radius, (height /2).toFloat())
            circlePath.rLineTo(radius, 0f)
            circlePath.arcTo(circleRect, 0f, circleProgress)

            val dx = radius * cosd(circleProgress)
            val dy = radius * sind(circleProgress)

            circlePath.rLineTo(-dx, -dy)

            invalidate()
        }
        animator.start()
    }

    private fun cosd(degrees: Float): Float {
        return cos(Math.toRadians(degrees.toDouble())).toFloat()
    }

    private fun sind(degrees: Float): Float {
        return sin(Math.toRadians(degrees.toDouble())).toFloat()
    }

}