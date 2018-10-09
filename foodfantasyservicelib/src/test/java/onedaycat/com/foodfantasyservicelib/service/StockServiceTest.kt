package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.contract.repository.StockRepo
import onedaycat.com.foodfantasyservicelib.error.*
import onedaycat.com.foodfantasyservicelib.input.AddProductStockInput
import onedaycat.com.foodfantasyservicelib.input.GetProductStocksInput
import onedaycat.com.foodfantasyservicelib.input.SubProductStockInput
import onedaycat.com.foodfantasyservicelib.validate.StockValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class StockServiceTest {
    @Mock
    private lateinit var stockService: StockService
    private lateinit var stockRepo: StockRepo
    private lateinit var stockValidate: StockValidate

    @Mock
    private lateinit var inputAddStock: AddProductStockInput
    private lateinit var inputSubStock: SubProductStockInput
    private lateinit var inputGetProductString: GetProductStocksInput
    private lateinit var expPStock: ProductStock

    private var stock: ProductStock? = null
    @Before
    fun setup() {
        stockRepo = mock(StockRepo::class.java)
        stockValidate = mock(StockValidate::class.java)
        stockService = StockService(stockRepo, stockValidate)

        inputAddStock = AddProductStockInput(
                "111",
                "Apple",
                10
        )

        inputSubStock = SubProductStockInput(
                "111",
                5
        )

        stock = ProductStock(
                "111",
                "Apple",
                0
        )

        expPStock = stock?.newProductStock("111", "Apple",20)!!
    }

    @Test
    fun `Add product stock success`() {
        val psGet = expPStock.newProductStock("111", "Apple",10)

        val expStock = ProductStock(
                "111",
                "Apple",
                30
        )

        doNothing().`when`(stockValidate).inputPStock(inputAddStock)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(psGet)
        doNothing().`when`(stockRepo).upsert(expPStock)

        val pstock = stockService.addProductStock(inputAddStock)

        Assert.assertEquals(expStock, pstock)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
        verify(stockRepo).upsert(pstock)
    }

    @Test(expected = InvalidInputException::class)
    fun `Add product stock but validate failed`() {
        `when`(stockValidate.inputPStock(inputAddStock)).thenThrow(Errors.InvalidInputProductStock)

        stockService.addProductStock(inputAddStock)
    }

    @Test
    fun `Add product stock but not found product stock`() {
        doNothing().`when`(stockValidate).inputPStock(inputAddStock)
        `when`(stockRepo.get(inputAddStock.productID)).thenThrow(Errors.ProductStockNotFound)

        val pstock = stockService.addProductStock(inputAddStock)

        Assert.assertEquals(stock, pstock)

        verify(stockValidate).inputPStock(inputAddStock)
        verify(stockRepo).get(inputAddStock.productID)
    }

    @Test(expected = InternalError::class)
    fun `Add product stock but save or update failed`() {
        val psGet = ProductStock("111", "Apple",10)

        doNothing().`when`(stockValidate).inputPStock(inputAddStock)
        `when`(stockRepo.get(inputAddStock.productID)).thenReturn(psGet)
        `when`(stockRepo.upsert(psGet)).thenThrow(Errors.UnableSaveProductStock)

        stockService.addProductStock(inputAddStock)
    }

    @Test
    fun `Sub product stock success`() {
        val psGet = ProductStock("111", "Apple",10)

        val expPStock = ProductStock(
                "111",
                "Apple",
                5
        )

        doNothing().`when`(stockValidate).inputSubStock(inputSubStock)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(psGet)
        doNothing().`when`(stockRepo).upsert(expPStock)

        val pstock = stockService.subProductStock(inputSubStock)

        Assert.assertEquals(expPStock, pstock)

        verify(stockValidate).inputSubStock(inputSubStock)
        verify(stockRepo).get(inputSubStock.productID)
        verify(stockRepo).upsert(expPStock)
    }

    @Test(expected = InvalidInputException::class)
    fun `Sub product stock but validate failed`() {
        `when`(stockValidate.inputSubStock(inputSubStock)).thenThrow(Errors.InvalidInputProductStock)

        stockService.subProductStock(inputSubStock)
    }

    @Test(expected = NotFoundException::class)
    fun `Sub product stock but get stock failed`() {
        doNothing().`when`(stockValidate).inputSubStock(inputSubStock)
        `when`(stockRepo.get(inputSubStock.productID)).thenThrow(Errors.ProductStockNotFound)

        stockService.subProductStock(inputSubStock)
    }

    @Test(expected = InternalError::class)
    fun `Sub product stock but save or update failed`() {
        val psGet = expPStock.newProductStock("111", "Apple", 10)
        doNothing().`when`(stockValidate).inputSubStock(inputSubStock)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(psGet)
        `when`(stockRepo.upsert(psGet)).thenThrow(Errors.UnableSaveProductStock)

        stockService.subProductStock(inputSubStock)
    }

    @Test(expected = BadRequestException::class)
    fun `Sub product stock failed`() {
        val psGet = expPStock.newProductStock("111", "Apple",2)

        doNothing().`when`(stockValidate).inputSubStock(inputSubStock)
        `when`(stockRepo.get(inputSubStock.productID)).thenReturn(psGet)
        doNothing().`when`(stockRepo).upsert(expPStock)

        stockService.subProductStock(inputSubStock)
    }
}