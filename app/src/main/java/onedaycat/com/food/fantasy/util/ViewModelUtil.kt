package onedaycat.com.food.fantasy.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

object ViewModelUtil {
    var mockViewModel: ViewModelProvider.Factory? = null

    get() {
        return field?.let {
            it
        }
    }

    set(value) {
        value?.let {
            field = value
        }
    }

    fun <T : ViewModel> createViewModelFor(model: T): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(model.javaClass)) {
                        return model as T
                    }
                    throw IllegalArgumentException("Unexpected model class $modelClass")
                }
            }
}