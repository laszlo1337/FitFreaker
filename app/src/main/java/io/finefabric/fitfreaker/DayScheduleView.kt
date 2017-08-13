package io.finefabric.fitfreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.*

/**
 * Created by laszlo on 2017-08-05.
 */

class DayScheduleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    val MINUTES_IN_DAY = 1440f
    val MINUTES_IN_HOUR = 60f
    val HOURS_IN_DAY = 24
    val DEFAULT_HOUR_HEIGHT_MULTIPLIER = 1.2f
    val TIME_PATTERN = "00:00"

    val screenStartPixelX = 0f

    val screenEndPixelX = resources.displayMetrics.widthPixels.toFloat()
    var hourHeightMultiplier = DEFAULT_HOUR_HEIGHT_MULTIPLIER
    var calculatedDayHeight: Int

    var hourTextMargin: Float
    val screenRect = Rect()
    val hourTextRect = Rect()

    var hourTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var backgroundPaint = Paint()
    var eventSeparatorPaint = Paint()
    var addNewEventTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var addNewEventPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var touchedHourRect = RectF()

    var hourRects = LinkedList<RectF>()

    private lateinit var onAddEventClickListener: DayScheduleView.OnAddEventClickListener

    init {
        calculatedDayHeight = (applyDimension(MINUTES_IN_DAY) * hourHeightMultiplier).toInt()

        eventSeparatorPaint.strokeWidth = applyDimension(0.5f)
        eventSeparatorPaint.color = Color.GRAY

        backgroundPaint.color = Color.WHITE
        addNewEventTextPaint.color = Color.WHITE
        addNewEventTextPaint.textSize = applyDimension(18f)
        addNewEventPaint.color = Color.BLACK

        hourTextPaint.textSize = applyDimension(14f)
        hourTextPaint.isSubpixelText = true
        hourTextPaint.color = Color.GRAY
        hourTextPaint.getTextBounds(TIME_PATTERN, 0, TIME_PATTERN.length, hourTextRect)

        hourTextMargin = applyDimension(48f)

        initializeHourRects()
    }

    private fun initializeHourRects() {
        var hourStart = 0f
        var hourEnd = 0f

        for (h in 1..HOURS_IN_DAY) {
            hourStart = hourEnd + applyDimension(0.5f)
            hourEnd = h * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier
            hourRects.add(RectF(hourTextMargin, hourStart, screenEndPixelX - applyDimension(4f), hourEnd - applyDimension(0.5f)))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = calculatedDayHeight
        screenRect.set(0, 0, width, height)
        setMeasuredDimension(width, height)
//        Log.d("measureSpec: ", "wMeasureSpec: ".plus(widthMeasureSpec).plus(" hMeasureSpec: ".plus(heightMeasureSpec)))
//        Log.d("ON MEASURE", "width: ".plus(width).plus(" height: ").plus(height))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_SCROLL -> return false
            MotionEvent.ACTION_DOWN -> {
                Log.d("TOUCH EVENT", "x: " + event.x + " y: " + event.y)
                handleTouch(event)
                return true
            }
        }

        return false
    }

    private fun handleTouch(event: MotionEvent) {
        for (rect in hourRects) {
            if (event.y > rect.top && event.y < rect.bottom) {
                if (touchedHourRect.equals(rect)) {
                    onAddEventClickListener?.onClick()
                }
                touchedHourRect = rect
                invalidate()
//                var sc = parent as ScrollView
//                thread { sc.smoothScrollTo(0, touchedHourRect.top.toInt()) }
                break
            }
        }
    }


override fun onDraw(canvas: Canvas?) {
    canvas?.drawRect(screenRect, backgroundPaint)
    drawHourTextAndSeparators(canvas)

    touchedHourRect.let {
        drawAddNewEventRect(canvas)
    }
}

private fun drawHourTextAndSeparators(canvas: Canvas?) {
    for (i in 0..HOURS_IN_DAY) {
        canvas?.drawText(if (i.toString().length < 2) "0%d:00".format(i) else "%2d:00".format(i),
                applyDimension(4f),
                (i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier) - hourTextRect.centerY(),
                hourTextPaint)
        canvas?.drawLine(hourTextMargin,
                i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier,
                width.toFloat() - applyDimension(4f),
                i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier,
                eventSeparatorPaint)
    }
}

fun drawAddNewEventRect(canvas: Canvas?) {
    canvas?.drawRoundRect(touchedHourRect, 15f, 15f, addNewEventPaint)
    canvas?.drawText("+ Add new event", touchedHourRect.left + applyDimension(16f), touchedHourRect.centerY(), addNewEventTextPaint)
}

fun applyDimension(valueInDip: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDip, resources.displayMetrics)
}

fun setOnAddEventClickListener(listener: OnAddEventClickListener) {
    this.onAddEventClickListener = listener
}

interface OnAddEventClickListener {
    fun onClick()
}
}