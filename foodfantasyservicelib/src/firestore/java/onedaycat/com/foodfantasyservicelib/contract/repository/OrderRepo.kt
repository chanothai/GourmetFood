package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.entity.State
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

interface OrderRepo {
    fun upsert(order: Order)
    fun get(userId: String): Order
    fun getAll(userId: String): ArrayList<Order>
}

class OrderFireStore(
        private val oauthAdapter: OauthAdapter
): OrderRepo {
    private val colOrder: String = "Orders"
    private val db = FirebaseFirestore.getInstance()

    override fun upsert(order: Order) {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colOrder)

            val arrPQTY = mutableListOf<HashMap<String, Any>>()

            for (product in order.products) {
                val docPQTY = HashMap<String, Any>()
                docPQTY["productId"] = product.productId
                docPQTY["productName"] = product.productName
                docPQTY["price"] = product.price
                docPQTY["qty"] = product.qty

                arrPQTY.add(docPQTY)
            }

            val docData = HashMap<String, Any>()
            docData["id"] = order.id!!
            docData["userId"] = order.userId!!
            docData["products"] = arrPQTY
            docData["totalPrice"] = order.totalPrice
            docData["createDate"] = order.createDate!!
            docData["status"] = order.status.toString()


            Tasks.await(docRef.add(docData))
        }catch (e: Error) {
            throw e
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun get(userId: String): Order {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colOrder)
            val query: Query = docRef.whereEqualTo("userId", userId)

            val docOrder = Tasks.await(query.get()) ?: throw Errors.NotOrderOwner
            return docOrder.toObjects(Order::class.java)[0]

        }catch (e:Exception) {
            throw Errors.UnableGetOrder
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getAll(userId: String): ArrayList<Order> {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colOrder).orderBy("createDate", Query.Direction.DESCENDING)
            val query: Query = docRef.whereEqualTo("userId", userId)

            val docOrder = Tasks.await(query.get()) ?: throw Errors.UnableGetOrder

            return docOrder.toObjects(Order::class.java) as ArrayList<Order>
        }catch (e:Exception) {
            throw e
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }
}