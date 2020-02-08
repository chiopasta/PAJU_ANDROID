package com.bitxflow.sungmin_android.biz.splash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Build
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
        checkPermission()
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

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) { // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) { // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show()
                }
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    100
                )
                // MY_PERMISSION_REQUEST_STORAGE is an
// app-defined int constant
            } else { // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
            }
        }
    }
}
