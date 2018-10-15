package onedaycat.com.food.fantasy.ui.forgotpassword


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import kotlinx.android.synthetic.main.fragment_confirm_forgot_password.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.service.cognito.CognitoService
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.GetConfirmPasswordInput

private const val ARG_USER = "username_param"
class ConfirmForgotPasswordFragment : Fragment() {

    private var paramUser: String? = null
    private var cognito: CognitoUserPool? = null

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.apply {
            paramUser = this.getString(ARG_USER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_forgot_password, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(username: String) = ConfirmForgotPasswordFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER, username)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()
        clickedConfirmPassword()
    }

    private fun initViewModel() {
        forgotPasswordViewModel = ViewModelProviders.of(this,
                ViewModelUtil.createViewModelFor(ForgotPasswordViewModel(CognitoService())))
                .get(ForgotPasswordViewModel::class.java)

        msgErrorObserver()
        userAuthObserver()
    }

    private fun msgErrorObserver() {
        forgotPasswordViewModel.msgErrorLiveData.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun userAuthObserver() {
        forgotPasswordViewModel.userAuthLiveData.observe(this, Observer {
            activity?.finish()
        })
    }

    private fun clickedConfirmPassword() {
        btn_confirm_password.setOnClickListener {
            paramUser?.let {username->
                val input = GetConfirmPasswordInput(
                        username,
                        c_new_pass.text.toString(),
                        "",
                        c_code_verify.text.toString()
                )

                input
            }?.also {input->

                launch(UI) {
                    forgotPasswordViewModel.confirmPassword(input)
                }
            }
        }
    }
}
