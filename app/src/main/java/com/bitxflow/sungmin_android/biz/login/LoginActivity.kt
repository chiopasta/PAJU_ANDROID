package com.bitxflow.sungmin_android.biz.login

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var userDB : MemberDatabase? = null
    private var isTwo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_pbar.visibility = View.GONE

        login_button_bt.setOnClickListener{
            val id = input_id_et.text.toString()
            val pw = password_et.text.toString()
            login_pbar.visibility = View.VISIBLE
            SendTask().execute(id,pw)
            login_button_bt.isClickable = false
        }
    }

    internal inner class SendTask : AsyncTask<String, String, String>() {
        var userId : String =""
        var userPassword : String = ""

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            userId = params[0].trim()
            userPassword = params[1].trim()

            val url = "login"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("password", userPassword)

            return su.requestPOST(url,postDataParams)

        }

        override fun onPostExecute(result: String) {
            login_button_bt.isClickable = true
            login_pbar.visibility = View.GONE
            if(result =="")
            {
                Toast.makeText(baseContext, "서버 통신오류, 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val json = JSONObject(result)
                val success = json.getBoolean("success")

                if(success)
                {
                    val addRunnable = Runnable {

                        userDB = MemberDatabase.getInstance(baseContext)

                        ////LOGIN 된 유져가 있다면 multy = false
                        val users : List<User>? =  userDB?.userDao()?.getUsers()
                        if(users!!.isNotEmpty()) {
                            var login_user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                            if (login_user!!.multy_login!!) {
                                    login_user.multy_login = false
                                userDB?.userDao()?.update(login_user)
                                Log.d("bitx_log", "login 유져 존재함")
                            }
                        }

                        //////////////////new USER insert //////////////
                        val user = User()
                        user.userId = userId
                        user.userPassword = userPassword
                        user.userName = ""
                        user.classSid = ""
                        user.className = ""
                        user.imgSrc=""
                        user.multy_login = true

                        userDB?.userDao()?.insert(user)
                        Log.d("bitx_log","userDB insert" + userId)
                        Log.d("bitx_log","size?" + userDB?.userDao()?.getUsers()!!.size)

                    }

                    val addThread = Thread(addRunnable)
                    addThread.start()

                    setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", userId))
                    finish()
                }
                else {
                    Toast.makeText(baseContext, json.getString("message"), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!isTwo) {
            Toast.makeText(this, "\'뒤로\'버튼을 한번더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
                .show()
            val timer = myTimer(2000, 1) // 2초동안 수행
            timer.start() // 타이머를 이용해줍시다
        } else
        {
            setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", "BackPress"))
            finish()
        }
    }

    inner class myTimer constructor(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        init {
            isTwo = true
        }

        override fun onFinish() {
            isTwo = false
        }

        override fun onTick(millisUntilFinished: Long) {
            //Log.i("Test", "isTwo" + isTwo);
        }

    }

    override fun onDestroy() {
        MemberDatabase.destroyInstace()
        super.onDestroy()
    }
}
