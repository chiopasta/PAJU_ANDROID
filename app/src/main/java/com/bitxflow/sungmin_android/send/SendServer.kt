package com.bitxflow.sungmin_android.send

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class SendServer {

    private val localhost = "https://m.sungmin-i.com/"

    @Throws(IOException::class)
    private fun encodeParams(params: JSONObject): String? {
        val result = StringBuilder()
        var first = true
        val itr = params.keys()
        while (itr.hasNext()) {
            val key = itr.next()
            val value = params[key]
            if (first) first = false else result.append("&")
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value.toString(), "UTF-8"))
        }
        return result.toString()
    }

    fun requestGET(_url: String?): String {
        val url = URL(localhost + _url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            conn.inputStream.bufferedReader().readText()
        }catch (e : Exception) {
            ""
        }
    }

    fun requestPOST(_url: String?, postDataParams: JSONObject): String {
        val url = URL(localhost + _url)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.readTimeout = 3000
        conn.connectTimeout = 3000
        conn.requestMethod = "POST"
        conn.setRequestProperty("Accept","application/json")
        conn.doInput = true
        conn.doOutput = true
        try {
            val os: OutputStream = conn.outputStream
            val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
            writer.write(encodeParams(postDataParams))
            writer.flush()
            writer.close()
            os.close()
            val responseCode: Int = conn.responseCode // To Check for 200
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                return conn.inputStream.bufferedReader().readText()
            }
        }catch(e: Exception)
        {
            return ""
        }
        return ""
    }

}


