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
import kotlinx.android.synthetic.main.reply_send.view.*
import org.json.JSONObject
import java.util.*

class BoardFragment : Fragment() {

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

        val view = inflater.inflate(R.layout.fragment_board, null)

        val ModifyRunnable = Runnable {

            try {
                userDB = MemberDatabase.getInstance(context!!)
                var user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                val class_name = user!!.className.toString()

                getBoardList().execute(user_id, class_name)

            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val modifyThread = Thread(ModifyRunnable)
        modifyThread.start()

        return view
    }


    internal inner class getBoardList : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val classname = params[1]
            val url = "post"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("classname", classname)

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            Log.d("bitx_log", "homeletter result :$result")

            if(result.equals(""))
                Toast.makeText(activity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            else {
                activity!!.main_progressText.text = ""
                activity!!.main_progressbar.visibility = View.GONE
                try {
                    val `object` = JSONObject(result)
                    val count: Int = `object`.getInt("postCount")
                    val homeLetterList = `object`.getJSONArray("postList")

                    var titleList: MutableList<String> = ArrayList()
                    var sidList: MutableList<String> = ArrayList()
                    var contentsList: MutableList<String> = ArrayList()
                    var attachmentList: MutableList<String> = ArrayList()

                    for (i in 0 until count) {
                        val json = homeLetterList.getJSONObject(i)
                        val t_imgarr = json.getJSONArray("attachment")
                        titleList!!.add(json.getString("title"))
                        sidList!!.add(json.getString("postID"))
                        contentsList!!.add(json.getString("contents"))
                        attachmentList!!.add(t_imgarr.toString())
                    }

                    adapter = ArrayAdapter(
                        activity,
                        android.R.layout.simple_list_item_1,
                        titleList
                    )

                    fragment_board_listview.adapter = adapter

                    fragment_board_listview.setOnItemClickListener { parent, view, position, id ->
                        val nextIntent = Intent(context, BoardActivity::class.java)
                        nextIntent.putExtra("type", "post")
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