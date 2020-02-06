package com.bitxflow.sungmin_android.send

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.HTTP
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.math.BigInteger
import java.net.URI
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException


class SendServer {

    //    lateinit var localhost : String
    private val ALL_TIMEOUT = 10L

    companion object Localhost {
        var localhost: String = ""
        var httpclinet: HttpClient? = null
    }

//        private val web_localhost = "http://222.233.185.212/"
    private val web_localhost = "http://sungmin-i.com/"

//    private val httpclient: HttpClient= DefaultHttpClient()

    @Suppress("DEPRECATION")
    fun Login(id: String, pw: String, regId: String): String {
        var line = "fail"
        try {
//            httpclient = DefaultHttpClient()
            //TODO
            var Sungmin = true
            if (id.substring(0, 3).equals("SSC", ignoreCase = true)) {
                Localhost.localhost = "http://sungmin-i.net/"
//                Localhost.localhost = "http://192.168.0.13/"
            }
//            else if (id.substring(0, 3).equals("JSC", ignoreCase = true))
//                localhost = "http://suji-sungmin-i.net/"
//            else if (id.substring(0, 3).equals("NSC", ignoreCase = true))
//                localhost = "http://s2-sungmin-i.net/"
//            else if (id.substring(0, 3).equals("HSC", ignoreCase = true))
//                localhost = "http://hwasan-sungmin-i.net/"
//            else if (id.substring(0, 3).equals("GSC", ignoreCase = true))
//                localhost = "http://gugal-sungmin-i.net/"
            else
                Sungmin = false

            if (Sungmin) {
                Localhost.httpclinet = DefaultHttpClient();

                var httpclient: HttpClient? = Localhost.httpclinet

                val httppost = HttpPost(Localhost.localhost + "android/loginRSA.android" + "")
                val responseHandler = BasicResponseHandler()
                val response = httpclient!!.execute(httppost, responseHandler)
                val publicKey = getPublicKey(response)

                val encodeID = Encrypt(id, publicKey)
                val encodePW = Encrypt(pw, publicKey)

                httppost.setURI(URI(Localhost.localhost + "android/loginProc.android"))
                val nameValuePairs = ArrayList<NameValuePair>(2)
                nameValuePairs.add(BasicNameValuePair("id", encodeID))
                nameValuePairs.add(BasicNameValuePair("pass", encodePW))
                nameValuePairs.add(BasicNameValuePair("token", regId))
                httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
                val response2 = httpclient.execute(httppost, responseHandler)

                line = response2
            } else
                line = "fail"
        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }


        return line
    }

    fun getAllBoardList(userid: String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(web_localhost + "android/get_all_board.android")
            val responseHandler = BasicResponseHandler()
            val nameValuePairs = ArrayList<NameValuePair>(1)
            nameValuePairs.add(BasicNameValuePair("userid", userid))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getBoardList(userid: String,classname : String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(web_localhost + "android/get_my_board.android")
            val responseHandler = BasicResponseHandler()
            val nameValuePairs = ArrayList<NameValuePair>(2)
            nameValuePairs.add(BasicNameValuePair("userid", userid))
            nameValuePairs.add(BasicNameValuePair("classname",URLEncoder.encode(classname, "UTF-8")))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getCalendar(year: String,month : String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(Localhost.localhost + "android/getEventPlan.do")
            val responseHandler = BasicResponseHandler()
            val nameValuePairs = ArrayList<NameValuePair>(2)
            nameValuePairs.add(BasicNameValuePair("year", year))
            nameValuePairs.add(BasicNameValuePair("month",month))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    //TODO classname -> classSid (서버랑 같이작업)
    @Suppress("DEPRECATION")
    fun getPhotoList(userid : String,classname : String , page : String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(web_localhost + "android/newphotolist.mobile")
            val nameValuePairs = ArrayList<NameValuePair>(2)
            nameValuePairs.add(BasicNameValuePair("userid", userid))
            nameValuePairs.add(BasicNameValuePair("classname",classname))
            nameValuePairs.add(BasicNameValuePair("page", page))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }


    fun sendReply(boardSid : String, userId : String,userName : String
                  ,replySecretYN : String ,replyContent: String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(web_localhost + "mobile/reply_write.android")
            val responseHandler = BasicResponseHandler()
            val nameValuePairs = ArrayList<NameValuePair>(5)
            nameValuePairs.add(BasicNameValuePair("boardSid", boardSid))
            nameValuePairs.add(BasicNameValuePair("userId", userId))
            nameValuePairs.add(BasicNameValuePair("replyWriter", userName))
            nameValuePairs.add(BasicNameValuePair("replySecretYN", replySecretYN))
            nameValuePairs.add(BasicNameValuePair("replyContent", replyContent))
            httppost.entity = UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8)
//            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getPhotoDetail(userid: String,boardSid : String): String {
        var line = "fail"
        try {
            val httppost = HttpPost(web_localhost + "android/newphotodetail.android")
            val responseHandler = BasicResponseHandler()
            val nameValuePairs = ArrayList<NameValuePair>(2)
            nameValuePairs.add(BasicNameValuePair("userid", userid))
            nameValuePairs.add(BasicNameValuePair("boardSid", boardSid))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))
            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }


    fun getUser(): String {
        var line = "fail"

        try {

            line = getStringFromUrl(
                Localhost.localhost + "android/getUser.android"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("bitx_log", "error getUser:" + e.toString())
        }

        return line
    }


    fun getMeal(): String {
        var line = "fail"

        try {
            val httppost = HttpPost(Localhost.localhost + "android/mealinfo.do")
            val responseHandler = BasicResponseHandler()
            val response = Localhost.httpclinet!!.execute(httppost, responseHandler)
            line = response.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("bitx_log", "error getUser:" + e.toString())
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getMobilePush(): String {
        var line = "fail"
        try {
            val httppost = HttpPost(Localhost.localhost + "android/getNotice.do")

            val response: HttpResponse = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getHomeLetterList(): String {
        var line = "fail"
        try {
            val httppost = HttpPost(Localhost.localhost + "android/getHomeLetterList.do")

            val response: HttpResponse = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getPlanList(): String {
        var line = "fail"
        try {

            line = getStringFromUrl(Localhost.localhost + "android/educationplanlist.android")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("bitx_log", "planlist error : $e")
        }

        return line
    }


    @Suppress("DEPRECATION")
    fun infoUpdate(np : String , na : String , ph : String): String {
        var line = "fail"

        try {
            val httppost = HttpPost(Localhost.localhost + "android/infoupdate.do")
            val responseHandler = BasicResponseHandler()

            Log.d("bitx_log","np : $np na : $na \n ph : $ph")
            val nameValuePairs = ArrayList<NameValuePair>(3)
            nameValuePairs.add(BasicNameValuePair("np", np))
            nameValuePairs.add(BasicNameValuePair("na",URLEncoder.encode(na, "UTF-8")))
            nameValuePairs.add(BasicNameValuePair("ph", ph))
            httppost.setEntity(UrlEncodedFormEntity(nameValuePairs))

            val response = Localhost.httpclinet!!.execute(httppost)
            val responseString = EntityUtils.toString(response.getEntity(), "UTF_8")

            line = responseString

//            val response = Localhost.httpclinet!!.execute(httppost, responseHandler)
//            line = response.toString()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("bitx_log", "error :" + e.toString())
        }

        return line
    }


    @Throws(UnsupportedEncodingException::class)
    fun getStringFromUrl(url: String): String {
        val br = BufferedReader(
            InputStreamReader(
                getInputStreamFromUrl(url)!!, "UTF-8"
            )
        )
        val sb = StringBuffer()
        try {
            var line: String? = null
            do {
                line = br.readLine()
                sb.append(line)
            } while (line!!.isNotEmpty())

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    @SuppressLint("TrulyRandom")
    private fun Encrypt(data: String, publicKey: RSAPublicKey?): String? {
        var result: String? = null
        try {
            val clsCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            clsCipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val EncryptedData = clsCipher.doFinal(data.toByteArray())
            val resultData = Base64.encode(EncryptedData, Base64.DEFAULT)
            result = String(resultData)

        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }

        return result
    }

    internal fun getPublicKey(response: String): RSAPublicKey? {
        var publicKey: RSAPublicKey? = null
        try {
            val `object` = JSONObject(response)
            val publicKeyExponent = `object`.getString("exponent")
            val publicKeyModulus = `object`.getString("Modulus")

            val mo = BigInteger(publicKeyModulus, 16)
            val ex = BigInteger(publicKeyExponent, 16)

            val keyFactory = KeyFactory.getInstance("RSA")
            val pub = RSAPublicKeySpec(mo, ex)
            publicKey = keyFactory.generatePublic(pub) as RSAPublicKey
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        }

        return publicKey
    }

    fun getInputStreamFromUrl(url: String): InputStream? {
        var contentStream: InputStream? = null
        try {

            var httpclient: HttpClient? = Localhost.httpclinet

            val response = httpclient!!.execute(HttpGet(url))
            contentStream = response.entity.content
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("bitx_log", "getinputstream error:" + e.toString())
        }

        return contentStream
    }

    private fun convertStreamToString(`is`: InputStream): String {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
        } catch (e1: UnsupportedEncodingException) {
            e1.printStackTrace()
        }

        val sb = StringBuilder()

        var line: String
        try {
            do {
                line = reader!!.readLine()
                sb.append(line + "\n")
            } while (!line.isEmpty())
//            while ((line = reader!!.readLine()) != null) {
//                sb.append(line + "\n")
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                `is`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return sb.toString()
    }
}