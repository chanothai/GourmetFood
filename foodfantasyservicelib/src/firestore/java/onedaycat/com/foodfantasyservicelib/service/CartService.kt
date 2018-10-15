package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.foodfantasyservicelib.validate.CartValidateAdapter

class CartService(private val stockRepo: StockRepo,
                  private val cartRepo: CartRepo,
                  private val cartValidateAdapter: CartValidateAdapter) {

    fun addProductCarts(input: AddCartsToCartInput): Cart{
        return input.cart.let {cart->
            cart.products.let {
                val pStocks = stockRepo.getAllWithPrice(cart.products)

                val newCarts = Cart().apply {
                    this.userId = cart.userId
                }

                for ((i, pstock) in pStocks.withIndex()) {
                    val index = cart.products.indexOfFirst {productQty->
                        productQty.productId == pStocks[i].productStock?.productID
                    }

                    val newPStock = newProductQTY(
                            pstock.productStock?.productID ?: "",
                            pstock.productStock?.productName ?: "",
                            pstock.price,
                            input.cart.products[index].qty
                    )

                    newCarts.addPQTY(newPStock, pstock.productStock ?: throw Errors.ProductStockNotFound)
                }

                newCarts
            }.also {
                cartRepo.upsert(it)
            }.run {
                this
            }
        }
    }

    fun addProductCart(input: AddToCartInput): Cart {
        var cart: Cart?

        cartValidateAdapter.inputCart(input)

        //get cart if not found cart will create new cart
        cart = cartRepo.getByUserID(input.userID)

        with(cart) {
            if (this.products.size > 0) {
                this.userId = input.userID
                return@with
            }

            cart = Cart(
                    input.userID,
                    mutableListOf()
            )
        }

        return cart?.let {
            //get stock
            val pStock = stockRepo.getWithPrice(input.productID)

            //create new productQTY
            val newProductQTY = newProductQTY(
                    pStock.productStock?.productID ?: "",
                    pStock.productStock?.productName ?: "",
                    pStock.price,
                    input.qty)

            //add product qty to cart
            it.addPQTY(newProductQTY, pStock.productStock ?: throw Errors.ProductStockNotFound)

            //Save or Update Cart
            cartRepo.upsert(it)
            it
        } ?: throw Errors.CartNotFound
    }

    fun removeFromeCart(input: RemoveFromCartInput): Cart? {
        cartValidateAdapter.inputRemoveCart(input)

        val cart = cartRepo.getByUserID(input.userID)

        val pstock = stockRepo.getWithPrice(input.productID)

        cart.remove(newProductQTY(
                pstock.productStock?.productID ?: "",
                pstock.productStock?.productName ?: "",
                pstock.price,
                input.qty),
                pstock.productStock ?: throw Errors.ProductStockNotFound)

        cartRepo.upsert(cart)

        return cart
    }

    fun getCartWithUserID(input: GetCartInput): Cart? {
        cartValidateAdapter.inputGetCart(input)

        return cartRepo.getByUserID(input.userID)
    }

    fun deleteProductCart(input: DeleteCartInput) {

        cartRepo.delete(input.userID)
    }
}