package onedaycat.com.food.fantasy.ui.signin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_in.w_username as username
import kotlinx.android.synthetic.main.activity_sign_in.w_password as password
import kotlinx.android.synthetic.main.activity_sign_in.s_forgot_password as forgotPass
import kotlinx.android.synthetic.main.activity_sign_in.w_btn_signin as btnSignIn
import kotlinx.android.synthetic.main.activity_sign_in.w_btn_signup as btnSignUp
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.service.cognito.CognitoService
import onedaycat.com.food.fantasy.ui.forgotpassword.ForgotPasswordActivity
import onedaycat.com.food.fantasy.ui.mainfood.activity.MainActivity
import onedaycat.com.food.fantasy.ui.signup.SignUpActivity
import onedaycat.com.food.fantasy.util.DateUtils
import onedaycat.com.food.fantasy.util.SharedPreferenceHelper
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.GetUserAuthenInput


class SignInActivity : AppCompatActivity() {

    private lateinit var signInViewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViewModel()
        clickSignIn()
        clickSignUp()
        clickForgotPassword()
    }


    private fun initViewModel() {
        signInViewModel = ViewModelProviders.of(this, ViewModelUtil.createViewModelFor(
                SignInViewModel(CognitoService())
        )).get(SignInViewModel::class.java)

        msgErrorObserver()
        userCognitoObserver()
    }

    private fun userCognitoObserver() {
        signInViewModel.userCogNiToLiveData.observe(this, Observer { token ->
            token?.let {
                SharedPreferenceHelper.setString(
                        SharedPreferenceHelper.usernameKey, it.username)
                SharedPreferenceHelper.setString(
                        SharedPreferenceHelper.tokenKey, it.accessToken)
                SharedPreferenceHelper.setString("" +
                        SharedPreferenceHelper.expireKey, DateUtils.toSimpleString(it.expired))

                startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }

    private fun msgErrorObserver() {
        signInViewModel.msgErrorLiveData.observe(this, Observer { msg ->
            msg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clickForgotPassword() {
        forgotPass.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun clickSignIn() {
        btnSignIn.setOnClickListener {
            val input = GetUserAuthenInput(
                    username.text.toString(),
                    password.text.toString()
            )

            launch(UI) {
                signInViewModel.signInUser(input)
            }
        }
    }

    private fun clickSignUp() {
        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            this.putString("UserName", username.text.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            password.setText(it.getString("UserName"))
        }
    }
}
