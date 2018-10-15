package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Product

data class ProductPaging(
        var products: MutableList<Product>
)

interface ProductRepo {
    fun create(product: Product)
    fun remove(id: String)
    suspend fun getAllWithPaging(limit: Int): ProductPaging
    suspend fun get(id: String): Product
}