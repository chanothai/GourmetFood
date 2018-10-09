package onedaycat.com.food.fantasy.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import onedaycat.com.food.fantasy.R

class LoadingDialogFragment: DialogFragment() {

    companion object {
        fun newInstance() = LoadingDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        isCancelable = false

        return inflater.inflate(R.layout.dialog_loading, container)
    }

    class Builder {
        companion object {
            fun build(): LoadingDialogFragment = LoadingDialogFragment.newInstance()
        }
    }
}