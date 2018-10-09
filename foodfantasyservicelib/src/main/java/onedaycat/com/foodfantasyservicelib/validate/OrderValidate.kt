package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput

interface OrderValidate {
    fun inputGetOrder(input: GetOrderInput)
}

class OrderMemoValidate: OrderValidate {
    override fun inputGetOrder(input: GetOrderInput) {
        if (input.id.isEmpty() || input.id.isBlank()) {
            throw Errors.InvalidInput
        }
    }
}