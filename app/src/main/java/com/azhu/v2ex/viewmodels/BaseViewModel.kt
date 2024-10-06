package com.azhu.v2ex.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.http.ApiException
import com.azhu.v2ex.http.Http
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

/**
 * @author: azhu
 * @date: 2024-09-29 22:23
 * @version: 1.0.0
 */
open class BaseViewModel : ViewModel() {

    val title = mutableStateOf(value = "")
    protected val http by lazy {
        Http()
    }

    fun toast(context: Context, message: String?) {
        if (message.isNullOrBlank()) {
            return
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    protected fun getHtmlFromAssets(fileName: String) = flow<Result<InputStream>> {
        val context = AppManager.getCurrentActivity()
        runCatching {
            context?.resources?.assets?.open(fileName) as InputStream
        }.onSuccess {
            emit(Result.success(it))
        }.onFailure {
            emit(Result.failure(ApiException(it.message)))
        }
    }

    protected fun parse(input: InputStream) = flow<Result<Document>> {
        runCatching {
            Jsoup.parse(input, "UTF-8", "https://www.v2ex.com/")
        }.onSuccess {
            emit(Result.success(it))
        }.onFailure {
            emit(Result.failure(it))
        }
    }

    protected fun getDocument(stream: InputStream): Document {
        return Jsoup.parse(stream, "UTF-8", "https://www.v2ex.com/")
    }

    protected inline fun str(block: () -> String): String {
        return try {
            block.invoke()
        } catch (e: Exception) {
            ""
        }
    }

    protected inline fun catch(block: () -> Unit) {
        try {
            block.invoke()
        } catch (e: Exception) {
            logger.info("error -> $e")
        }
    }

}