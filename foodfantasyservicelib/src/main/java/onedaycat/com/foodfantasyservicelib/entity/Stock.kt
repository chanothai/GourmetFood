package onedaycat.com.foodfantasyservicelib.entity

import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import kotlin.math.acos


data class ProductStockWithPrice(
        var productStock: ProductStock? = null,
        var price: Int = 0
)

data class ProductStock(
        var productID: String? = null,
        var productName: String = "",
        var qty: Int = 0) {

    private var productStock: ProductStock? = this

    fun createProductStock(productID: String,productName: String, qty: Int): ProductStock? {
        productStock = ProductStock(
                productID,
                productName,
                qty
        )

        return productStock
    }

    fun newProductStock(productID: String,productName: String, qty: Int): ProductStock {
        productStock = ProductStock(
                productID,
                productName,
                qty
        )

        deposit(qty)

        productStock?.let {
            return it
        }

        throw Errors.ProductStockNotFound
    }

    fun deposit(qty: Int) {
        productStock?.let {
            it.qty += qty
        }
    }

    fun withDraw(qty: Int): ProductStock {
        productStock?.let {ps->
            if ((ps.qty - qty) < 0) {
                throw Errors.ProductOutOfStock
            }

            ps.qty -= qty
            ps
        }?.also {ps->
            return ProductStock(
                    ps.productID,
                    ps.productName,
                    ps.qty
            )
        }

        throw Errors.ProductStockNotFound
    }

    fun has(qty: Int): Boolean {
        productStock?.let {
            val currentStockQTY = it.qty
            if (currentStockQTY > qty) {
                return true
            }

            return false
        }

        throw Errors.ProductStockNotFound
    }
}