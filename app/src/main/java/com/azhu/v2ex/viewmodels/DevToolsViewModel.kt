package com.azhu.v2ex.viewmodels

import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import com.azhu.basic.AppManager
import com.azhu.basic.provider.logger
import com.azhu.v2ex.ui.component.LoadingDialogState
import com.azhu.v2ex.ui.component.MessageDialogState
import com.azhu.v2ex.ui.component.ReplayDialogState
import com.azhu.v2ex.utils.V2exUtils

/**
 * @author: Jerry
 * @date: 2024-11-02 00:08
 * @version: 1.0.0
 */
class DevToolsViewModel : BaseViewModel() {

    val messageDialog = MessageDialogState()
    val loadingDialog = LoadingDialogState()
    val replyDialog = ReplayDialogState(
        onInsertLinkClick = ::onInsertLink,
        onSubmit = ::onSubmit
    )
    val reply = ReplayDialogState(
        onInsertLinkClick = ::onInsertLink,
        onSubmit = ::onSubmit
    )

    val registerForActivityResult: ActivityResultCallback<Uri?> = ActivityResultCallback { uri ->
        logger.i("ActivityResultCallback -> selected file uri = $uri")
        if (uri != null) {
            val context = AppManager.getCurrentActivity()
            if (context != null) {
                val path = V2exUtils.getRealPathFromURI(context, uri)
                logger.i("ActivityResultCallback context -> ${context.javaClass.name} path = $path")
            }
        }
    }

    private fun onInsertImage(uri: Uri) {

    }

    private fun onInsertLink() {

    }

    private fun onSubmit() {

    }
}