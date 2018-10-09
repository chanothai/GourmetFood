package onedaycat.com.food.fantasy.ui.cart

data class CartModel(
        var cartPId: String = "",
        var cartName: String = "",
        var cartPrice: Int = 0,
        var cartTotalPrice: Int = 0,
        var cartQTY: Int = 0,
        var hasFood: Boolean = false
)



