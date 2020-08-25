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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.MainActivity
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
    private var className: String = ""

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id").toString()
        className = arguments!!.getString("className").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_calendar, null)

        val ModifyRunnable = Runnable {

            try {
                getCalendarList().execute(user_id,className)
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

            val userId = params[0]
            val className = params[1]
            val url = "calendar"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("classname", className)

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            Log.d("bitx_log", "calendar result :$result")
            if(result.equals("")) {
                Toast.makeText(activity, "다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                try {
                    activity!!.main_progressText.text = ""
                    activity!!.main_progressbar.visibility = View.GONE

                    val `object` = JSONObject(result)
//                    val count: Int = `object`.getInt("eventCount")

                    val planList = `object`.getJSONArray("eventList")

                    var startDyList: MutableList<String> = ArrayList()
                    var endDyList: MutableList<String> = ArrayList()
                    var titleList: MutableList<String> = ArrayList()

                    var dates: MutableList<CalendarDay> = ArrayList()

                    for (i in 0 until planList.length()) {
                        val json = planList.getJSONObject(i)

                        val startDy = json.getString("from")

                        val transFormat =
                            SimpleDateFormat("yyyy-MM-dd")
                        val startDate = transFormat.parse(startDy)

                        var day: CalendarDay = CalendarDay.from(startDate)
                        dates.add(day)

                        var sameDate = false

                        for (j in 0 until startDyList.size) {
                            val startDyFromList = startDyList[j]
                            val startDate1 = transFormat.parse(startDyFromList)
                            var calStartDate = Calendar.getInstance()
                            calStartDate.time = startDate1

                            if (day.day == calStartDate.get(Calendar.DATE))// 같은날짜?
                            {
                                var title = titleList.get(j)
                                title = title + "\n" + startDy.substring(
                                    10,
                                    16
                                ) + " " + json.getString("title")
                                titleList[j] = title
                                sameDate = true
                            }
                        }

                        if (!sameDate) {
                            startDyList!!.add(startDy)
                            endDyList!!.add(json.getString("to"))
                            titleList!!.add(
                                startDy.substring(
                                    10,
                                    16
                                ) + " " + json.getString("title")
                            )
                        }

                    }

                    view!!.calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, selected ->
                        for (i in 0 until startDyList.size) {
                            val transFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val startDate = transFormat.parse(startDyList.get(i))
                            val cal = Calendar.getInstance()
                            cal.time = startDate
                            if (cal.get(Calendar.DATE) == date.day) {
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle(
                                    "" + cal.get(Calendar.YEAR) + "년 " + (cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(
                                        Calendar.DATE
                                    ) + "일 일정"
                                )
                                builder.setMessage(
                                    titleList[i]
                                )

                                builder.setPositiveButton("확인") { dialog, which ->
                                    dialog.dismiss()
                                }

                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                            }
                        }
                    })
                    view!!.calendarView.addDecorators(EventDecorator(Color.RED, dates, activity!!))
                } catch (e: Exception) {

                }
//
            }//else
            if(isAdded) {
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
}