package com.bitxflow.sungmin_android.biz.setting

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.DB.User
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.biz.board.BoardActivity
import com.bitxflow.sungmin_android.biz.login.LoginActivity
import com.bitxflow.sungmin_android.biz.photo.Photo
import com.bitxflow.sungmin_android.biz.splash.SplashActivity
import com.bitxflow.sungmin_android.send.SendServer
import kotlinx.android.synthetic.main.activity_multy_login.*
import kotlinx.android.synthetic.main.multy_login_item.*
import org.json.JSONObject
import java.util.ArrayList

class MultyLoginActivity : AppCompatActivity() {

    private var userDB: MemberDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multy_login)

        var userList : ArrayList<User> = ArrayList()

        val ModifyRunnable = Runnable {

            try {
                userDB = MemberDatabase.getInstance(this)
                var users = userDB?.userDao()?.getUsers()

                userList = users as ArrayList<User>

                val adduser = User()
                adduser.userName = "계정추가"
                adduser.imgSrc = ""
                userList.add(adduser)

                val userAdapter = MultyLoginListAdapter(this,userList)
                userAdapter.notifyDataSetChanged()
                multy_login_lv.adapter = userAdapter

            } catch (e: Exception) {
                Log.d("bitx_log", "error :" + e.toString())
            }
        }

        val modifyThread = Thread(ModifyRunnable)
        modifyThread.start()


        multy_login_lv.setOnItemClickListener { parent, view, position, id ->
            val count = multy_login_lv.adapter.count
            if(position+1 == count)
            {
                val SPLASH = 10
                val nextIntent = Intent(this, LoginActivity::class.java)
                startActivityForResult(nextIntent,SPLASH)
            }
            else
            {
                val user = userList.get(position)
                Log.d("bitx_log","다른 아이디로 변경" + userList.get(position).userName)
                //AUTO login 을 넣으면 됨 . splash 로 부터
//                SendTask().execute(user.userId,user.userPassword)

                Toast.makeText(baseContext, "계정이 변경 되었습니다", Toast.LENGTH_SHORT).show()
//                setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", userId))
                val intent = Intent(baseContext, SplashActivity::class.java)
//                intent.putExtra("LOGIN",true)
//                intent.putExtra("userid",userId)

                val addRunnable = Runnable {

                    userDB = MemberDatabase.getInstance(baseContext)
                    ////LOGIN 된 유져가 있다면 multy = false
                    val users : List<User>? =  userDB?.userDao()?.getUsers()
                    if(users!!.size>0) {
                        var login_user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                        if (login_user!!.multy_login!!) {
                            login_user.multy_login = false
                            userDB?.userDao()?.update(login_user)
                        }
                    }
                    //////////////////user Change //////////////
                    user!!.multy_login = true
                    userDB?.userDao()?.update(user)
                }

                val addThread = Thread(addRunnable)
                addThread.start()

                startActivity(intent)
//                startActivityForResult(nextIntent,LOGIN_ACTIVITY)
                finish()

            }
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
//            pbar.setVisibility(View.INVISIBLE)
//            Login_button.setClickable(true)

            if (result == "fail") {
                Toast.makeText(baseContext, "로그인 실패 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(baseContext, "계정이 변경 되었습니다", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK, Intent().putExtra("BackPress", userId))
                val intent = Intent(baseContext, SplashActivity::class.java)
                intent.putExtra("LOGIN",true)
                intent.putExtra("userid",userId)

                val addRunnable = Runnable {

                    userDB = MemberDatabase.getInstance(baseContext)
                    ////LOGIN 된 유져가 있다면 multy = false
                    val users : List<User>? =  userDB?.userDao()?.getUsers()
                    if(users!!.size>0) {
                        var login_user: User? = userDB?.userDao()?.getMultyLoginUser(true)
                        if (login_user!!.multy_login!!) {
                            login_user.multy_login = false
                            userDB?.userDao()?.update(login_user)
                        }
                    }
                    //////////////////user Change //////////////
                    val user = userDB?.userDao()?.getUser(userId)
                    user!!.multy_login = true
                    userDB?.userDao()?.update(user)
                }

                val addThread = Thread(addRunnable)
                addThread.start()

//                startActivityForResult(nextIntent,LOGIN_ACTIVITY)
                finish()
            }

        }
    }
}
