package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.input.SubProductStockInput

interface StockValidate {
    fun inputPStock(input: AddProductStockInput)
    fun inputSubStock(input: SubProductStockInput)
}

class StockMemoValidate: StockValidate {
    override fun inputPStock(input: AddProductStockInput) {
        if (input.productID.isEmpty()
                || input.qty < 0
                || input.productID.isBlank()) {

            throw Errors.InvalidInputProductStock
        }
    }

    override fun inputSubStock(input: SubProductStockInput) {
        if (input.productID.isEmpty()
                || input.qty < 0
                || input.productID.isBlank()) {
            throw Errors.InvalidInputProductStock
        }
    }
}