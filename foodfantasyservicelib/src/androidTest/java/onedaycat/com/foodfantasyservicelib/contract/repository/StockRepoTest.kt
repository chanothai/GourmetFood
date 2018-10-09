package onedaycat.com.foodfantasyservicelib.contract.repository

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.ProductStockWithPrice
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockRepoTest {

    private lateinit var expPStockPrice: ProductStockWithPrice
    private lateinit var expPStock: ProductStock
    private lateinit var stockRepo: StockRepo

    @Before
    fun setup() {

        CognitoFoodFantasyServiceLib.cognitoUserPool = CognitoUserPool(
                InstrumentationRegistry.getContext(),
                "ap-southeast-1_M7jwwdfSy",
                "6qualkk2qcjqokgsujkgm5c2sq",
                null,
                Regions.AP_SOUTHEAST_1)

        stockRepo = StockFireStore(OauthCognito())

        expPStockPrice = ProductStockWithPrice(
                ProductStock(
                        "1111",
                        "Apple",
                        300
                ),
                100
        )

        expPStock = ProductStock(
                "1111",
                "Apple",
                300
        )
    }

    @Test
    fun createStockProduct() {
        stockRepo.upsert(expPStock)
    }

    @Test
    fun getStockProduct() {
        val id = "1111"
        val pstock = stockRepo.get(id)
        assertEquals(id, pstock?.productID)
    }

    @Test(expected = NotFoundException::class)
    fun getStockProductFailed() {
        val id = "2222"
        stockRepo.get(id)
    }

    @Test
    fun getStockProductWithPrice() {
        val id = "1111"
        val pstock = stockRepo.getWithPrice(id)
        assertEquals(id, pstock.productStock?.productID)
    }

    @Test(expected = NotFoundException::class)
    fun getStockProductWIthPriceFailed(){
        val id = "22222"
        stockRepo.getWithPrice(id)
    }

    @Test
    fun getByIds() {
        val ids = mutableListOf("1111", "1112")
        val pStocks = stockRepo.getByIDs(ids)

        assertNotNull(pStocks)
    }

    @Test(expected = NotFoundException::class)
    fun getByIdsFailed() {
        val ids = mutableListOf("1111", "2222")
        stockRepo.getByIDs(ids)
    }
}