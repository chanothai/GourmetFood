package onedaycat.com.foodfantasyservicelib.service

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.input.CreateProductInput
import onedaycat.com.foodfantasyservicelib.input.GetProductInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveProductInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate

class ProductService(
        private val productRepo: ProductRepo,
        private val productValidate: ProductValidate) {

    fun createProduct(input: CreateProductInput): Product? {
        productValidate.inputProduct(input)
        val now = Clock.NowUTC()
        val product = Product(
                IdGen.NewId(),
                input.name,
                input.price,
                input.desc,
                input.image,
                now,
                now
        )

        productRepo.create(product)

        return product
    }

    fun removeProduct(input: RemoveProductInput) {
        productValidate.inputId(input.id)

        productRepo.remove(input.id)
    }

    suspend fun getProduct(input: GetProductInput): Product? {
        productValidate.inputId(input.productId)

        return productRepo.get(input.productId)
    }

    suspend fun getProducts(input: GetProductsInput): ProductPaging {
        productValidate.inputLimitPaging(input)

        return productRepo.getAllWithPaging(input.limit)
    }
}