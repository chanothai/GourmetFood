package onedaycat.com.food.fantasy.ui.forgotpassword


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.service.cognito.CognitoService
import onedaycat.com.food.fantasy.util.ViewModelUtil
import onedaycat.com.foodfantasyservicelib.input.GetUsernameInput

class ForgotPasswordFragment : Fragment() {
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ForgotPasswordFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        clickedForgotPassword()
    }

    private fun initViewModel() {
        forgotPasswordViewModel = ViewModelProviders.of(this,
                ViewModelUtil.createViewModelFor(ForgotPasswordViewModel(CognitoService()))).get(ForgotPasswordViewModel::class.java)

        usernameObserver()
        msgErrorObserver()
    }

    private fun usernameObserver() {
        forgotPasswordViewModel.usernameLiveData.observe(this, Observer {
            it?.let {username->
                (activity as ForgotPasswordActivity).replaceFragment(ConfirmForgotPasswordFragment.newInstance(username))
            }
        })
    }

    private fun msgErrorObserver() {
        forgotPasswordViewModel.msgErrorLiveData.observe(this, Observer {
            it?.let {error->
                Toast.makeText(context!!, error, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun clickedForgotPassword() {
        btn_forgotpassword.setOnClickListener {
            val input = GetUsernameInput(
                    f_username.text.toString()
            )

            launch(UI) {
                forgotPasswordViewModel.forgotPassword(input)
            }
        }
    }
}
