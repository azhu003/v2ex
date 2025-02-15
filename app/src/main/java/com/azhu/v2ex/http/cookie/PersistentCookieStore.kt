package com.azhu.v2ex.http.cookie

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.ConcurrentHashMap

/**
 * 根据一定的规则去缓存或者获取Cookie
 * @author Zhaopo Liu
 * @see <a href="http://www.lzhpo.com/article/158">Android拓展okhttp3.Cookie实现Cookie管理(缓存、持久化、移除...)</a>
 */
object PersistentCookieStore {

    private const val LOG_TAG: String = "PersistentCookieStore"
    private const val COOKIE_PREFS: String = "Cookies_Prefs"

    private val cookies: MutableMap<String, ConcurrentHashMap<String, Cookie>> = HashMap()
    private lateinit var cookiePrefs: SharedPreferences

    fun init(context: Context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0)

        // 将持久化的cookies缓存到内存中，即 map cookies
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            val cookieNames = TextUtils.split(value as String?, ",")
            for (name in cookieNames) {
                val encodedCookie = cookiePrefs.getString(name, null)
                if (encodedCookie != null) {
                    val decodedCookie = decodeCookie(encodedCookie)
                    if (decodedCookie != null) {
                        if (!cookies.containsKey(key)) {
                            cookies[key] = ConcurrentHashMap()
                        }
                        cookies[key]!![name!!] = decodedCookie
                    }
                }
            }
        }
    }

    private fun getCookieToken(cookie: Cookie): String {
        return cookie.name + "@" + cookie.domain
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)

        // 将cookies缓存到内存中，如果缓存过期，就重置此cookie
        if (cookie.persistent || cookie.expiresAt > System.currentTimeMillis()) {
            if (!cookies.containsKey(url.host)) {
                cookies[url.host] = ConcurrentHashMap()
            }
            cookies[url.host]!![name] = cookie
        } else {
            if (cookies.containsKey(url.host)) {
                cookies[url.host]!!.remove(name)
            }
        }

        // 将cookies持久化到本地
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(url.host, TextUtils.join(",", cookies[url.host]?.keys()?.toList() ?: emptyList<String>()))
        prefsWriter.putString(name, encodeCookie(SerializableCookies(cookie)))
        prefsWriter.apply()
    }

    fun get(url: HttpUrl): List<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(url.host)) ret.addAll(cookies[url.host]!!.values)
        return ret
    }

    fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)

        if (cookies.containsKey(url.host) && cookies[url.host]!!.containsKey(name)) {
            cookies[url.host]!!.remove(name)

            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(name)) {
                prefsWriter.remove(name)
            }
            prefsWriter.putString(url.host, TextUtils.join(",", cookies[url.host]!!.keys))
            prefsWriter.apply()
            return true
        } else {
            return false
        }
    }

    fun getCookies(): List<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys) ret.addAll(cookies[key]!!.values)

        return ret
    }

    /**
     * cookies 序列化成 string
     *
     * @param cookie 要序列化的cookie
     * @return 序列化之后的string
     */
    private fun encodeCookie(cookie: SerializableCookies?): String? {
        if (cookie == null) return null
        val byteArrayInputStream = ByteArrayOutputStream()
        var outputStream: ObjectOutputStream? = null
        try {
            outputStream = ObjectOutputStream(byteArrayInputStream)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e)
            return null
        } finally {
            outputStream?.close()
            byteArrayInputStream.close()
        }
        return byteArrayToHexString(byteArrayInputStream.toByteArray())
    }

    /**
     * 将字符串反序列化成cookies
     *
     * @param cookieString cookies string
     * @return cookie object
     */
    private fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableCookies).getCookies()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        } finally {
            byteArrayInputStream.close()
        }

        return cookie
    }

    /**
     * 二进制数组转十六进制字符串
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
//        val sb = StringBuilder(bytes.size * 2)
//        for (element in bytes) {
//            val v = element.toInt() and 0xff
//            if (v < 16) {
//                sb.append('0')
//            }
//            sb.append(Integer.toHexString(v))
//        }
//        return sb.toString().uppercase(Locale.US)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * 十六进制字符串转二进制数组
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private fun hexStringToByteArray(hexString: String): ByteArray {
//        val len = hexString.length
//        val data = ByteArray(len / 2)
//        var i = 0
//        while (i < len) {
//            data[i / 2] =
//                ((hexString[i].digitToIntOrNull(16) ?: (-1 shl 4)) + hexString[i + 1].digitToIntOrNull(16)!!).toByte()
//            i += 2
//        return data

        // 过滤掉可能的空格等无效字符，并确保长度为偶数
        val hex = hexString.filterNot { it.isWhitespace() }
        require(hex.length % 2 == 0) { "Hex string must have an even length" }

        return ByteArray(hex.length / 2) { i ->
            val index = i * 2
            hex.substring(index, index + 2).toInt(16).toByte()
        }
    }
}