package onedaycat.com.food.fantasy.common

import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import kotlinx.android.synthetic.main.appbar_normal.view.*
import onedaycat.com.food.fantasy.R
import onedaycat.com.food.fantasy.dialog.LoadingDialogFragment


abstract class BaseActivity: AppCompatActivity() {

    private val tagDialogFragment = "dialog_fragment"
    private var loadingDialogFragment: LoadingDialogFragment? = null
    private lateinit var toolbar: Toolbar

    override fun onResume() {
        super.onResume()

        this.getToolbarInstance()?.let {
            this.initView(it)
        }
    }

    private fun initView(toolbar: Toolbar) {
        this.toolbar = toolbar

        title()?.let {
            toolbar.title_toolbar.text = it
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            supportActionBar?.setDisplayHomeAsUpEnabled(isDisplayHomeEnable()!!)

            it.setDisplayShowTitleEnabled(false)
        }
    }

    fun updateTitleToolbar(title: String) {
        toolbar.title_toolbar.text = title
    }

    abstract fun isDisplayHomeEnable(): Boolean?
    abstract fun getToolbarInstance(): Toolbar?
    abstract fun title(): String?

    fun showLoadingDialog() {
        dismissDialog()
        loadingDialogFragment = LoadingDialogFragment.Builder.build()
        createFragmentDialog(loadingDialogFragment!!)
    }

    fun dismissDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment?.dismiss()
        }
    }

    private fun createFragmentDialog(dialogFragment: DialogFragment) {
        dialogFragment.show(supportFragmentManager, tagDialogFragment)
    }
}