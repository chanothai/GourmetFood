package onedaycat.com.foodfantasyservicelib.contract.repository

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.regions.Regions
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.UserCognito
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.cognito.CognitoFoodFantasyServiceLib
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class ProductRepoTest {
    private lateinit var productRepo:ProductRepo
    private lateinit var expProduct: Product

    @Before
    fun setup() {
        CognitoFoodFantasyServiceLib.cognitoUserPool = CognitoUserPool(
                InstrumentationRegistry.getContext(),
                "ap-southeast-1_M7jwwdfSy",
                "6qualkk2qcjqokgsujkgm5c2sq",
                null,
                Regions.AP_SOUTHEAST_1)

        productRepo = ProductFireStore(OauthCognito())

        val id = IdGen.NewId()
        val now = Clock.NowUTC()

        expProduct = Product(
                "1114",
                "Banana",
                100,
                "Banana from Thailand",
                "banana.png",
                now,
                now
        )

        IdGen.setFreezeID(id)
        Clock.setFreezeTimes(now)
    }

    @Test
    fun createProduct() {
        productRepo.create(expProduct)
    }

    @Test
    fun removeProductSuccess() {
        val id = "1114"
        productRepo.remove(id)
    }

    @Test(expected = Exception::class)
    fun removeProductFailed() {
        val id = "12312312313"
        productRepo.remove(id)
    }

    @Test
    fun getProductSuccess() {
        val id = "1113"
        val product = productRepo.get(id)

        assertEquals(id, product!!.id)
    }

    @Test(expected = NotFoundException::class)
    fun getProductFailed() {
        val id = "1234232423"
        productRepo.get(id)
    }

    @Test
    fun getProductAllWithPagingSuccess() {
        val products = productRepo.getAllWithPaging(3)
        assertEquals(3, products!!.products.size)
    }
}