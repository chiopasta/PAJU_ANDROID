package com.bitxflow.sungmin_android.biz.homeletter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bitxflow.sungmin_android.R
import com.bitxflow.sungmin_android.util.DownloadImage
import kotlinx.android.synthetic.main.activity_home_letter.*

class HomeLetterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_letter)

        val intent = intent
        val url = intent.extras!!.getString("content")

        DownloadImage(letter_image).execute(url)
//        DownloadImageTask(meal_iv).execute(url)

    }
}
