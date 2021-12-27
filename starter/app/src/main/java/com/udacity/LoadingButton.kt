package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    public var optionSelected = 0

    private var widthSize = 0
    private var heightSize = 0
    private var background = 0
    private var loadingColor = 0
    private var circleColor = 0
    private var textColor = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    @Volatile
    private var progress: Double = 0.0


    private val valueAnimator: ValueAnimator

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        isClickable = true
        valueAnimator = ValueAnimator.ofFloat(0f, 100f).apply {
            duration = 1000
            start()
        }
        valueAnimator.addUpdateListener {
            Log.i("button", (it.animatedValue as Float).toString())
            progress = (it.animatedValue as Float).toDouble()
            invalidate()
            requestLayout()
        }
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            background = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            setBackgroundColor(background)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            paint.setColor(textColor)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)

        }
    }

    override fun performClick(): Boolean {
        super.performClick()

        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading



        valueAnimator.start()




        //invalidate()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        if (buttonState == ButtonState.Loading && progress < 100.0) {
            paint.setColor(loadingColor)
            canvas?.drawRect(0f, 0f, (width * (progress/100)).toFloat(), height.toFloat(), paint)
            paint.setColor(textColor)
            canvas?.drawText(context.getString(R.string.button_loading), (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
            paint.setColor(circleColor)
            canvas?.drawArc((width-height).toFloat(), 0f, width.toFloat(), height.toFloat(), 0f, (360.0*(progress/100.0)).toFloat(), true, paint )
            if (optionSelected == -1) {
                if (progress == 0.0) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.no_option_selected),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        else {
            paint.setColor(textColor)
            canvas?.drawText(context.getString(R.string.button_name), (width / 2).toFloat(), ((height + 30) / 2).toFloat(), paint)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun hasCompletedDownload() {

        // cancel the animation when file is downloaded
        Log.i("button", "completed")

        valueAnimator.cancel()

        buttonState = ButtonState.Completed

        invalidate()

        requestLayout()

    }

}