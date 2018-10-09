package onedaycat.com.food.fantasy.ui.cart

interface OnActionCartListener {
    fun onAddCart(cartModel: onedaycat.com.food.fantasy.ui.cart.CartModel)
    fun onRemoveCart(cartModel: onedaycat.com.food.fantasy.ui.cart.CartModel)
    fun onTextChange(text: String, position: Int)
}