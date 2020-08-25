package com.bitxflow.sungmin_android.biz.setting

import android.app.AlertDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.Reply
import com.bitxflow.sungmin_android.biz.board.ReplyAdapter
import com.bitxflow.sungmin_android.send.SendServer
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.account_dialog.*
import kotlinx.android.synthetic.main.account_dialog.view.*
import kotlinx.android.synthetic.main.activity_board.*
import org.json.JSONObject
import java.net.URLDecoder
import java.util.*

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val intent = intent
        val user_id = intent.extras!!.getString("user_id")

        pw_bt.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.account_dialog,null)

            dialogView.custom_dialog_title.text = "새로운 비밀번호"
            dialogView.custom_dialog_content.text = "새로운 비밀번호를 입력해주세요.\n 변경된 비밀번호는 다시 확인하기 어려우니\n신중하게 바꿔주세요.\n \'확인\'을 누르면 바로 변경됩니다."
            dialogView.custom_dialog_et.hint = "새로운 비밀번호를 입력하세요"
            dialogView.custom_dialog_et.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            dialogView.dialog_pw2.visibility = View.VISIBLE
            dialogView.dialog_pw2.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
//
            builder.setView(dialogView)
                .setPositiveButton("확인"){dialogInterface, i ->
                    val pw_et1 = dialogView.custom_dialog_et.text
                    val pw_et2 = dialogView.dialog_pw2.text
                    //TODO 확인 눌러도 안꺼지게 하는법??
                    if(pw_et1.toString() != pw_et2.toString())
                    {
                        Toast.makeText(applicationContext,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        if(pw_et1.length < 8) Toast.makeText(applicationContext,"비밀번호가 너무 짧습니다",Toast.LENGTH_SHORT).show()
                        else
                        {
                            infoUpdateTask().execute(user_id,pw_et1.toString(),"","")
                        }
                    }
                }
                .setNegativeButton("취소"){dialogInterface, i ->
                }
                .show()
        }

        phone_bt.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.account_dialog,null)

            dialogView.custom_dialog_title.text = "새로운 전화번호"
            dialogView.custom_dialog_content.text = "새로운 전화번호를 입력해주세요.\n \'확인\'을 누르면 바로 변경됩니다."
            dialogView.custom_dialog_et.hint = "새로운 전화번호를 입력하세요"
            dialogView.custom_dialog_et.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_PHONE
            dialogView.dialog_pw2.visibility = View.GONE
//
            builder.setView(dialogView)
                .setPositiveButton("확인"){dialogInterface, i ->
                    val new_phone = dialogView.custom_dialog_et.text
                    if(new_phone.toString().equals(""))
                        Toast.makeText(applicationContext,"전화번호를 입력해주세요",Toast.LENGTH_SHORT).show()
                    else {
                        if(new_phone.length < 8) Toast.makeText(applicationContext,"전화번호가 너무 짧습니다",Toast.LENGTH_SHORT).show()
                        else
                            infoUpdateTask().execute(user_id,"", "", new_phone.toString())
                    }
                }
                .setNegativeButton("취소"){dialogInterface, i ->

                }
                .show()
        }

        address_bt.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.account_dialog,null)

            dialogView.custom_dialog_title.text = "새로운 주소"
            dialogView.custom_dialog_content.text = "새로운 주소를 입력해주세요.\n \'확인\'을 누르면 바로 변경됩니다."
            dialogView.custom_dialog_et.hint = "새로운 주소를 입력하세요"
            dialogView.custom_dialog_et.inputType = InputType.TYPE_CLASS_TEXT
            dialogView.dialog_pw2.visibility = View.GONE

//
            builder.setView(dialogView)
                .setPositiveButton("확인"){dialogInterface, i ->
                    val new_address = dialogView.custom_dialog_et.text
                    if(new_address.toString().equals(""))
                        Toast.makeText(applicationContext,"주소를 입력해주세요",Toast.LENGTH_SHORT).show()
                    else
                        infoUpdateTask().execute(user_id,"",new_address.toString(),"")
                }
                .setNegativeButton("취소"){dialogInterface, i ->

                }
                .show()
        }

        logout_bt.setOnClickListener{
            val builder = AlertDialog.Builder(this)

            builder.setTitle("로그아웃")
            builder.setMessage(
                "로그아웃 하시겠습니까?"
            )

            builder.setPositiveButton("확인") { dialog, which ->

                val userDB = MemberDatabase.getInstance(this)

                val DeleteRunnable = Runnable {
                    try {
                        var user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                        val cal = Calendar.getInstance()
                        val year =
                            Integer.toString(cal[Calendar.YEAR])
                        var classSid = user!!.classSid
                        val userId = user!!.userId
                        var upper = userId?.substring(0, 3)
                        upper = upper?.toUpperCase()
                        val topic = "$upper-$year-$classSid"

                        Log.d("bitx_log","logout topic $topic")

                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)

                        userDB?.userDao()?.deleteUser(user!!)
                    } catch (e: Exception) {
                        Log.d("bitx_log", "error :" + e.toString())
                    }
                }

                val deleteThread = Thread(DeleteRunnable)
                deleteThread.start()

                Toast.makeText(applicationContext,"로그아웃 되었습니다 어플을 종료합니다",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
                Process.killProcess(Process.myPid())

            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

    }


    internal inner class infoUpdateTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()

            val userId = params[0]
            val new_password = params[1]
            val new_address = params[2]
            val new_phone = params[3]

            var updateInfo = JSONObject()
            if(new_password!="")
                updateInfo.put("password",new_password)
            if(new_address!="")
                updateInfo.put("address",new_address)
            if(new_phone!="")
                updateInfo.put("emergencyNumber",new_phone)

            val url = "setting"
            Log.d("bitx_log","updateInfo $updateInfo")
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("updateInfo", updateInfo.toString())
            return su.requestPOST(url,postDataParams)
        }

        override fun onPostExecute(result: String) {

            if(result.equals(""))
                Toast.makeText(this@AccountActivity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            else {
                try {
                    val `object` = JSONObject(result)
                    val success = `object`.getBoolean("success")
                    if (success)
                        Toast.makeText(applicationContext, "수정되었습니다", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(applicationContext, "다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }catch(e : Exception)
                {
                    Toast.makeText(this@AccountActivity,"다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
