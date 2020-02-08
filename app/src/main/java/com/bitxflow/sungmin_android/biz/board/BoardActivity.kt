package com.bitxflow.sungmin_android.biz.board

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.send.SendServer
import com.bitxflow.sungmin_android.util.DownloadImage
import com.bitxflow.sungmin_android.util.saveDrawable
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.board_head.*
import kotlinx.android.synthetic.main.board_head.view.*
import kotlinx.android.synthetic.main.reply_send.view.*
import org.json.JSONObject
import java.net.URLDecoder
import java.util.*


class BoardActivity : AppCompatActivity() {

    var user_id : String =""
    var boardSid : String =""
    var contents : String =""
    var userName = ""

    private var userDB: MemberDatabase? = null

    private val MY_PERMISSION_REQUEST_STORAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val intent = intent
        user_id = intent.extras.getString("user_id")
        val title = intent.extras.getString("title")
        contents = intent.extras.getString("contents")
        boardSid = intent.extras.getString("board_sid")

        val header: View =
            layoutInflater.inflate(R.layout.board_head, null, false)

        header.board_head_title_tx.text = title
        header.board_head_contents_tx.text = Html.fromHtml(contents)
        header.board_head_contents_tx.movementMethod = LinkMovementMethod.getInstance()
        board_progressbar.visibility = View.GONE
        board_listview.addHeaderView(header)


        val footer: View =
            layoutInflater.inflate(R.layout.reply_send, null, false)


        footer.reply_send_bt.setOnClickListener{
            val content = footer.reply_content.text
            if(content.isEmpty()) Toast.makeText(applicationContext,"내용을 입력해주세요",Toast.LENGTH_SHORT).show()
            else
            {
                val builder = AlertDialog.Builder(this@BoardActivity)

                builder.setTitle("댓글 전송")
                builder.setMessage(
                    "댓글을 작성하시겠습니까?"
                )

                builder.setPositiveButton("네") { dialog, which ->
                    var replySecretYN = ""
                    if(footer.secret_checkbox.isChecked) replySecretYN="Y"
                    SendReplyTask().execute(replySecretYN,content.toString())
                    board_progressbar.visibility = View.VISIBLE
                }

                builder.setNeutralButton("취소") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog: AlertDialog = builder.create()

                dialog.show()


            }
        }

        val ModifyRunnable = Runnable {

            try {
                userDB = MemberDatabase.getInstance(this)
                var user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                userName = user!!.userName.toString()

                footer.photo_reply_send_name.text = userName + " 어머님"


            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val modifyThread = Thread(ModifyRunnable)
        modifyThread.start()



        footer.reply_content.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.showSoftInput(footer.reply_content, 0)
        }
        board_listview.addFooterView(footer)

        getPhotoListTask().execute(user_id.trim(),boardSid)
    }

    internal inner class getPhotoListTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            return su.getPhotoDetail(params[0],params[1])
        }

        override fun onPostExecute(result: String) {

            Log.d("bitx_log","result : $result")
            val `object` = JSONObject(result)


            ////////////////REPLY/////////////////////////////////
            val count : Int = `object`.getInt("replyCount")
            val replys = `object`.getJSONArray("list")

            var replyList :ArrayList<Reply> = ArrayList()

            for (i in 0 until count) {
                val json = replys.getJSONObject(i)

                val reply = Reply(
                    json.getString("id"),
                    (URLDecoder.decode(json.getString("user"), "UTF-8")),
                    (URLDecoder.decode(json.getString("reply_content"), "UTF-8"))
                )

                replyList.add(reply)

            }

            if(count == 0)
            {
                Log.d("bitx_log","in")
                val reply = Reply(
                    "0","","댓글이 없습니다"
                )
                replyList.add(reply)
            }

            val replyAdapter = ReplyAdapter(applicationContext!!,replyList)
            replyAdapter.notifyDataSetChanged()
            board_listview.adapter = replyAdapter


            /////////////////////////////APPEND ///////////////////////////
            val photoCount : Int = `object`.getInt("appendCount")
            val photos = `object`.getJSONArray("appendList")

            for(i in 0 until photoCount)
            {
                val json = photos.getJSONObject(i)

                val img_url = json.getString("hash")

                val iv  = TouchImageView(applicationContext)
                DownloadImage(iv).execute(img_url)

                iv.setOnLongClickListener {

                    val builder = AlertDialog.Builder(this@BoardActivity)

                    builder.setTitle("사진저장")
                    builder.setMessage(
                        "사진을 저장하시겠습니까?"
                    )

                    builder.setPositiveButton("네") { dialog, which ->
                        saveDrawable(iv,img_url.drop(22),applicationContext)
                        Toast.makeText(applicationContext, "저장 되었습니다.", Toast.LENGTH_SHORT).show()


                    }

                    builder.setNeutralButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog: AlertDialog = builder.create()

                    dialog.show()

                    true
                }

                board_head_ll.addView(iv)

                board_progressbar.visibility = View.GONE
            }

        }
    }

    internal inner class  SendReplyTask : AsyncTask<String, String, String>() {

        private var content: String? = null

        override fun doInBackground(vararg params: String): String {
            content = params[1]
            Log.d("bitx_log","send task in : " + params[0])
            val su = SendServer()
            return su.sendReply(boardSid, user_id, userName, params[0], params[1])
        }

        override fun onPostExecute(result: String) {
//            replyList.add(
//                PhotoReplyMember(
//                    "", userName + " 어머님",
//                    content
//                )
//            )
//            adapter.notifyDataSetChanged()
            getReplyListTask().execute(user_id,boardSid)
        }
    }

    internal inner class getReplyListTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            return su.getPhotoDetail(params[0],params[1])
        }

        override fun onPostExecute(result: String) {

            val `object` = JSONObject(result)

            Log.d("bitx_log","reply get in")

            ////////////////REPLY/////////////////////////////////
            val count : Int = `object`.getInt("replyCount")
            val replys = `object`.getJSONArray("list")

            var replyList :ArrayList<Reply> = ArrayList()

            for (i in 0 until count) {
                val json = replys.getJSONObject(i)

                val reply = Reply(
                    json.getString("id"),
                    (URLDecoder.decode(json.getString("user"), "UTF-8")),
                    (URLDecoder.decode(json.getString("reply_content"), "UTF-8"))
                )

                replyList.add(reply)

            }

            if(count == 0)
            {
                Log.d("bitx_log","in")
                val reply = Reply(
                    "0","","댓글이 없습니다"
                )
                replyList.add(reply)
            }

            val replyAdapter = ReplyAdapter(applicationContext!!,replyList)
            replyAdapter.notifyDataSetChanged()
            board_listview.adapter = replyAdapter
            board_progressbar.visibility = View.GONE
        }
    }

}
