package com.bitxflow.sungmin_android.biz.calendar

import android.R
import android.app.Activity
import android.graphics.drawable.Drawable
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan


class EventDecorator(
    color: Int,
    dates: MutableList<CalendarDay>,
    context: Activity
) :
    DayViewDecorator {
    private val color: Int
    private val dates:  MutableList<CalendarDay>
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
//        view.setSelectionDrawable(drawable)
        view.addSpan(DotSpan(color)); // 날자밑에 점
    }

    init {
//        drawable = context.resources.getDrawable(R.drawable.more)
        this.color = color
        this.dates = dates
    }
}
