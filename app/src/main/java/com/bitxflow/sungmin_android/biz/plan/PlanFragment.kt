package com.bitxflow.sungmin_android.biz.plan

import android.content.Intent
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
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.homeletter.HomeLetterActivity
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_homeletter.*
import kotlinx.android.synthetic.main.fragment_plan.*
import org.json.JSONObject
import java.util.ArrayList

class PlanFragment : Fragment() {

    private var user_id: String = ""
    private var className: String = ""
    private var userDB: MemberDatabase? = null
    private var isAbsent: Boolean = false

//    private val noticeList: ArrayList<String>? = null
//    private val noticeDateList: ArrayList<String>? = null
//    private val noticeContentList: ArrayList<String>? = null

    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id").toString()
        className = arguments!!.getString("className").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plan, null)

        val startRunnable = Runnable {

            try {
                getPlanListTask().execute(user_id,className)
            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val thread = Thread(startRunnable)
        thread.start()

        return view
    }

    internal inner class getPlanListTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val className = params[1]
            val url = "plan"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("classname", className)
            return su.requestPOST(url, postDataParams)
        }

        override fun onPostExecute(result: String) {
            if(result == "")
                Toast.makeText(activity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            else {
                try {
                    activity!!.main_progressText.text = ""
                    activity!!.main_progressbar.visibility = View.GONE

                    val `object` = JSONObject(result)
                    val count: Int = `object`.getInt("educationPlanCount")
                    val homeLetterList = `object`.getJSONArray("educationPlanList")

                    var noticeList: MutableList<String> = ArrayList()
                    var homeLetter_img_url: MutableList<String> = ArrayList()
                    var homeLetterDateList: MutableList<String> = ArrayList()
                    if (count == 0) {
                        var noticeList: MutableList<String> = ArrayList()

                        noticeList.add("교육활동안이 없습니다")
                        if (isAdded) {
                            adapter = ArrayAdapter(
                                activity!!,
                                android.R.layout.simple_list_item_1,
                                noticeList
                            )

                            plan_listview.adapter = adapter

                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        for (i in 0 until count) {
                            val json = homeLetterList.getJSONObject(i)

                            noticeList.add(json.getString("educationPlanDate") + "     " + "교육활동안")
                            homeLetter_img_url!!.add(
                                "https://d1d2thkw8tiq2x.cloudfront.net/" + json.getString(
                                    "educationPlanImagePath"
                                )
                            )
                            homeLetterDateList!!.add(json.getString("educationPlanDate"))

                        }

                        if (isAdded) {
                            adapter = ArrayAdapter(
                                activity!!,
                                android.R.layout.simple_list_item_1,
                                noticeList
                            )

                            plan_listview.adapter = adapter

                            plan_listview.setOnItemClickListener { parent, view, position, id ->
                                val nextIntent = Intent(context, HomeLetterActivity::class.java)
                                nextIntent.putExtra("user_id", user_id)
                                nextIntent.putExtra("letterDay", homeLetterDateList.get(position))
                                nextIntent.putExtra("content", homeLetter_img_url.get(position))
                                startActivity(nextIntent)
                            }

                            val footer: View =
                                layoutInflater.inflate(R.layout.home_list_footer, null, false)
                            plan_listview.addFooterView(footer)
                        }
                    }
                }catch (e : Exception)
                {
                }
            }
            if(isAdded) {
                activity!!.nav_home_ll.isClickable = true
                activity!!.nav_photo_ll.isClickable = true
                activity!!.nav_letter_ll.isClickable = true
                activity!!.nav_plan_ll.isClickable = true
                activity!!.nav_all_board_ll.isClickable = true
                activity!!.nav_board_ll.isClickable = true
                activity!!.nav_setting_ll.isClickable = true
                activity!!.nav_calendar_ll.isClickable = true
            }
        }
    }
}