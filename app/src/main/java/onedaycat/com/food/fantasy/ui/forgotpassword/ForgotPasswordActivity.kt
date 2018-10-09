package onedaycat.com.food.fantasy.ui.forgotpassword

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.common.BaseActivity

class ForgotPasswordActivity : BaseActivity() {

    override fun isDisplayHomeEnable(): Boolean? = false

    override fun getToolbarInstance(): Toolbar? = null

    override fun title() = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        replaceFragment(ForgotPasswordFragment.newInstance())
    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_forgot_password, fragment, "ForgotPasswordFragment")
                .commit()
    }
}
