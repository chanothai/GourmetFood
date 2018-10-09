package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.AddToCartInput
import onedaycat.com.foodfantasyservicelib.input.GetCartInput
import onedaycat.com.foodfantasyservicelib.input.RemoveFromCartInput

interface CartValidate{
    fun inputCart(input: AddToCartInput)
    fun inputRemoveCart(input: RemoveFromCartInput)
    fun inputGetCart(input: GetCartInput)
}

class CartMemoValidate: CartValidate {
    override fun inputCart(input: AddToCartInput) {
        if ((input.userID.isBlank()
                        || input.userID.isEmpty()
                        || input.qty < 0)) {

            throw Errors.InvalidInput
        }
    }

    override fun inputRemoveCart(input: RemoveFromCartInput) {
        if (input.qty < 0
                || input.productID.isBlank()
                || input.productID.isEmpty()
                || input.userID.isBlank()
                || input.userID.isEmpty())
        {
            throw Errors.InvalidInput
        }
    }

    override fun inputGetCart(input: GetCartInput) {
        if (input.userID.isBlank() || input.userID.isEmpty()) {
            throw Errors.InvalidInput
        }
    }
}