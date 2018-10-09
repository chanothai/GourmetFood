package onedaycat.com.food.fantasy.store

import onedaycat.com.food.fantasy.ui.cart.CartModel

object CartStore {

    @JvmStatic var foodCart: FoodCartStore? = null

    @JvmStatic var counter:Int = 0
}

data class FoodCartStore(
        var userId: String? = null,
        var cartList: ArrayList<onedaycat.com.food.fantasy.ui.cart.CartModel>? = arrayListOf()
)