package onedaycat.com.food.fantasy.ui.signup

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.service.CognitoService
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.CreateUserInput

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpViewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViewModel()
        userObserver()
        msgErrorObserver()
        clickedRegister()
    }

    private fun initViewModel() {
        signUpViewModel = ViewModelProviders.of(this,
                ViewModelUtil.createViewModelFor(SignUpViewModel(CognitoService()))).get(SignUpViewModel::class.java)
    }

    private fun userObserver() {
        signUpViewModel.userLiveData.observe(this, Observer {
            finish()
        })
    }

    private fun msgErrorObserver() {
        signUpViewModel.msgErrorLiveData.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun clickedRegister() {
        btn_register.setOnClickListener {

            val input = CreateUserInput(
                    r_username.text.toString(),
                    r_fullname.text.toString(),
                    r_password.text.toString()
            ).apply {
                gender = r_gender.text.toString()
            }

            launch(UI) {
                signUpViewModel.signUp(input)
            }
        }
    }
}
