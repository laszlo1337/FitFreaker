package io.finefabric.fitfreaker

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

/**
 * @author Leszek Janiszewski
 */
class DayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {

    private val dayshedule = DayScheduleView(context)

    init {
        addView(dayshedule)
    }

    fun setOnAddEventClickListener(listener: DayScheduleView.OnAddEventClickListener){
        dayshedule.setOnAddEventClickListener(listener)
    }
}
