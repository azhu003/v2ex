package com.azhu.v2ex.ui.activity

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.azhu.v2ex.R
import com.azhu.v2ex.ui.component.LoadingLayout
import com.azhu.v2ex.ui.theme.custom
import com.azhu.v2ex.viewmodels.LoginViewModel

/**
 * @author: Jerry
 * @date: 2024-10-19 23:28
 * @version: 1.0.0
 */
class LoginActivity : BaseActivity() {

    private val vm by viewModels<LoginViewModel>()

    override fun initialize() {
        setAppBarTitle(getString(R.string.login))
        vm.fetchLoginParams()
    }

    override fun getContentView(): @Composable () -> Unit {
        return {
            LoginPage(vm)
        }
    }
}

@Composable
fun LoginPage(vm: LoginViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val form = vm.form

    LoadingLayout(vm.state, Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
        ) {

            Spacer(Modifier.height(70.dp))

            TextField(
                value = form.username,
                onValueChange = { form.username = it },
                placeholder = { Text(text = context.getString(R.string.username_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.custom.background),
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = form.password,
                onValueChange = { form.password = it },
                placeholder = { Text(text = context.getString(R.string.password_placeholder)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.custom.background),
            )

            Spacer(Modifier.height(16.dp))

            AsyncImage(
                model = vm.params.captchaImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                clipToBounds = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vm.refreshCaptchaImage() },
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = form.captcha,
                onValueChange = { form.captcha = it },
                placeholder = { Text(text = context.getString(R.string.captcha_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.custom.background),
            )

            Spacer(Modifier.height(16.dp))

            // Error Message
            if (vm.warning.isNotEmpty()) {
                Text(
                    text = vm.warning,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { vm.login() },
                enabled = vm.ui.isLoading.value
            ) {
                Text(text = context.getString(R.string.login), color = Color.White)
            }
        }
    }
}