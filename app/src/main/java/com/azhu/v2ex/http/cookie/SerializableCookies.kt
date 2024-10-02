package com.azhu.v2ex.http.cookie

import okhttp3.Cookie
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * - 将Cookie对象输出为ObjectStream
 * - 将ObjectStream序列化成Cookie对象
 * @author Zhaopo Liu
 * @see <a href="http://www.lzhpo.com/article/158">Android拓展okhttp3.Cookie实现Cookie管理(缓存、持久化、移除...)</a>
 */
class SerializableCookies(@Transient private var cookies: Cookie) : Serializable {

    @Transient
    private var clientCookies: Cookie? = null

    fun getCookies(): Cookie {
        var bestCookies: Cookie = cookies
        if (clientCookies != null) {
            bestCookies = clientCookies as Cookie
        }
        return bestCookies
    }

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        out.writeObject(cookies.name)
        out.writeObject(cookies.value)
        out.writeLong(cookies.expiresAt)
        out.writeObject(cookies.domain)
        out.writeObject(cookies.path)
        out.writeBoolean(cookies.secure)
        out.writeBoolean(cookies.httpOnly)
        out.writeBoolean(cookies.hostOnly)
        out.writeBoolean(cookies.persistent)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(input: ObjectInputStream) {
        val name = input.readObject() as String
        val value = input.readObject() as String
        val expiresAt = input.readLong()
        val domain = input.readObject() as String
        val path = input.readObject() as String
        val secure = input.readBoolean()
        val httpOnly = input.readBoolean()
        val hostOnly = input.readBoolean()
        val persistent = input.readBoolean()
        var builder = Cookie.Builder()
        builder = builder.name(name)
        builder = builder.value(value)
        builder = builder.expiresAt(expiresAt)
        builder = if (hostOnly) builder.hostOnlyDomain(domain) else builder.domain(domain)
        builder = builder.path(path)
        builder = if (secure) builder.secure() else builder
        builder = if (httpOnly) builder.httpOnly() else builder
        clientCookies = builder.build()
    }
}