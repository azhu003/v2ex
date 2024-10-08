package com.azhu.v2ex.ui.activity

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable

class UserDetailsActivity : BaseActivity() {

    companion object {
        fun start(context: Context, username: String) {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra("username",username)
            context.startActivity(intent)
        }
    }

    override fun getContentView(): @Composable () -> Unit {
        return {
            UserDetailsPage()
        }
    }

}

@Composable
private fun UserDetailsPage() {

}