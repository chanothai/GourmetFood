package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.*
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.*
import onedaycat.com.foodfantasyservicelib.validate.CartValidate

class CartService(private val stockRepo: StockRepo,
                  private val cartRepo: CartRepo,
                  private val cartValidate: CartValidate) {

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
                            pstock.productStock?.productID!!,
                            pstock.productStock?.productName!!,
                            pstock.price,
                            input.cart.products[index].qty
                    )

                    newCarts.addPQTY(newPStock, pstock.productStock!!)
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

        cartValidate.inputCart(input)

        //get cart if not found cart will create new cart
        cart = cartRepo.getByUserID(input.userID)

        with(cart) {
            this?.let {
                it.userId = input.userID
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
                    pStock.productStock!!.productID!!,
                    pStock.productStock?.productName!!,
                    pStock.price,
                    input.qty)

            //add product qty to cart
            it.addPQTY(newProductQTY, pStock.productStock!!)

            //Save or Update Cart
            cartRepo.upsert(it)
            it
        }!!
    }

    fun removeFromeCart(input: RemoveFromCartInput): Cart? {
        cartValidate.inputRemoveCart(input)

        val cart = cartRepo.getByUserID(input.userID)

        val pstock = stockRepo.getWithPrice(input.productID)

        cart?.remove(newProductQTY(
                pstock.productStock?.productID!!,
                pstock.productStock?.productName!!,
                pstock.price,
                input.qty),
                pstock.productStock!!)

        cartRepo.upsert(cart)

        return cart
    }

    fun getCartWithUserID(input: GetCartInput): Cart? {
        cartValidate.inputGetCart(input)

        return cartRepo.getByUserID(input.userID)
    }

    fun deleteProductCart(input: DeleteCartInput) {

        cartRepo.delete(input.userID)
    }
}