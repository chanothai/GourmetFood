package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.ProductStockWithPrice
import onedaycat.com.foodfantasyservicelib.error.Errors

interface StockRepo {
    fun upsert(product: ProductStock?)
    fun get(productId: String): ProductStock?
    fun getByIDs(productIDs: MutableList<String>): MutableList<ProductStock?>
    fun getWithPrice(productId: String): ProductStockWithPrice
    fun getAllWithPrice(pQTYs: MutableList<ProductQTY>): MutableList<ProductStockWithPrice>
}

class StockFireStore(
        private val oauth: OauthAdapter): StockRepo {
    private val colStock = "Stocks"
    private val colProduct = "Products"
    private val db = FirebaseFirestore.getInstance()
    override fun upsert(product: ProductStock?) {
        try {
            oauth.validateToken()

            val docRef = db.collection(colStock).document(product!!.productID!!)
            Tasks.await(docRef.set(product))
        }catch (e:FirebaseFirestoreException) {
            throw UnknownError(e.toString())
        }
    }

    override fun get(productId: String): ProductStock? {
        try {
            oauth.validateToken()

            val docRef = db.collection(colStock).document(productId)
            return Tasks.await(docRef.get()).toObject(ProductStock::class.java) ?: throw Errors.ProductStockNotFound
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getByIDs(productIDs: MutableList<String>): MutableList<ProductStock?> {
        try {
            oauth.validateToken()

            val pStocks = mutableListOf<ProductStock?>()

            for (id in productIDs) {
                val docRef = db.collection(colStock).document(id)
                val pStock = Tasks.await(docRef.get()).toObject(ProductStock::class.java)

                pStocks.add(pStock!!)
            }

            return pStocks
        }catch (e:Exception) {
            throw Errors.ProductStockNotFound

        }catch (e:FirebaseFirestoreException) {
            Errors.UnKnownError
        }

        return mutableListOf()
    }

    override fun getWithPrice(productId: String): ProductStockWithPrice {
        try {
            oauth.validateToken()

            val docRefStock = db.collection(colStock).document(productId)
            val pStock = Tasks.await(docRefStock.get()).toObject(ProductStock::class.java)
                    ?: throw Errors.ProductStockNotFound

            val docRefProduct = db.collection(colProduct).document(productId)
            val product = Tasks.await(docRefProduct.get()).toObject(Product::class.java) ?: throw Errors.ProductNotFound

            return ProductStockWithPrice(
                    pStock,
                    product.price
            )

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getAllWithPrice(pQTYs: MutableList<ProductQTY>): MutableList<ProductStockWithPrice> {
        try {
            oauth.validateToken()

            val docRef = db.collection(colStock)
            val pstocks = Tasks.await(docRef.get()).toObjects(ProductStock::class.java)

            val listPStockWithPrice = mutableListOf<ProductStockWithPrice>()

            for ((i, pQTY) in pQTYs.withIndex()) {
                val index = pstocks.indexOfFirst {
                    it.productID == pQTY.productId
                }

                listPStockWithPrice.add(ProductStockWithPrice().apply {
                    this.price = pQTYs[i].price
                    this.productStock = pstocks[index]
                })
            }

            return listPStockWithPrice
        }catch (e: Exception) {
            throw Errors.UnableGetProductStock
        }catch (e: FirebaseFirestoreException) {
            throw UnknownError(e.toString())
        }
    }
}