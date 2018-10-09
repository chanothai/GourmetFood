package onedaycat.com.foodfantasyservicelib.validate

import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.CreateProductInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput

interface ProductValidate {
    fun inputId(id: String)
    fun inputProduct(input: CreateProductInput?)
    fun inputLimitPaging(input: GetProductsInput)
}

class ProductMemoValidate: ProductValidate {
    override fun inputId(id: String) {
        if (id.isBlank() || id.isEmpty()) {

            throw Errors.InvalidInput
        }
    }

    override fun inputProduct(input: CreateProductInput?) {
        if (input != null) {
            if (input.price < 0
                    || (input.name.isEmpty() || input.name.isBlank()))
            {
                throw Errors.InvalidInputProduct
            }
        }

        throw Errors.InvalidInput
    }

    override fun inputLimitPaging(input: GetProductsInput) {
        if (input.limit < 1) {
            throw Errors.InvalidInputLimitPaging
        }
    }
}