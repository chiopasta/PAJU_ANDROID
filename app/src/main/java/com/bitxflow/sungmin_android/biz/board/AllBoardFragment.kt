package com.bitxflow.sungmin_android.biz.board

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
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.homeletter.HomeLetterActivity
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_board.*
import kotlinx.android.synthetic.main.fragment_homeletter.*
import org.json.JSONObject
import java.util.*

class AllBoardFragment : Fragment() {

    private var user_id: String = ""

    private var adapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_board, null)

//        activity!!.main_progressText.text =""
//        activity!!.main_progressbar.visibility = View.GONE
//
//        getAllBoardList().execute()

        val startRunnable = Runnable {

            try {
                getAllBoardList().execute(user_id)
            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val thread = Thread(startRunnable)
        thread.start()

        return view
    }


    internal inner class getAllBoardList : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val url = "board"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            Log.d("bitx_log", "homeletter result :$result")

            if(result.equals(""))
                Toast.makeText(activity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()

            else {
                try {
                activity!!.main_progressText.text = ""
                activity!!.main_progressbar.visibility = View.GONE

                    val `object` = JSONObject(result)
                    val count: Int = `object`.getInt("boardCount")
                    val homeLetterList = `object`.getJSONArray("boardList")

                    var titleList: MutableList<String> = ArrayList()
                    var sidList: MutableList<String> = ArrayList()
                    var contentsList: MutableList<String> = ArrayList()
                    var attachmentList: MutableList<String> = ArrayList()

                    for (i in 0 until count) {
                        val json = homeLetterList.getJSONObject(i)
                        val t_imgarr = json.getJSONArray("attachment")
                        titleList!!.add(json.getString("title"))
                        sidList!!.add(json.getString("boardID"))
                        contentsList!!.add(json.getString("contents"))
                        attachmentList!!.add(t_imgarr.toString())
                    }

                    adapter = ArrayAdapter(
                        activity!!,
                        android.R.layout.simple_list_item_1,
                        titleList
                    )

                    fragment_board_listview.adapter = adapter

                    fragment_board_listview.setOnItemClickListener { parent, view, position, id ->
                        val nextIntent = Intent(context, BoardActivity::class.java)
                        nextIntent.putExtra("type", "board")
                        nextIntent.putExtra("user_id", user_id)
                        nextIntent.putExtra("board_sid", sidList[position])
                        nextIntent.putExtra("contents", contentsList[position])
                        nextIntent.putExtra("attachmentList", attachmentList[position])
                        nextIntent.putExtra("title", titleList[position])
                        startActivity(nextIntent)
                    }

                    val footer: View =
                        layoutInflater.inflate(R.layout.home_list_footer, null, false)
                    fragment_board_listview.addFooterView(footer)
                } catch (e: Exception) {
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