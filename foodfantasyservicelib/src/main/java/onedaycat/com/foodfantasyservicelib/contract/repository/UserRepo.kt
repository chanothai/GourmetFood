package onedaycat.com.foodfantasyservicelib.contract.repository

import com.google.firebase.firestore.FirebaseFirestore
import onedaycat.com.foodfantasyservicelib.entity.User
import onedaycat.com.foodfantasyservicelib.error.Errors
import android.util.Log
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.error.NotFoundException


interface UserRepo {
    fun create(user: User)
    fun getByEmail(email: String): User?
    fun get(userId: String): User?
}

class UserFireStore(
        private val oauth: OauthAdapter
) : UserRepo {
    private var user: User? = null
    private val collectionUser: String = "users"
    private val db = FirebaseFirestore.getInstance()

    override fun create(user: User) {
        try {
            oauth.validateToken()

            val docRef = db.collection(collectionUser).document(user.email)
            Tasks.await(docRef.set(user))
        }catch (e: Exception) {
            throw Errors.UnableCreateUser
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun getByEmail(email: String): User? {
        try {
            oauth.validateToken()

            val docRef = db.collection(collectionUser).document(email)
            val document = Tasks.await(docRef.get())

            user = document.toObject(User::class.java)
            user!!.password = ""

            return user
        }catch (e: Exception) {
            throw Errors.UserNotFound
        }catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }

    override fun get(userId: String): User? {
        try {
            oauth.validateToken()

            val docRef = db.collection(collectionUser)
            val query: Query = docRef.whereEqualTo("id", userId)
            val document = Tasks.await(query.get())

            user = document.toObjects(User::class.java)[0]

            user!!.password = ""
            return user

        }catch (e: Exception) {
            throw Errors.UserNotFound
        }
        catch (e: FirebaseFirestoreException) {
            throw Errors.UnKnownError
        }
    }
}

