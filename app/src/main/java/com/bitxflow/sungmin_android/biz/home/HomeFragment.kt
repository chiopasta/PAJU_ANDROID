package com.bitxflow.sungmin_android.biz.home

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.MainActivity
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.BoardActivity
import com.bitxflow.sungmin_android.send.SendServer
import com.bitxflow.sungmin_android.util.DownloadImage
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.home_list_header.*
import kotlinx.android.synthetic.main.home_list_header.view.*
import org.json.JSONObject
import java.net.URLDecoder
import java.util.*


class HomeFragment : Fragment() {

    private var user_id: String = ""
    private var imgSrc: String = ""

    private var userDB: MemberDatabase? = null
    private var isAbsent: Boolean = false

//    private val noticeList: ArrayList<String>? = null
//    private val noticeDateList: ArrayList<String>? = null
//    private val noticeContentList: ArrayList<String>? = null

    private var adapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user_id = arguments!!.getString("user_id")
        try {
            imgSrc = arguments!!.getString("imgSrc")
        }catch(e: Exception)
        {
            imgSrc = ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        // 처리

        val startRunnable = Runnable {

            try {
                mobilePushTask().execute()
            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val thread = Thread(startRunnable)
        thread.start()

        return view
    }


    internal inner class mobilePushTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            val url = "home"
            val postDataParams = JSONObject()
            postDataParams.put("userid", user_id.toUpperCase())

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {

//            Log.d("bitx_log","result $result")
            if (isAdded) {
                activity!!.main_progressText.text = ""
                activity!!.main_progressbar.visibility = View.GONE

                val header: View =
                    layoutInflater.inflate(R.layout.home_list_header, null, false)

                val footer: View =
                    layoutInflater.inflate(R.layout.home_list_footer, null, false)


            /////////////// ATTEND /////////////////////////////////
            header.late_absent_switch.setOnCheckedChangeListener { buttonView, isChecked ->
                isAbsent = isChecked
            }

            header.late_absent_send_bt.setOnClickListener {
                val reason: String = absent_reason_et.text.toString()

                if (reason.equals(""))
                    Toast.makeText(context, "사유를 적어주세요", Toast.LENGTH_SHORT).show()
                else {
                    val cal: Calendar = Calendar.getInstance()
                    val year = cal[Calendar.YEAR]
                    val month = cal[Calendar.MONTH] + 1
                    val day = cal[Calendar.DAY_OF_MONTH]

                    val builder = AlertDialog.Builder(context)

                    val absent = when{
                        isAbsent -> "결석" //결석
                        else -> "지각" //지각
                    }

                    builder.setTitle("" + absent + "알리기")
                    builder.setMessage(
                        "" + year + "년" + month + "월" + day + "일" + "\n"
                                + "유치원에 " + absent + " 메시지를 보냅니다."
                    )

                    builder.setPositiveButton("YES") { _, _ ->
                        SendAttendTask().execute(absent,reason)
                    }

                    builder.setNeutralButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }

            }

                mobilePush_listView.addHeaderView(header)
                mobilePush_listView.addFooterView(footer)

            }
            if(result.equals(""))
                Toast.makeText(activity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()

            else {
                val `object` = JSONObject(result)
                val notices = `object`.getJSONArray("mobileNotices")
                val count = `object`.getInt("mobileNoticeCount")

                if (count == 0) {
                    var noticeList: MutableList<String> = ArrayList()

                    noticeList.add("모바일 알림이 없습니다")
                    if (isAdded) {
                        adapter = ArrayAdapter(
                            activity,
                            android.R.layout.simple_list_item_1,
                            noticeList
                        )

                        mobilePush_listView.adapter = adapter

                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    var noticeList: MutableList<String> = ArrayList()
                    var noticeDateList: MutableList<String> = ArrayList()
                    var noticeContentList: MutableList<String> = ArrayList()

                    val SHOW_COUNT = 10

                    for (i in 0 until count) {
                        val json = notices.getJSONObject(i)
                        val content = json.getString("mobileNoticeContents")
                        var short_content = ""
                        if (content.length > 10) {
                            short_content = content.dropLast(content.length - 10)
                            short_content = short_content.plus(" ...")
                        }

                        if (short_content == "")
                            noticeList.add(json.getString("mobileNoticeDate") + "     " + content)
                        else
                            noticeList.add(json.getString("mobileNoticeDate") + "     " + short_content)
                        noticeDateList!!.add(json.getString("mobileNoticeDate"))
                        noticeContentList!!.add(content)

                    }

                    if (count > SHOW_COUNT) {
                        noticeList = noticeList.dropLast(count - SHOW_COUNT).toMutableList()
                        noticeDateList =
                            noticeDateList!!.dropLast(count - SHOW_COUNT).toMutableList()
                        noticeContentList =
                            noticeContentList.dropLast(count - SHOW_COUNT).toMutableList()
                    }
                    if (isAdded) {
                        adapter = ArrayAdapter(
                            activity,
                            android.R.layout.simple_list_item_1,
                            noticeList
                        )

                        mobilePush_listView.adapter = adapter

                        mobilePush_listView.setOnItemClickListener { parent, view, position, id ->
                            val builder = AlertDialog.Builder(context)

                            builder.setTitle("모바일 알림")
                            builder.setMessage(
                                "" + noticeDateList[position - 1] + "\n"
                                        + "" + noticeContentList[position - 1]
                            )

                            builder.setPositiveButton("확인") { dialog, which ->
                                dialog.dismiss()
                            }

                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                }

                //////////////////////// INFO /////////////////////////////
                val userName = `object`.getString("username")
                val className = `object`.getString("classname")
                val userAge = `object`.getString("age")
                val commute = `object`.getString("wayToCome")
                val teacher = `object`.getString("teachers")
                val fcm_topic = `object`.getString("fcmTopic")

                /////SETTING TEXTS
                if (isAdded) {
                    info_user_id_tx.text = user_id
                    info_username_tx.text = userName
                    teacher_tx.text = teacher + "선생님"
                    info_user_age_tx.text = userAge + " 세"
                    info_user_commute_tx.text = commute
                    info_class_name_tx.text = className
                    val GALLERY = 100
                    info_iv.setOnClickListener {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = MediaStore.Images.Media.CONTENT_TYPE
                        activity!!.startActivityForResult(intent, GALLERY)
                    }
                }


                val ModifyRunnable = Runnable {
                    try {
                        userDB = MemberDatabase.getInstance(context!!)
                        var user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                        user!!.userName = userName
                        user!!.className = className
                        user!!.classSid =
                            fcm_topic.substring(fcm_topic.length - 9, fcm_topic.length)
                        userDB?.userDao()?.update(user)
                    } catch (e: Exception) {
                        Log.d("bitx_log", "여기다")
                        Log.d("bitx_log", "error :" + e.toString())
                    }
                }

                val modifyThread = Thread(ModifyRunnable)
                modifyThread.start()

                if (imgSrc.isNotEmpty()) {
                    val bmp = BitmapFactory.decodeFile(imgSrc)
                    info_iv.setImageBitmap(bmp)
                }

                FirebaseMessaging.getInstance().subscribeToTopic(fcm_topic)
                    .addOnCompleteListener {
                    }

                //////////// MEAL ///////
                val displaydate = `object`.getString("displaydate")
                val menu = `object`.getString("menu")

                if (`object`.has("imagepath")) {
                    val url = `object`.getString("imagepath")
                    meal_date_tx.text = ("오늘의 식단(" + displaydate + ")")
                    meal_menu_tx.text = menu
                    DownloadImage(meal_iv).execute(url)
                } else {
                    meal_date_tx.text = ("오늘의 식단(" + displaydate + ")")
                    meal_menu_tx.text = menu
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


    internal inner class SendAttendTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            val attendanceStatus = params[0]
            val attendanceReason = params[1]

//            val url = "https://m.bitxdev.com/home/attendance/" + user_id.toUpperCase()
            val url = "home/attendance/" + user_id.toUpperCase()
            val postDataParams = JSONObject()
            postDataParams.put("attendanceStatus", attendanceStatus)
            postDataParams.put("attendanceReason", attendanceReason)

            return su.requestPOST(url,postDataParams)

        }

        override fun onPostExecute(result: String) {
            if(result.equals(""))
                Toast.makeText(context,"다시 시도해 주세요",Toast.LENGTH_SHORT).show()
            else {
                val json = JSONObject(result)
                val success = json.getBoolean("success")
                if(success)
                    Toast.makeText(context, "처리 되었습니다 ", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context,json.getString("message"),Toast.LENGTH_SHORT).show()
            }
        }
    }

}