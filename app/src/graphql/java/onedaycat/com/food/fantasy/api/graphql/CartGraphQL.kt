package onedaycat.com.food.fantasy.api.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import onedaycat.com.food.fantasy.AddToCartMutation
import onedaycat.com.food.fantasy.GetCartQuery
import onedaycat.com.food.fantasy.network.ApolloHelper
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.contract.repository.CartRepo
import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY
import onedaycat.com.foodfantasyservicelib.error.Errors
import kotlin.coroutines.experimental.suspendCoroutine

class CartGraphQL(
        private val oauthAdapter: OauthAdapter
): CartRepo {
    override suspend fun upsert(productQTY: ProductQTY) {
        val accessToken = oauthAdapter.validateToken()

        suspendCoroutine<String> {cont->
            val apolloCallBack = object : ApolloCall.Callback<AddToCartMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    cont.resumeWithException(e)
                    throw e
                }

                override fun onResponse(response: Response<AddToCartMutation.Data>) {
                    response.data()?.let {body->
                        cont.resume(body.addToCart() ?: "")
                    }
                }
            }

            ApolloHelper.setup(accessToken.token).mutate(AddToCartMutation
                    .builder()
                    .productID(productQTY.productId)
                    .qty(productQTY.qty)
                    .build()).enqueue(apolloCallBack)
        }
    }

    override suspend fun getAll(): Cart {
        val accessToken = oauthAdapter.validateToken()

        return suspendCoroutine {cont->
            val apolloCallBack = object : ApolloCall.Callback<GetCartQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    cont.resumeWithException(e)
                }

                override fun onResponse(response: Response<GetCartQuery.Data>) {
                    response.data()?.let { body->
                        val products = body.cart?.products()

                        val listProductQTY = arrayListOf<ProductQTY>()
                        products?.let {list->
                            for (product in list) {
                                ProductQTY().apply {
                                    this.productId = product.product()?.id() ?: ""
                                    this.productName = product.product()?.name() ?: ""
                                    this.price = product.product()?.price() ?: 0
                                    this.qty = product.qty() ?: 0
                                }.also {
                                    listProductQTY.add(it)
                                }
                            }

                            listProductQTY
                        }?.also {listPQTY->
                            Cart().apply {
                                this.userId = accessToken.token
                                this.products = listPQTY
                            }.run {
                                cont.resume(this)
                            }
                        }
                    }
                }
            }

            ApolloHelper.setup(accessToken.token).query(GetCartQuery
                    .builder()
                    .build()).enqueue(apolloCallBack)
        }
    }

    override suspend fun delete(userId: String) {

    }
}