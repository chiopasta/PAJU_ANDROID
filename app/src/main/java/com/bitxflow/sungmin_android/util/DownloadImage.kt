package com.bitxflow.sungmin_android.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.net.URL

class DownloadImage(val bmImage: ImageView): AsyncTask<String, Void, Bitmap>()
{

    override fun doInBackground(vararg urls: String?): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return mIcon11
    }

    override fun onPostExecute(result: Bitmap?) {
        Log.d("bitx_log","result $result")
        bmImage!!.setImageBitmap(result)
    }
}