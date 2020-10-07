package com.bitxflow.sungmin_android.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import java.io.*

@Throws(FileNotFoundException::class)
fun saveDrawable(
    imageView: ImageView,
    imagePath: String,
    context: Context
): Int {
    var result = 0
    val drawable = imageView.drawable
    val bounds = drawable.bounds
    var bitmap: Bitmap? = null
    bitmap = try {
        Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
    } catch (e: Exception) {
        Log.d("bitx_log","저장 에러 " + e.toString())
        return 0
    }
    if (bitmap == null) return 0
    Log.d("bitx_log","bitmap : $bitmap")
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    var fOut: OutputStream? = null
    try {
        val root = Environment.getExternalStorageDirectory()
        val f = File(root.absolutePath + "/Pictures/Sungmin/")
        f.mkdirs()
        val cachePath =
            File(root.absolutePath + "/Pictures/Sungmin/" + imagePath + ".jpg")
        try {
            Log.d("bitx_log", "out file $imagePath")
            fOut = FileOutputStream(cachePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut)
            fOut.flush()
            fOut.close()
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(cachePath)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
//            context.sendBroadcast(
//                Intent(
//                    Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/Pictures/Sungmin")
//                )
//            )
        } catch (e: Exception) {
            Log.d("bitx_log", "error :$e")
        }
        result = 1
    } finally {
        if (fOut != null) {
            try {
                fOut.close()
            } catch (e: IOException) {
                Log.d("bitx", "ERROR :$e")
            }
        }
    }
    return result
}