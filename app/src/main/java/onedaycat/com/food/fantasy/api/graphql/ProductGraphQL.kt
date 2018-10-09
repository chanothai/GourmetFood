package onedaycat.com.food.fantasy.api.graphql

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import onedaycat.com.food.fantasy.CreateProductMutation
import onedaycat.com.food.fantasy.GetProductsQuery
import onedaycat.com.food.fantasy.network.ApolloHelper
import onedaycat.com.food.fantasy.oauth.OauthAdapter
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductPaging
import onedaycat.com.foodfantasyservicelib.contract.repository.ProductRepo
import onedaycat.com.foodfantasyservicelib.entity.Product
import onedaycat.com.foodfantasyservicelib.error.Errors

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
                response.data()?.let {body->
                    body.createProduct()?.let {data->
                        data
                    }
                }
            }
        })
    }

    override fun remove(id: String) {

    }

    override fun getAllWithPaging(limit: Int): ProductPaging {
        val accessToken = oauthAdapter.validateToken()

        var productPaging: ProductPaging? = null

        ApolloHelper.setup(accessToken.token).query(GetProductsQuery
                .builder()
                .build()).enqueue(object: ApolloCall.Callback<GetProductsQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                throw e
            }

            override fun onResponse(response: Response<GetProductsQuery.Data>) {

                response.data()?.let {body->
                    body.products?.products()?.let {data->
                        productPaging = ProductPaging(mapProducts(data))
                    }
                }
            }
        })

        productPaging?.let {
            return it
        }

        throw Errors.ProductNotFound
    }

    private fun mapProducts(items: MutableList<GetProductsQuery.Product>): MutableList<Product> {
        val products = mutableListOf<Product>()

        if (items.size == 0) {
            throw Errors.ProductNotFound
        }

        for (item in items){
            val product = Product().apply {
                item.name()?.let {name->
                    this.name = name
                }

                item.desc()?.let {desc->
                    this.desc = desc
                }

                item.price()?.let {price->
                    this.price = price
                }

                item.image()?.let {img->
                    this.image = img
                }
            }

            products.add(product)
        }

        return products
    }

    override fun get(id: String): Product {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}