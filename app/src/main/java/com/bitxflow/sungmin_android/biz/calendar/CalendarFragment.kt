package com.bitxflow.sungmin_android.biz.calendar

import android.app.AlertDialog
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.send.SendServer
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private var user_id: String = ""

    private var adapter: ArrayAdapter<String>? = null
    private var userDB: MemberDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_calendar, null)

        val ModifyRunnable = Runnable {

            try {
                val cal = Calendar.getInstance()
                val year = cal.get(Calendar.YEAR).toString()
                val month = cal.get(Calendar.MONTH).toString()
                getCalendarList().execute(year,month)
            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val modifyThread = Thread(ModifyRunnable)
        modifyThread.start()

        view.calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator(),
            OneDayDecorator())


        view.calendarView.setOnMonthChangedListener { widget, date ->
            val year = date.year.toString()
            val month = date.month.toString()
            getCalendarList().execute(year,month)
        }
        return view
    }


    internal inner class getCalendarList : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            return su.getCalendar(params[0], params[1])
        }

        override fun onPostExecute(result: String) {
            Log.d("bitx_log", "calendar result :$result")

            activity!!.main_progressText.text = ""
            activity!!.main_progressbar.visibility = View.GONE

            val `object` = JSONObject(result)
            val count: Int = `object`.getInt("count")

            val planList = `object`.getJSONArray("plans")

            var startDyList: MutableList<String> = ArrayList()
            var endDyList: MutableList<String> = ArrayList()
            var titleList: MutableList<String> = ArrayList()

            var dates : MutableList<CalendarDay> = ArrayList()

            for (i in 0 until planList.length()) {
                val json = planList.getJSONObject(i)

                val startDy = json.getString("startDy")

                val transFormat =
                    SimpleDateFormat("yyyy-MM-dd")
                val startDate = transFormat.parse(startDy)

                var day : CalendarDay = CalendarDay.from(startDate)
                dates.add(day)

                var sameDate = false


                for (j in 0 until startDyList.size) {
                    val startDyFromList = startDyList.get(j)
                    val startDate = transFormat.parse(startDyFromList)
                    var calStartDate = Calendar.getInstance()
                    calStartDate.time = startDate

                    if(day.day == calStartDate.get(Calendar.DATE))// 같은날짜?
                    {
                        var title = titleList.get(j)
                        title = title + "\n" + startDy.substring(10,16) + " " + json.getString("title")
                        titleList.set(j,title)
                        sameDate = true
                    }
                }

                if(!sameDate) {
                    startDyList!!.add(startDy)
                    endDyList!!.add(json.getString("endDy"))
                    titleList!!.add(startDy.substring(10,16) + " " + json.getString("title"))
                }

            }

            view!!.calendarView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
                Log.d("bitx_log","day selceted , " + date.toString())
                for(i in 0 until startDyList.size)
                {
                    val transFormat =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val startDate = transFormat.parse(startDyList.get(i))
                    val cal = Calendar.getInstance()
                    cal.time = startDate
                    if(cal.get(Calendar.DATE) == date.day) {
                       Log.d("bitx_log", "same" + startDyList.get(i))
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(""+cal.get(Calendar.YEAR)+"년 " + (cal.get(Calendar.MONTH)+1) +"월 " +cal.get(Calendar.DATE)+"일 일정")
                        builder.setMessage(
                            titleList.get(i)
                        )

                        builder.setPositiveButton("확인") { dialog, which ->
                            dialog.dismiss()
                        }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            })
//
            view!!.calendarView.addDecorators(EventDecorator(Color.RED, dates,activity!!))

            activity!!.nav_home_ll.isClickable = true
            activity!!.nav_photo_ll.isClickable = true
            activity!!.nav_letter_ll.isClickable = true
            activity!!.nav_plan_ll.isClickable = true
            activity!!.nav_all_board_ll.isClickable = true
            activity!!.nav_board_ll.isClickable = true
            activity!!.nav_setting_ll.isClickable = true
        }
    }
}