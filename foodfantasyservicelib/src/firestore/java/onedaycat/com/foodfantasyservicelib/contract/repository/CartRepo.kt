package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.error.Error
import onedaycat.com.foodfantasyservicelib.error.Errors
import java.util.*
import kotlin.collections.HashMap

interface CartRepo {
    fun upsert(cart: Cart)
    fun getByUserID(userId: String): Cart
    fun delete(userId: String)
}

class CartFireStore(
        private val oauthAdapter: OauthAdapter
): CartRepo {
    private val colCart: String = "Carts"
    private val db = FirebaseFirestore.getInstance()

    override fun upsert(cart: Cart) {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colCart).document(cart.userId ?: "")

            Tasks.await(docRef.set(cart))
        }catch (e:FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getByUserID(userId: String): Cart {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colCart).document(userId)
            val document = Tasks.await(docRef.get())
            return document.toObject(Cart::class.java) ?: throw Errors.CartNotFound

        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun delete(userId: String) {
        try {
            oauthAdapter.validateToken()

            val docRef = db.collection(colCart).document(userId)

            val cart = Cart().apply {
                this.products = arrayListOf()
                this.userId = userId
            }

            Tasks.await(docRef.set(cart))
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }
}