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

    //    lateinit var localhost : String
    private val ALL_TIMEOUT = 10L

    companion object Localhost {
        var localhost: String = ""
    }

    private val localhost = "https://m.sungmin-i.com/"
    private val web_localhost = "http://sungmin-i.com/"

//    private val httpclient: HttpClient= DefaultHttpClient()

    @Suppress("DEPRECATION")
    fun Login(id: String, pw: String, regId: String): String {

        val url = localhost+"login"
        val postDataParams = JSONObject()
        postDataParams.put("userid", id.toUpperCase())
        postDataParams.put("password", pw)

        return requestPOST(url,postDataParams)
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
//            val `in` = BufferedReader(InputStreamReader(conn.inputStream))
//            Log.d("bitx_log","`data`:"+ data)
//            val sb = StringBuffer("")
//            var line: String? = ""
//            while (`in`.readLine().also { line = it } != null) {
//
//                sb.append(line)
//                break
//            }
//            `in`.close()
//            Log.d("bitx_log","sb:"+sb.toString())
//            return sb.toString()
            }
        }catch(e: Exception)
        {
            return ""
        }
        return ""
    }

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
        return conn.inputStream.bufferedReader().readText()
    }

    fun getAllBoardList(userid: String): String {
        var line = "fail"
        try {


        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getBoardList(userid: String,classname : String): String {
        var line = "fail"
        try {


        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getCalendar(year: String,month : String): String {
        var line = "fail"
        try {

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun SendAttend(attendstatus: String,reason : String): String {
        var line = "fail"
        try {

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

        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }


    fun sendReply(boardSid : String, userId : String,userName : String
                  ,replySecretYN : String ,replyContent: String): String {
        var line = "fail"
        try {


        } catch (e: Exception) {
            Log.d("bitx_log", "error:$e")
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getPhotoDetail(photoID: String): String {
        var line = "fail"
        try {
            line = getStringFromUrl(localhost + "photo/comment/"+photoID)
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
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getMeal(): String {
        var line = "fail"

        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getMobilePush(): String {
        var line = "fail"
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return line
    }

    @Suppress("DEPRECATION")
    fun getHomeLetterList(): String {
        var line = "fail"
        try {


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

     fun readStream(inputStream: BufferedInputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }

    fun sendPostRequest(userName:String, password:String) {

        var reqParam = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")
        val mURL = URL("<Your API Link>")

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "POST"

            val wr = OutputStreamWriter(getOutputStream());
            wr.write(reqParam);
            wr.flush();

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")
            }
        }
    }




}


