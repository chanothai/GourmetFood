package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors

data class Cart(
        var userId: String? = null,
        var products: MutableList<ProductQTY> = mutableListOf())
{
    private var cart = this

    fun newCart(id:String): Cart {
        return Cart(
                userId = id,
                products = mutableListOf()
        )
    }

    private fun checkStock(productQTY: ProductQTY, stock: ProductStock) {
        if (productQTY.productId != stock.productID) {
            throw Errors.ProductNotMatched
        }

        val qty = productQTY.qty

        if (!stock.has(qty)) {
            throw Errors.ProductOutOfStock
        }
    }

    fun addPQTY(productQTY: ProductQTY, stock: ProductStock) {
        checkStock(productQTY, stock)

        if (products.size == 0) {
            products.add(productQTY)
        }

        for ((i, value) in products.withIndex()) {
            if (value.productId == productQTY.productId) {
                products[i] = productQTY
                return
            }
        }

        products.add(productQTY)
    }

    fun remove(newProductQTY: ProductQTY, stock: ProductStock) {
        if (products.size == 0) {
            throw Errors.ProductNotFound
        }

        for ((i, productQty) in products.withIndex()) {
            if (newProductQTY.productId == productQty.productId) {
                val result = productQty.qty - newProductQTY.qty

                if (result < 0) {
                    throw Errors.UnableRemoveProduct
                }

                if (result == 0) {
                    products.removeAt(i)
                    stock.qty += newProductQTY.qty
                    return
                }

                productQty.qty = result

                products[i] = productQty

                stock.qty += newProductQTY.qty
                return
            }
        }

        throw Errors.ProductNotFound
    }

    fun productIDs(): MutableList<String> {
        val arrProductId = mutableListOf<String>()
        if (cart.products.size == 0) {
            throw Errors.ProductNotFound
        }

        for (product in cart.products) {
            arrProductId.add(product.productId)
        }

        return arrProductId
    }

    fun getPQTY(productId: String): ProductQTY? {
        if (cart.products.size == 0) {
            return null
        }

        for (pQTY in cart.products) {
            if (productId == pQTY.productId) {
                return pQTY
            }
        }

        return null
    }

    fun toProductQTYList(): MutableList<ProductQTY> {
        if (cart.products.size == 0) {
            throw Errors.ProductNotFound
        }

        return cart.products
    }

    fun totalPrice(): Int {
        var sumTotalPrice = 0
        for (product in cart.products) {
            sumTotalPrice += (product.price * product.qty)
        }

        return sumTotalPrice
    }
}

