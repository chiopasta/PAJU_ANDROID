package com.bitxflow.sungmin_android.biz.splash

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.login.LoginActivity
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {


    private val LOGIN_ACTIVITY : Int = 0
    private var userDB : MemberDatabase? = null

    var pbar : ProgressBar? = null
    var myProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var users : List<User>? = null

        pbar = splash_progressBar

        pbar!!.progressDrawable
            .setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
        pbar!!.progress = myProgress

        val getUserRunable = Runnable {

            userDB = MemberDatabase.getInstance(baseContext)

            users = userDB?.userDao()?.getUsers()

            if(users!!.isNotEmpty())
            {
                val user : User? = userDB?.userDao()?.getMultyLoginUser(true)

                val userId = user!!.userId
                val userpw = user!!.userPassword

                pbar!!.progress = ++myProgress
                Thread.sleep(1000)
                SendTask().execute(userId,userpw)

            }
            else
            {
                val nextIntent = Intent(this, LoginActivity::class.java)
                startActivityForResult(nextIntent,LOGIN_ACTIVITY)
            }
        }

        val thread = Thread(getUserRunable)
        thread.start()



    }



    internal inner class SendTask : AsyncTask<String, String, String>() {
        var userId : String =""
        var userPassword : String = ""

        override fun doInBackground(vararg params: String): String {
            val su = SendServer()
            userId = params[0]
            userPassword = params[1]
            return su.Login(params[0], params[1],"")
        }

        override fun onPostExecute(result: String) {
//            pbar.setVisibility(View.INVISIBLE)
//            Login_button.setClickable(true)

            if (result == "fail") {
                Toast.makeText(baseContext, "자동로그인 실패", Toast.LENGTH_SHORT).show()
                val nextIntent = Intent(baseContext, LoginActivity::class.java)
                pbar!!.progress = 100
                startActivityForResult(nextIntent,LOGIN_ACTIVITY)

            } else {
                Toast.makeText(baseContext, "자동로그인 되었습니다", Toast.LENGTH_SHORT).show()
                pbar!!.progress = 100
                setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", userId))
                finish()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                LOGIN_ACTIVITY -> {
                    val backPress = data?.getStringExtra("BackPress")
                    setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", backPress))
                    pbar!!.progress = 100
                    finish()
                }
            }
        }
    }
}
