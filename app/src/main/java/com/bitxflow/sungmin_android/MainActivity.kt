package com.bitxflow.sungmin_android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bitxflow.sungmin_android.DB.MemberDatabase
import com.bitxflow.sungmin_android.biz.board.AllBoardFragment
import com.bitxflow.sungmin_android.biz.board.BoardFragment
import com.bitxflow.sungmin_android.biz.calendar.CalendarFragment
import com.bitxflow.sungmin_android.biz.home.HomeFragment
import com.bitxflow.sungmin_android.biz.homeletter.HomeLetterFragment
import com.bitxflow.sungmin_android.biz.photo.PhotoFragment
import com.bitxflow.sungmin_android.biz.plan.PlanFragment
import com.bitxflow.sungmin_android.biz.setting.SettingFragment
import com.bitxflow.sungmin_android.biz.splash.SplashActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_list_header.*
import java.io.File


class MainActivity : AppCompatActivity() {

    //    private val LOGIN_ACTIVITY : Int = 0
    private val SPLASH_ACTIVITY: Int = 0

    private var mCurrentFragmentIndex = 0
    private val HOME = 0
    private val PHOTO = 1
    private val HOMELETTER = 2
    private val PLAN = 3
    private val ALL_BOARD = 4
    private val BOARD = 5
    private val CALENDAR = 6
    private val SETTING = 7

    private val GALLERY = 100
    private val CROP_FROM_CAMERA = 101
    private var userDB: MemberDatabase? = null

    private var menu_move_y: Int = 0
    private var ori_height: Int = 0
    private var bottom_nav_height: Int = 0

    private var user_id: String = ""

    var isTwo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        main_progressbar.visibility = View.GONE

        val nextIntent = Intent(this, SplashActivity::class.java)
        startActivityForResult(nextIntent, SPLASH_ACTIVITY)

//        val testRunnable = Runnable {
//
//            userDB = MemberDatabase.getInstance(baseContext)
//
////            var user : User? = userDB?.userDao()?.getUser("ssc001236")
//
////            val users : List<User>? = userDB?.userDao()?.getUsers()
//            val user = userDB?.userDao()?.getMultyLoginUser(true)
//
////            val str = user!!.imgSrc?.toUri()
////            info_iv.setImageURI(str)
//
////            Log.d("bitx_log","str : $str")
////            users = userDB?.userDao()?.getUsers()
//
//        }
//
//        val thread = Thread(testRunnable)
//        thread.start()

        //////////////////// BOTTOM NAV ///////////////////////
        var close_menu = true

        bottom_hold_rl.setOnClickListener {
            if (close_menu) {
                val ori_height = bottom_nav_rl.layoutParams.height
                bottom_nav_rl.layoutParams.height = ori_height * 2
                bottom_nav_rl.invalidate()
                bottom_nav_rl.requestLayout()
                close_menu = false
            } else {
                val ori_height = bottom_nav_rl.layoutParams.height
                bottom_nav_rl.layoutParams.height = ori_height / 2
                bottom_nav_rl.invalidate()
                bottom_nav_rl.requestLayout()
                close_menu = true
            }
        }

        nav_home_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != HOME) {
                menu_click(false)
                mCurrentFragmentIndex = HOME
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_photo_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != PHOTO) {
                menu_click(false)
                mCurrentFragmentIndex = PHOTO
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_letter_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != HOMELETTER) {
                menu_click(false)
                mCurrentFragmentIndex = HOMELETTER
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_plan_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != PLAN) {
                menu_click(false)
                mCurrentFragmentIndex = PLAN
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_all_board_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != ALL_BOARD) {
                menu_click(false)
                mCurrentFragmentIndex = ALL_BOARD
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_board_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != BOARD) {
                menu_click(false)
                mCurrentFragmentIndex = BOARD
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_calendar_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != CALENDAR) {
                menu_click(false)
                mCurrentFragmentIndex = CALENDAR
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        nav_setting_ll.setOnClickListener(View.OnClickListener {
            if (mCurrentFragmentIndex != SETTING) {
                menu_click(false)
                mCurrentFragmentIndex = SETTING
                fragmentReplace(mCurrentFragmentIndex)
            }
        })

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

            })


    }

    private fun fragmentReplace(reqNewFragmentIndex: Int) {
        val newFragment: Fragment = getFragment(reqNewFragmentIndex)
        // replace fragment
        val transaction = supportFragmentManager
            .beginTransaction()

        transaction.replace(R.id.ll_fragment, newFragment)

        transaction.addToBackStack(null)
        transaction.commitAllowingStateLoss()
    }

    private fun getFragment(idx: Int): Fragment {
        var newFragment: Fragment? = null
        val bundle = Bundle()
        bundle.putString("user_id", user_id)
        when (idx) {
            HOME -> {
                newFragment = HomeFragment()
                main_progressText.text = "Loading..."
            }
            PHOTO -> {
                newFragment = PhotoFragment()
                main_progressText.text = "사진첩 로딩중..."
            }
            HOMELETTER -> {
                newFragment = HomeLetterFragment()
                main_progressText.text = "가정통신문 로딩중..."
            }
            PLAN -> {
                newFragment = PlanFragment()
                main_progressText.text = "교육계획안 로딩중..."
            }
            ALL_BOARD -> {
                newFragment = AllBoardFragment()
                main_progressText.text = "전체게시판 로딩중..."
            }
            BOARD -> {
                newFragment = BoardFragment()
                main_progressText.text = "전체게시판 로딩중..."
            }
            CALENDAR -> {
                newFragment = CalendarFragment()
                main_progressText.text = "일정 로딩중..."
            }
            SETTING -> newFragment = SettingFragment()

        }
        main_progressbar.visibility = View.VISIBLE

        newFragment!!.arguments = bundle
        return newFragment
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SPLASH_ACTIVITY -> {
                    val backPress = data?.getStringExtra("BackPress")
                    if (backPress.equals("BackPress")) finish()
                    else {
                        user_id = backPress.toString()
                        mCurrentFragmentIndex = HOME
                        fragmentReplace(mCurrentFragmentIndex)
                    }
                }
                GALLERY -> {
                    if (resultCode === Activity.RESULT_OK) {
                        try {
                            Log.d("bitx_log", "data ? " + data!!.data)
                            var mImageCaptureUri = data!!.data

//                            info_iv.setImageURI(selectedImage)

                            val testRunnable = Runnable {

                                userDB = MemberDatabase.getInstance(baseContext)

                                val user = userDB?.userDao()?.getUser(user_id)
                                user!!.imgSrc = data!!.data.toString()
                                userDB?.userDao()?.update(user)
                            }

                            val thread = Thread(testRunnable)
                            thread.start()


                            grantUriPermission(
                                "com.android.camera", mImageCaptureUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            val intent = Intent("com.android.camera.action.CROP")
//				Intent intent = new Intent(thisContext,CropImage.class);
                            //				Intent intent = new Intent(thisContext,CropImage.class);
                            intent.setDataAndType(mImageCaptureUri, "image/*")
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            val list =
                                packageManager.queryIntentActivities(intent, 0)
                            grantUriPermission(
                                list[0].activityInfo.packageName, mImageCaptureUri,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                            //				intent.putExtra("outputX", 90);
//				intent.putExtra("outputY", 90);
//				intent.putExtra("aspectX", 1);
//				intent.putExtra("aspectY", 1);
                            intent.putExtra("crop", "true")
//                intent.putExtra("aspectX", 4);
//                intent.putExtra("aspectY", 3);
                            //                intent.putExtra("aspectX", 4);
//                intent.putExtra("aspectY", 3);
                            intent.putExtra("scale", true)
//                intent.putExtra("return-data", true);
                            //                intent.putExtra("return-data", true);
                            intent.putExtra("return-data", true)

//                            val filePathColumn =
//                                arrayOf(MediaStore.Images.Media.DATA)
//                            val cursor: Cursor = this.getContentResolver()
//                                .query(selectedImage, filePathColumn, null, null, null)
//
//                            cursor.moveToFirst()
//                            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
//                            val picturePath: String = cursor.getString(columnIndex)
//                            Log.d("bitx_log","file path :" + picturePath)

                            val imageFileName = user_id
                            val storageDir =
                                File(Environment.getExternalStorageDirectory().toString() + "/imageDir/")
                            if (!storageDir.exists()) {
                                storageDir.mkdirs()
                            }

                            var image: File? = null
                            try {
                                image = File.createTempFile(
                                    imageFileName,
                                    ".jpg",
                                    storageDir
                                )
                            } catch (e: java.lang.Exception) {
                            }

                            mImageCaptureUri = Uri.fromFile(image)

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
                            startActivityForResult(intent, CROP_FROM_CAMERA)
                        } catch (e: Exception) {
                        }
                    } else if (resultCode === Activity.RESULT_CANCELED) {
                        Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
                    }

                    //                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행
                    //                    mImageCaptureUri = FileProvider.getUriForFile(thisContext, thisContext.getPackageName() + ".fileprovider", image);


                }
                CROP_FROM_CAMERA ->
                {
                    val selectedImage = data!!.data
                    info_iv.setImageURI(selectedImage)
                }
            }
        }
    }

    fun menu_click(click: Boolean) {
        nav_home_ll.isClickable = click
        nav_photo_ll.isClickable = click
        nav_letter_ll.isClickable = click
        nav_plan_ll.isClickable = click
        nav_all_board_ll.isClickable = click
        nav_calendar_ll.isClickable = click
        nav_board_ll.isClickable = click
        nav_setting_ll.isClickable = click
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {

            if (!isTwo) {
                Toast.makeText(
                    this, "'뒤로'버튼 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT
                ).show()

                val timer = myTimer(2000, 1)
                timer.start()

            } else  // super.onBackPressed();
            {
                finish()
                super.onBackPressed()
                Process.killProcess(Process.myPid())
            }
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    internal inner class myTimer(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            isTwo = false
        }

        override fun onTick(millisUntilFinished: Long) {
        }

        init {
            isTwo = true
        }
    }
}
