package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.input.GetProductStocksInput
import onedaycat.com.foodfantasyservicelib.input.SubProductStockInput
import onedaycat.com.foodfantasyservicelib.validate.StockValidate

class StockService(
        private val stockRepo: StockRepo,
        private val stockValidate: StockValidate) {

    private var productStock:ProductStock? = null

    fun addProductStock(input: AddProductStockInput): ProductStock? {
        try {
            stockValidate.inputPStock(input)

            productStock = stockRepo.get(input.productID)
            productStock?.deposit(input.qty)

        }catch (e: NotFoundException) {
            productStock = ProductStock(input.productID, input.productName, 0)
        }

        stockRepo.upsert(productStock)

        return productStock
    }

    fun subProductStock(input: SubProductStockInput): ProductStock? {
        stockValidate.inputSubStock(input)

        productStock= stockRepo.get(input.productID)

        val pStock = productStock?.withDraw(input.qty)

        stockRepo.upsert(pStock)

        return pStock
    }

    fun getProductStock(input: GetProductStocksInput): ArrayList<ProductStock?> {
        if (input.productIds.size == 0) {
            throw Errors.ProductStockNotFound
        }

        val listProductStock = stockRepo.getByIDs(input.productIds)

        return listProductStock as ArrayList<ProductStock?>
    }
}