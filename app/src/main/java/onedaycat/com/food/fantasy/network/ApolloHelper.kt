package onedaycat.com.food.fantasy.network

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ApolloHelper {
    companion object {
        private const val BASE_URL = "https://lr2qt7bxwvgazndthz5lpkcf3q.appsync-api.ap-southeast-1.amazonaws.com/graphql"
        val setup: (token: String) -> ApolloClient = fun(token): ApolloClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttp = OkHttpClient
                    .Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor { chain ->

                        val original = chain.request()
                        val builder = original.newBuilder()
                        builder.addHeader(
                                "Authorization",
                                token)
                        chain.proceed(builder.build())
                    }.build()
            return ApolloClient.builder()
                    .serverUrl(BASE_URL)
                    .okHttpClient(okHttp)
                    .build()
        }
    }
}