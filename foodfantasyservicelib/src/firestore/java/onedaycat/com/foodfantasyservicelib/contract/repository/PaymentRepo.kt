package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.entity.ProductStock
import onedaycat.com.foodfantasyservicelib.entity.Transaction
import onedaycat.com.foodfantasyservicelib.error.Errors

interface PaymentRepo {
    fun savePayment(order: Order, transaction: Transaction, productStocks: MutableList<ProductStock?>)
}

class PaymentFireStore(
        private val oauthAdapter: OauthAdapter
): PaymentRepo {
    private val colPayment = "Payments"
    private val db = FirebaseFirestore.getInstance()

    override fun savePayment(order: Order, transaction: Transaction, productStocks: MutableList<ProductStock?>) {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colPayment).document()

            val docPayment = HashMap<String, Any>()
            docPayment["order"] = mapOrder(order)
            docPayment["transaction"] = mapTransaction(transaction)
            docPayment["product_stocks"] = mapPStocks(productStocks)

            Tasks.await(docRef.set(docPayment))

        }catch (e: Exception) {
            throw Errors.UnableSavePayment
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    private fun mapOrder(order: Order): HashMap<String, Any> {
        val arrPQTY = mutableListOf<HashMap<String, Any>>()
        for (product in order.products) {
            val docPQTY = HashMap<String, Any>()
            docPQTY["productId"] = product.productId
            docPQTY["price"] = product.price
            docPQTY["qty"] = product.qty

            arrPQTY.add(docPQTY)
        }

        val docOrder = HashMap<String, Any>()
        docOrder["id"] = order.id!!
        docOrder["userId"] = order.userId!!
        docOrder["products"] = arrPQTY
        docOrder["totalPrice"] = order.totalPrice
        docOrder["createDate"] = order.createDate!!
        docOrder["status"] = order.status.toString()

        return docOrder
    }

    private fun mapTransaction(transaction: Transaction): HashMap<String, Any> {
        val docTx = HashMap<String, Any>()
        docTx["id"] = transaction.id
        docTx["orderId"] = transaction.orderID
        docTx["status"] = transaction.status.toString()
        docTx["amount"] = transaction.amount
        docTx["createAt"] = transaction.createAt

        return docTx
    }

    private fun mapPStocks(productStocks: MutableList<ProductStock?>): MutableList<HashMap<String, Any>> {
        val arrPStock = mutableListOf<HashMap<String, Any>>()
        for (product in productStocks) {
            val docPStock = HashMap<String, Any>()
            docPStock["productId"] = product!!.productID!!
            docPStock["qty"] = product.qty

            arrPStock.add(docPStock)
        }

        return arrPStock
    }
}