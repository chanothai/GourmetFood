package onedaycat.com.food.fantasy.store

import android.arch.lifecycle.MutableLiveData

object FoodCartLiveStores {
    var liveData = MutableLiveData<CartStore>()

    init {
        CartStore.let {
            liveData.postValue(CartStore)
        }
    }
}