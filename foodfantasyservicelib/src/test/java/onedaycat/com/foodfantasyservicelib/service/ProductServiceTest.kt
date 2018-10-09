package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.error.InternalError
import onedaycat.com.foodfantasyservicelib.error.InvalidInputException
import onedaycat.com.foodfantasyservicelib.error.NotFoundException
import onedaycat.com.foodfantasyservicelib.input.CreateProductInput
import onedaycat.com.foodfantasyservicelib.input.GetProductInput
import onedaycat.com.foodfantasyservicelib.input.GetProductsInput
import onedaycat.com.foodfantasyservicelib.input.RemoveProductInput
import onedaycat.com.foodfantasyservicelib.util.clock.Clock
import onedaycat.com.foodfantasyservicelib.util.idgen.IdGen
import onedaycat.com.foodfantasyservicelib.validate.ProductValidate
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class ProductServiceTest {
    @Mock
    private lateinit var productService: ProductService
    private lateinit var productRepo: ProductRepo
    private lateinit var productValidate: ProductValidate
    private lateinit var productPaging: ProductPaging

    @Mock
    private lateinit var expProduct: Product

    @Mock
    private lateinit var input: CreateProductInput
    private lateinit var inputIncorrect: CreateProductInput
    private lateinit var getProductInput: GetProductInput

    @Before
    fun setup() {
        productRepo = mock(ProductRepo::class.java)
        productValidate = mock(ProductValidate::class.java)
        productService = ProductService(productRepo, productValidate)

        input = CreateProductInput(
                "Apple",
                "Apple from November",
                100000,
                "img.png"
        )

        getProductInput = GetProductInput(
                "xxxx"
        )

        inputIncorrect = CreateProductInput(
                "",
                "",
                -1,
                "")

        val now = Clock.NowUTC()
        expProduct = Product(
                "xxxx",
                name = input.name,
                price = input.price,
                desc = input.desc,
                image = input.image,
                createDate = now,
                updateDate = now
        )

        IdGen.setFreezeID(id = "xxxx")
        Clock.setFreezeTimes(now)
    }

    @Test
    fun `create product success`() {
        doNothing().`when`(productValidate).inputProduct(input)
        doNothing().`when`(productRepo).create(expProduct)

        val product = productService.createProduct(input)

        Assert.assertEquals(expProduct.id, product!!.id)

        verify(productValidate).inputProduct(input)
        verify(productRepo).create(expProduct)
    }

    @Test(expected = InternalError::class)
    fun `create product fail`() {
        doNothing().`when`(productValidate).inputProduct(input)
        `when`(productRepo.create(expProduct)).thenThrow(Errors.UnableCreateProduct)

        productService.createProduct(input)
    }

    @Test(expected = InvalidInputException::class)
    fun `create product then validate fail`() {
        `when`(productValidate.inputProduct(inputIncorrect)).thenThrow(Errors.InvalidInputProduct)

        productService.createProduct(inputIncorrect)
    }

    @Test
    fun `remove product success`() {
        val input = RemoveProductInput(
                id = "xxxx"
        )

        doNothing().`when`(productValidate).inputId(input.id)
        doNothing().`when`(productRepo).remove(input.id)

        productService.removeProduct(input)

        verify(productValidate).inputId(input.id)
        verify(productRepo).remove(input.id)
    }

    @Test(expected = InternalError::class)
    fun `remove product fail`() {
        val input = RemoveProductInput(
                id = "xxxx"
        )

        doNothing().`when`(productValidate).inputId(input.id)
        `when`(productRepo.remove(input.id)).thenThrow(Errors.UnableRemoveProduct)

        productService.removeProduct(input)
    }

    @Test(expected = InvalidInputException::class)
    fun `remove product but validate failed`() {
        val input = RemoveProductInput(
                id = "   "
        )

        `when`(productValidate.inputId(input.id)).thenThrow(Errors.InvalidInput)

        productService.removeProduct(input)
    }

    @Test
    fun `get product success`() {
        val id = getProductInput.productId
        doNothing().`when`(productValidate).inputId(id)
        `when`(productRepo.get(id)).thenReturn(expProduct)

        val product = productService.getProduct(getProductInput)

        Assert.assertEquals(expProduct, product)

        verify(productValidate).inputId(id)
        verify(productRepo).get(id)
    }

    @Test(expected = NotFoundException::class)
    fun `get product not found`() {
        val id = getProductInput.productId

        doNothing().`when`(productValidate).inputId(id)
        `when`(productRepo.get(id)).thenThrow(Errors.ProductNotFound)

        productService.getProduct(getProductInput)
    }

    @Test(expected = InvalidInputException::class)
    fun `get product then validate fail`() {
        val id = getProductInput.productId

        `when`(productValidate.inputId(id)).thenThrow(Errors.InvalidInput)

        productService.getProduct(getProductInput)
    }

    @Test
    fun `get product all success`() {
        val input = GetProductsInput(
                limit = 10
        )

        productPaging = ProductPaging(
                products = mutableListOf(
                        Product("id1", "name1", 100000, "desc1", "img1", "1", "2"),
                        Product("id2", "name2", 200000, "desc2", "img2", "3", "4"),
                        Product("id3", "name3", 300000, "desc3", "img3", "5", "6")
                )
        )

        val expProductPaging = productPaging

        doNothing().`when`(productValidate).inputLimitPaging(input)
        `when`(productRepo.getAllWithPaging(input.limit)).thenReturn(expProductPaging)

        val productPaging= productService.getProducts(input)

        Assert.assertEquals(expProductPaging, productPaging)

        verify(productValidate).inputLimitPaging(input)
        verify(productRepo).getAllWithPaging(input.limit)
    }

    @Test(expected = InternalError::class)
    fun `get product all failed`() {
        val input = GetProductsInput(
                limit = 10
        )

        doNothing().`when`(productValidate).inputLimitPaging(input)
        `when`(productRepo.getAllWithPaging(input.limit)).thenThrow(Errors.UnableGetProduct)

        productService.getProducts(input)
    }

    @Test(expected = InvalidInputException::class)
    fun `get product all but validate failed`() {
        val input = GetProductsInput(
                limit = -1
        )

        `when`(productValidate.inputLimitPaging(input)).thenThrow(Errors.InvalidInputLimitPaging)
        
        productService.getProducts(input)
    }
}