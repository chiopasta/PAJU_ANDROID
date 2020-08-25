package com.bitxflow.sungmin_android.biz.board

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.send.SendServer
import com.bitxflow.sungmin_android.util.DownloadImage
import com.bitxflow.sungmin_android.util.saveDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper
import com.ortiz.touchview.TouchImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.board_head.*
import kotlinx.android.synthetic.main.board_head.view.*
import kotlinx.android.synthetic.main.reply_send.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class BoardActivity : AppCompatActivity() {

    var user_id : String =""
    var boardSid : String =""
    var contents : String =""
    var userName = ""
    var type = ""
    var json_arrary : JSONArray? = null
    private var userDB: MemberDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val intent = intent
        type = intent.extras!!.getString("type").toString()
        boardSid = intent.extras!!.getString("board_sid").toString()
        user_id = intent.extras!!.getString("user_id").toString()
        val title = intent.extras!!.getString("title").toString()
        contents = intent.extras!!.getString("contents").toString()
        val attachmentList = intent.extras!!.getString("attachmentList").toString()

        json_arrary = JSONArray(attachmentList)
        Log.d("bitx_log","json_arrary : $json_arrary")
        val header: View =
            layoutInflater.inflate(R.layout.board_head, null, false)

        header.board_head_title_tx.text = title
        header.board_head_contents_tx.text = Html.fromHtml(contents)
        header.board_head_contents_tx.movementMethod = LinkMovementMethod.getInstance()
        board_progressbar.visibility = View.GONE
        board_listview.addHeaderView(header)

        val photoCount = json_arrary!!.length()

        var names: MutableList<String> = ArrayList()
        var count = 0

        for(i in 0 until photoCount)
        {
            board_progressbar.visibility = View.VISIBLE
            val json = json_arrary!!.getJSONObject(i)

            var img_url = ""
            try {
                img_url = "https://d1d2thkw8tiq2x.cloudfront.net/" + json.getString("path")
            }catch(e: Exception)
            {
                continue;
            }
            val EXE = img_url.substring(img_url.length - 3, img_url.length).toUpperCase()
            if(EXE=="MP4")
            {
                val video: Uri =
                    Uri.parse(img_url)
                val videoView = VideoView(applicationContext)
                board_head_ll.addView(videoView)
//                Log.d("bitx_log","videoView")
             val mediaController = MediaController(applicationContext)
                mediaController.setAnchorView(videoView)
                videoView.setMediaController(mediaController)
                videoView.setVideoURI(video)

                videoView.setOnPreparedListener(OnPreparedListener { mp ->
                    mp.isLooping = true
                    videoView.start()
                })

                videoView.start()
            }
            else {
                val iv = TouchImageView(applicationContext)
//                DownloadImage(iv).execute(img_url)
//                UrlImageViewHelper.setUrlDrawable(iv, img_url,R.drawable.loading)
//                    Glide.with(this).load(img_url).into(iv)
                Glide.with(this)
                    .load(img_url)
                    .placeholder(R.drawable.loading)
                    .into(iv)
                board_head_ll.addView(iv)
                board_progressbar.visibility = View.GONE


                iv.id = count++
                val name = json.getString("name")
                names.add(name)

//                val picasso = Picasso.get()
//                picasso.load(img_url).into(iv)
                iv.adjustViewBounds = true
                iv.setOnLongClickListener {

                    val builder = AlertDialog.Builder(this@BoardActivity)

                    builder.setTitle("사진저장")
                    builder.setMessage(
                        "사진을 저장하시겠습니까?"
                    )

                    builder.setPositiveButton("네") { dialog, which ->
                        saveDrawable(iv, name, applicationContext)
                        Toast.makeText(applicationContext, "저장 되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                    builder.setNeutralButton("전체저장하기") { dialog, _ ->
                        for(i in 0 until count)
                        {
                            val ii = findViewById<ImageView>(i)
                            saveDrawable(ii, names[i], applicationContext)
                            Toast.makeText(applicationContext, "저장 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        dialog.dismiss()
                    }

                    val dialog: AlertDialog = builder.create()

                    dialog.show()

                    true
                }
//                board_head_ll.addView(iv)

            }
        }

        if(type=="photo") {
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
//                        if(footer.secret_checkbox.isChecked) replySecretYN="Y"
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
            getReplyListTask().execute(type, boardSid)
        }else
        {
            var replyList :ArrayList<Reply> = ArrayList()

            val reply = Reply("","","")
            replyList.add(reply)

            val replyAdapter = ReplyAdapter(applicationContext!!,replyList)
            replyAdapter.notifyDataSetChanged()
            board_listview.adapter = replyAdapter
            board_progressbar.visibility = View.GONE
        }

    }

    internal inner class getReplyListTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val type = params[0]
            val id = params[1]
            val url = type +"/comment/" + id

            return su.requestGET(url)
        }

        override fun onPostExecute(result: String) {

//            Log.d("bitx_log","result : $result")
            if(result.equals(""))
                Toast.makeText(this@BoardActivity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            else {
                try {
                    val `object` = JSONObject(result)

                    ////////////////REPLY/////////////////////////////////
                    val count: Int = `object`.getInt("replyCount")
                    val replys = `object`.getJSONArray("replyList")

                    var replyList: ArrayList<Reply> = ArrayList()

                    for (i in 0 until count) {
                        val json = replys.getJSONObject(i)

                        val reply = Reply(
                            json.getString("commentID"),
                            (URLDecoder.decode(json.getString("writerName"), "UTF-8")),
                            (URLDecoder.decode(json.getString("contents"), "UTF-8"))
                        )

                        replyList.add(reply)

                    }

                    if (count == 0) {
//                        Log.d("bitx_log", "in")
                        val reply = Reply(
                            "0", "", "댓글이 없습니다"
                        )
                        replyList.add(reply)
                    }

                    val replyAdapter = ReplyAdapter(applicationContext!!, replyList)
                    replyAdapter.notifyDataSetChanged()
                    board_listview.adapter = replyAdapter

                    board_progressbar.visibility = View.GONE
                }catch(e: Exception)
                {
                    Toast.makeText(this@BoardActivity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    internal inner class  SendReplyTask : AsyncTask<String, String, String>() {

        private var content: String? = null

        override fun doInBackground(vararg params: String): String {
//            content = params[1]
//            Log.d("bitx_log","send task in : " + params[0])
            val su = SendServer()
            val url = "photo/comment/" + boardSid
            val postDataParams = JSONObject()
            postDataParams.put("userID", user_id)
            postDataParams.put("writerName", userName)
            postDataParams.put("contents", params[1])

            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {
//            replyList.add(
//                PhotoReplyMember(
//                    "", userName + " 어머님",
//                    content
//                )
//            )
//            adapter.notifyDataSetChanged()
//            Log.d("bitx_log","send reply result + $result")
            getReplyListTask().execute(type,boardSid)
        }
    }

}
