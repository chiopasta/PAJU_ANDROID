package com.bitxflow.sungmin_android.biz.splash

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.login.LoginActivity
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


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

            try {

//                for(i in users!!)
//                {
//                    Log.d("bitx_log","users?" + i.userId)
//                    Log.d("bitx_log","users?" + i.userName)
//                    Log.d("bitx_log","users?" + i.imgSrc)
//                    Log.d("bitx_log","users?" + i.classSid)
//                    Log.d("bitx_log","users?" + i.className)
//                    Log.d("bitx_log","users?" + i.multy_login)
//                }

                if (users!!.isNotEmpty()) {
                    val user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                    val userId = user!!.userId
                    val userpw = user!!.userPassword
                    pbar!!.progress = ++myProgress
                    Thread.sleep(1000)
                    SendTask().execute(userId, userpw)
                } else {
                    val nextIntent = Intent(this, LoginActivity::class.java)
                    startActivityForResult(nextIntent, LOGIN_ACTIVITY)
                }
            }catch(e: Exception)
            {
                val nextIntent = Intent(this, LoginActivity::class.java)
                startActivityForResult(nextIntent, LOGIN_ACTIVITY)
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
            val url = "login"
            val postDataParams = JSONObject()
            postDataParams.put("userid", userId.toUpperCase())
            postDataParams.put("password", userPassword)

            return su.requestPOST(url,postDataParams)
//            return su.getHome(userId)
        }

        override fun onPostExecute(result: String) {
            Log.d("bitx_log","Login result : $result")

            if(result=="") {
                Toast.makeText(baseContext, "서버 통신오류", Toast.LENGTH_SHORT).show()
                val nextIntent = Intent(baseContext, LoginActivity::class.java)
                pbar!!.progress = 100
                startActivityForResult(nextIntent, LOGIN_ACTIVITY)
            }
            else {
                val `object` = JSONObject(result)
                val success = `object`.getBoolean("success")
                if (success) {
                    Toast.makeText(baseContext, "자동로그인 되었습니다", Toast.LENGTH_SHORT).show()
                    pbar!!.progress = 100
                    setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", userId))
                    finish()
                } else {
                    Toast.makeText(baseContext, "자동로그인 실패", Toast.LENGTH_SHORT).show()
                    val nextIntent = Intent(baseContext, LoginActivity::class.java)
                    pbar!!.progress = 100
                    startActivityForResult(nextIntent, LOGIN_ACTIVITY)
                }
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
