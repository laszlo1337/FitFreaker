package io.finefabric.fitfreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import org.joda.time.DateTime
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

    private var leftMargin = applyDimension(8f)
    var hourTextMargin: Float
    val screenRect = Rect()

    val hourTextRect = Rect()
    var hourTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var backgroundPaint = Paint()
    var hourSeparatorPaint = Paint()
    var addNewEventTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var timelinePaint = Paint()
    var timelineDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var addNewEventPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var noneRect = RectF()
    var touchedHourRect = noneRect

    var hourRects = LinkedList<RectF>()
    var timelineAndMarkNewEventColor = Color.parseColor("#AEB0FF")

    private var onAddEventClickListener: DayScheduleView.OnAddEventClickListener? = null

    init {
        calculatedDayHeight = applyDimension(MINUTES_IN_DAY * hourHeightMultiplier).toInt()

        hourSeparatorPaint.strokeWidth = applyDimension(0.5f)
        hourSeparatorPaint.color = Color.parseColor("#EEEEEE")

        backgroundPaint.color = Color.WHITE
        addNewEventTextPaint.color = Color.WHITE
        addNewEventTextPaint.textSize = applyDimension(18f)
        addNewEventPaint.color = timelineAndMarkNewEventColor
        timelinePaint.color = timelineAndMarkNewEventColor
        timelinePaint.strokeWidth = applyDimension(2f)
        timelineDotPaint.color = timelineAndMarkNewEventColor

        hourTextPaint.textSize = applyDimension(13f)
        hourTextPaint.color = Color.GRAY
        hourTextPaint.getTextBounds(TIME_PATTERN, 0, TIME_PATTERN.length, hourTextRect)

        hourTextMargin = applyDimension(60f)

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
        val height = calculatedDayHeight + applyDimension(16f).toInt()
        screenRect.set(0, 0, width, height)
        setMeasuredDimension(width, height)
//        Log.d("measureSpec: ", "wMeasureSpec: ".plus(widthMeasureSpec).plus(" hMeasureSpec: ".plus(heightMeasureSpec)))
//        Log.d("ON MEASURE", "width: ".plus(width).plus(" height: ").plus(height))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_SCROLL -> {
                touchedHourRect = noneRect
                invalidate()
                return true
            }
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

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(screenRect, backgroundPaint)
        drawHourTextAndSeparators(canvas)
        drawTimeline(canvas)

        drawAddNewEventRect(canvas)

    }

    private fun drawTimeline(canvas: Canvas) {
        val time = DateTime.now()
        val linePositionX = hourTextMargin - applyDimension(10f)
        val currentTime = applyDimension(time.minuteOfDay().get().toFloat() * hourHeightMultiplier)

        canvas.drawLine(linePositionX, 0f, linePositionX, currentTime, timelinePaint)

        canvas.drawCircle(linePositionX, currentTime, applyDimension(4f), timelineDotPaint)
    }

    private fun drawHourTextAndSeparators(canvas: Canvas) {
        for (i in 0..HOURS_IN_DAY) {
            canvas.drawLine(hourTextMargin,
                    i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier,
                    width.toFloat() - applyDimension(4f),
                    i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier,
                    hourSeparatorPaint)
            if (i == 0) continue
            canvas.drawText(if (i < 10) "0%d:00".format(i) else "%2d:00".format(i),
                    leftMargin,
                    (i * applyDimension(MINUTES_IN_HOUR) * hourHeightMultiplier) - hourTextRect.centerY(),
                    hourTextPaint)
        }
    }

    fun drawAddNewEventRect(canvas: Canvas) {
        canvas.drawRoundRect(touchedHourRect, 15f, 15f, addNewEventPaint)
        canvas.drawText("+ Add new training", touchedHourRect.left + applyDimension(16f), touchedHourRect.centerY(), addNewEventTextPaint)
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