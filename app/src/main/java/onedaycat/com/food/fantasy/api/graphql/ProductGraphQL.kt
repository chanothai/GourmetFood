package onedaycat.com.food.fantasy.api.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import onedaycat.com.food.fantasy.CreateProductMutation
import onedaycat.com.food.fantasy.GetProductsQuery
import onedaycat.com.food.fantasy.network.ApolloHelper
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors
import kotlin.coroutines.experimental.suspendCoroutine

class ProductGraphQL(
        private val oauthAdapter: OauthAdapter
) : ProductRepo {
    override fun create(product: Product) {
        val accessToken = oauthAdapter.validateToken()
        ApolloHelper.setup(accessToken.token).mutate(CreateProductMutation
                .builder()
                .name(product.name)
                .desc(product.desc)
                .price(product.price)
                .image(product.image)
                .build()).enqueue(object : ApolloCall.Callback<CreateProductMutation.Data>() {

            override fun onFailure(e: ApolloException) {
                throw e
            }

            override fun onResponse(response: Response<CreateProductMutation.Data>) {
                response.data()?.let { body ->
                    body.createProduct()?.let { data ->
                        data
                    }
                }
            }
        })
    }

    override fun remove(id: String) {

    }

    override suspend fun getAllWithPaging(limit: Int): ProductPaging {
        val accessToken = oauthAdapter.validateToken()

        return suspendCoroutine { cont->
            val apolloCallback = object : ApolloCall.Callback<GetProductsQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    cont.resumeWithException(e)
                    throw e
                }

                override fun onResponse(response: Response<GetProductsQuery.Data>) {
                    response.data()?.let { body ->
                        body.products?.let { data ->
                            val productPaging = ProductPaging(mapProducts(data.products()))
                            cont.resume(productPaging)
                        }
                    }
                }
            }

            ApolloHelper.setup(accessToken.token).query(GetProductsQuery
                    .builder()
                    .build()).enqueue(apolloCallback)
        }
    }

    private fun mapProducts(items: MutableList<GetProductsQuery.Product>): MutableList<Product> {
        val products = mutableListOf<Product>()

        if (items.size == 0) {
            throw Errors.ProductNotFound
        }

        for (item in items) {
            val product = Product().apply {

                this.name = item.name() ?: throw Errors.ProductNotFound
                this.desc = item.desc() ?: throw Errors.ProductNotFound
                this.price = item.price() ?: throw Errors.ProductNotFound
                this.image = item.image() ?: throw Errors.ProductNotFound
            }

            products.add(product)
        }

        return products
    }

    override fun get(id: String): Product {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}