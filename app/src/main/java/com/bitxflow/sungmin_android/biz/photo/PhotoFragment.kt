package com.bitxflow.sungmin_android.biz.photo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.BoardActivity
import com.bitxflow.sungmin_android.send.SendServer
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*
import org.json.JSONObject
import java.util.*


class PhotoFragment : Fragment() {

    private var user_id: String = ""
    private var userDB: MemberDatabase? = null
    private var page : Int = 0

    var photoList :ArrayList<Photo> = ArrayList()
    var titleList: MutableList<String> = ArrayList()
    var sidList: MutableList<String> = ArrayList()
    var contentsList: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo, null)

        var class_name = ""

        val ModifyRunnable = Runnable {

            try {
                userDB = MemberDatabase.getInstance(context!!)
                var user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                class_name = user!!.className.toString()

                getPhotoListTask().execute(user_id, class_name,"0")

            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val modifyThread = Thread(ModifyRunnable)
        modifyThread.start()

        view.swipyrefreshlayout.setOnRefreshListener{
            swipyrefreshlayout.isRefreshing = false
//            Log.d("bitx_log","down" + swipyrefreshlayout.canChildScrollDown()) // 위로했을때 true
//            Log.d("bitx_log","up" + swipyrefreshlayout.canChildScrollUp()) //아래로 했을때 true
            if(swipyrefreshlayout.canChildScrollUp())
            {
                page++
                val str_page = page.toString()
                
                activity!!.main_progressText.text = "사진첩 로딩중"
                activity!!.main_progressbar.visibility = View.VISIBLE
                getPhotoListTask().execute(user_id, class_name,str_page)
            }

        }



        return view
    }

    internal inner class getPhotoListTask : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            return su.getPhotoList(params[0], params[1],params[2])
        }

        override fun onPostExecute(result: String) {
            Log.d("bitx_log", "photolist result :$result")

            activity!!.main_progressText.text = ""
            activity!!.main_progressbar.visibility = View.GONE

            val `object` = JSONObject(result)
            val count: Int = `object`.getInt("count")
            val photos = `object`.getJSONArray("list")

            for(i in 0 until count)
            {
                val json = photos.getJSONObject(i)
                val photo = Photo(
                    json.getString("board_sid"),
                    json.getString("title"),
                    json.getString("contents"),
                    json.getString("pUrl")
                )
                titleList!!.add(json.getString("title"))
                sidList!!.add(json.getString("board_sid"))
                contentsList!!.add(json.getString("contents"))
                photoList.add(photo)
            }

            val photoAdapter = PhotoListAdapter(context!!,photoList)
            photoAdapter.notifyDataSetChanged()
            photo_lv.adapter = photoAdapter

            val footer: View =
            layoutInflater.inflate(R.layout.photo_list_footer, null, false)

            if(page==0)
                photo_lv.addFooterView(footer)

            photo_lv.setOnItemClickListener { parent, view, position, id ->
                    val nextIntent = Intent(context, BoardActivity::class.java)
                    nextIntent.putExtra("user_id", user_id)
                    nextIntent.putExtra("board_sid", sidList[position])
                    nextIntent.putExtra("contents", contentsList[position])
                    nextIntent.putExtra("title", titleList[position])
                    startActivity(nextIntent)
                }

            if(page>0)
                photo_lv.setSelection(page*10);

//

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