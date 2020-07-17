package com.bitxflow.sungmin_android.biz.homeletter

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
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_homeletter.*
import org.json.JSONObject
import java.util.*

class HomeLetterFragment : Fragment() {

    private var user_id: String = ""
    private var className: String = ""
    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id")
        className = arguments!!.getString("className")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_homeletter, null)

        val startRunnable = Runnable {

            try {
                getHomeLetterTask().execute(user_id,className)
            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val thread = Thread(startRunnable)
        thread.start()

        return view
    }


    internal inner class getHomeLetterTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val className = params[1]
            val url = "letter"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("classname", className)

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            Log.d("bitx_log", "homeletter result :$result")
            if(result.equals(""))
                Toast.makeText(context,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            else {
                try {
                    activity!!.main_progressText.text = ""
                    activity!!.main_progressbar.visibility = View.GONE

                    val `object` = JSONObject(result)
                    val count: Int = `object`.getInt("homeLetterCount")
                    val homeLetterList = `object`.getJSONArray("homeLetterList")

                    var noticeList: MutableList<String> = ArrayList()
                    var homeLetter_img_url: MutableList<String> = ArrayList()
                    var homeLetterDateList: MutableList<String> = ArrayList()

                    if (count == 0) {
                        var noticeList: MutableList<String> = ArrayList()

                        noticeList.add("가정통신문이 없습니다")
                        if (isAdded) {
                            adapter = ArrayAdapter(
                                activity,
                                android.R.layout.simple_list_item_1,
                                noticeList
                            )

                            homeletter_lv.adapter = adapter

                            adapter!!.notifyDataSetChanged()
                        }
                    } else {
                        for (i in 0 until count) {
                            val json = homeLetterList.getJSONObject(i)

                            noticeList.add(json.getString("homeLetterDate") + "     " + "가정통신문")
                            homeLetter_img_url!!.add(
                                "https://d1d2thkw8tiq2x.cloudfront.net/" + json.getString(
                                    "homeLetterImagePath"
                                )
                            )
                            homeLetterDateList!!.add(json.getString("homeLetterDate"))

                        }

                        adapter = ArrayAdapter(
                            activity,
                            android.R.layout.simple_list_item_1,
                            noticeList
                        )

                        homeletter_lv.adapter = adapter

                        homeletter_lv.setOnItemClickListener { parent, view, position, id ->
                            val nextIntent = Intent(context, HomeLetterActivity::class.java)
                            nextIntent.putExtra("user_id", user_id)
                            nextIntent.putExtra("letterDay", homeLetterDateList.get(position))
                            nextIntent.putExtra("content", homeLetter_img_url.get(position))
                            startActivity(nextIntent)
                        }

                        val footer: View =
                            layoutInflater.inflate(R.layout.home_list_footer, null, false)
                        homeletter_lv.addFooterView(footer)

                    }
                }catch(e : Exception)
                {

                }

            }//else
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