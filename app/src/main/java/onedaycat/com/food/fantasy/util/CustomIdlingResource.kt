package onedaycat.com.food.fantasy.util

import android.support.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class CustomIdlingResource: IdlingResource {

    private var mCallback: IdlingResource.ResourceCallback? = null

    private val mIsIdleNow: AtomicBoolean = AtomicBoolean(true)

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        mCallback = callback
    }


    fun setIdleState(isIdleNow: Boolean) {
        with(mIsIdleNow) {
            this.set(isIdleNow)
        }

        mCallback?.let {
            if (isIdleNow) {
                it.onTransitionToIdle()
            }

            it
        }
    }
}