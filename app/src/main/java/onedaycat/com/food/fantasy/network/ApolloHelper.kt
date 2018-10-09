package onedaycat.com.food.fantasy.network

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class ApolloHelper {
    companion object {
        private const val BASE_URL = "https://lr2qt7bxwvgazndthz5lpkcf3q.appsync-api.ap-southeast-1.amazonaws.com"
        val setup: (token: String) -> ApolloClient = fun(token): ApolloClient {
            val okHttp = OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val builder = original.newBuilder().method(original.method(),
                                original.body())
                        builder.addHeader(
                                "Authorization",
                                "Bearer $token")
                        chain.proceed(builder.build())
                    }
                    .build()
            return ApolloClient.builder()
                    .serverUrl(BASE_URL)
                    .okHttpClient(okHttp)
                    .build()
        }
    }
}