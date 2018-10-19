package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.foodfantasyservicelib.validate.CartValidateAdapter

class CartService(
        private val cartRepo: CartRepo,
        private val cartValidateAdapter: CartValidateAdapter) {

    suspend fun addProductToCart(input: AddToCartInput): ProductQTY {
        cartValidateAdapter.inputCart(input)

        return ProductQTY().apply {
            this.productId = input.productID
            this.productName = input.productName
            this.price = input.price
            this.qty = input.qty
        }.also {
            cartRepo.upsert(it)
        }
    }

    suspend fun getCarts(): Cart {
        return cartRepo.getAll()
    }

    suspend fun addProductsToCart(input: AddCartsToCartInput): Cart {
        return Cart()
    }
}