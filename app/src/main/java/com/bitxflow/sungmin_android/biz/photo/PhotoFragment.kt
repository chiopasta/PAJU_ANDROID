package com.bitxflow.sungmin_android.biz.photo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.BoardActivity
import com.bitxflow.sungmin_android.send.SendServer
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class PhotoFragment : Fragment() {

    private var user_id: String = ""
    private var userDB: MemberDatabase? = null
    private var page : Int = 0

    var photoList :ArrayList<Photo> = ArrayList()
    var titleList: MutableList<String> = ArrayList()
    var sidList: MutableList<String> = ArrayList()
    var contentsList: MutableList<String> = ArrayList()
    var attachmentList: MutableList<JSONArray> = ArrayList()

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

                getPhotoListTask().execute(user_id, class_name)

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
                
                activity!!.main_progressText.text = "반별갤러리 로딩중"
                activity!!.main_progressbar.visibility = View.VISIBLE
                getPhotoListTask().execute(user_id, class_name)
            }

        }



        return view
    }

    internal inner class getPhotoListTask : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val classname = params[1]
            val url = "photo"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("classname", classname)

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            Log.d("bitx_log", "photolist result :$result")

            if(result == "")
                Toast.makeText(activity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()

            else {
                try {
                    activity!!.main_progressText.text = ""
                    activity!!.main_progressbar.visibility = View.GONE


                    val `object` = JSONObject(result)
                    val count: Int = `object`.getInt("photoCount")
                    val photos = `object`.getJSONArray("photoList")

                    for (i in 0 until count) {
                        val json = photos.getJSONObject(i)
                        val t_imgarr = json.getJSONArray("attachment")
                        val t_json = t_imgarr.getJSONObject(0)
                        val pUrl =
                            "https://d1d2thkw8tiq2x.cloudfront.net/" + t_json.getString("path")
                        val photo = Photo(
                            json.getString("photoID"),
                            json.getString("title"),
                            json.getString("contents"),
                            pUrl
                        )
                        titleList!!.add(json.getString("title"))
                        sidList!!.add(json.getString("photoID"))
                        contentsList!!.add(json.getString("contents"))
                        attachmentList!!.add(t_imgarr)
                        photoList.add(photo)
                    }

                    val photoAdapter = PhotoListAdapter(context!!, photoList)
                    photoAdapter.notifyDataSetChanged()
                    photo_lv.adapter = photoAdapter

                    val footer: View =
                        layoutInflater.inflate(R.layout.photo_list_footer, null, false)

                    if (page == 0)
                        photo_lv.addFooterView(footer)

                    photo_lv.setOnItemClickListener { parent, view, position, id ->
                        val nextIntent = Intent(context, BoardActivity::class.java)
                        nextIntent.putExtra("type", "photo")
                        nextIntent.putExtra("user_id", user_id)
                        nextIntent.putExtra("board_sid", sidList[position])
                        nextIntent.putExtra("contents", contentsList[position])
                        nextIntent.putExtra("attachmentList", attachmentList[position].toString())
                        nextIntent.putExtra("title", titleList[position])
                        startActivity(nextIntent)
                    }

                    if (page > 0)
                        photo_lv.setSelection(page * 10);
                }catch (e: Exception)
                {
                }
            }
//
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