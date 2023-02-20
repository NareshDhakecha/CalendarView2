package com.ndsoftwares.calendarviewexample.fragments

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.children
import com.ndsoftwares.calendarview.model.CalendarDay
import com.ndsoftwares.calendarview.model.CalendarMonth
import com.ndsoftwares.calendarview.model.DayOwner
import com.ndsoftwares.calendarview.ui.DayBinder
import com.ndsoftwares.calendarview.ui.MonthHeaderFooterBinder
import com.ndsoftwares.calendarview.ui.ViewContainer
import com.ndsoftwares.calendarview.utils.Size
import com.ndsoftwares.calendarviewexample.*
import com.ndsoftwares.calendarviewexample.databinding.Example6CalendarDayBinding
import com.ndsoftwares.calendarviewexample.databinding.Example6CalendarHeaderBinding
import com.ndsoftwares.calendarviewexample.databinding.Example6FragmentBinding
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class Example6MonthView(context: Context) : CardView(context) {

    init {
        setCardBackgroundColor(context.getColorCompat(R.color.example_6_month_bg_color))
        radius = dpToPx(8, context).toFloat()
        elevation = 8f
    }
}

class Example6Fragment : BaseFragment(R.layout.example_6_fragment), HasBackButton {
    override val titleRes: Int = R.string.example_6_title

    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")

    private lateinit var bv: Example6FragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bv = Example6FragmentBinding.bind(view)
        // Setup custom day size to fit two months on the screen.
        val dm = DisplayMetrics()
        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)

        bv.exSixCalendar.apply {
            // We want the immediately following/previous month to be
            // partially visible so we multiply the total width by 0.73
            val monthWidth = (dm.widthPixels * 0.73).toInt()
            val dayWidth = monthWidth / 7
            val dayHeight = (dayWidth * 1.73).toInt() // We don't want a square calendar.
            daySize = Size(dayWidth, dayHeight)

            // Add margins around our card view.
            val horizontalMargin = dpToPx(8, requireContext())
            val verticalMargin = dpToPx(14, requireContext())
            setMonthMargins(start = horizontalMargin, end = horizontalMargin, top = verticalMargin, bottom = verticalMargin)
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = Example6CalendarDayBinding.bind(view).exSixDayText
        }
        bv.exSixCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val textView = container.textView

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.text = day.date.dayOfMonth.toString()
                    textView.makeVisible()
                } else {
                    textView.makeInVisible()
                }
            }
        }

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        bv.exSixCalendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
        bv.exSixCalendar.scrollToMonth(currentMonth)

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val binding = Example6CalendarHeaderBinding.bind(view)
            val textView = binding.exSixMonthText
            val legendLayout = binding.legendLayout.root
        }
        bv.exSixCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.textView.text = titleFormatter.format(month.yearMonth)
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                        tv.setTextColorRes(R.color.example_6_black)
                    }
                }
            }
        }
    }
}