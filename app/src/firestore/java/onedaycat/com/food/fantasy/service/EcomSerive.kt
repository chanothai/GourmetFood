package onedaycat.com.food.fantasy.service

import onedaycat.com.food.fantasy.oauth.OauthCognito
import onedaycat.com.foodfantasyservicelib.contract.creditcard_payment.CreditCardMemoPayment
import onedaycat.com.foodfantasyservicelib.contract.repository.*
import onedaycat.com.foodfantasyservicelib.service.*
import onedaycat.com.foodfantasyservicelib.validate.*

class EcomService {
    var paymentService = PaymentService(
            OrderFireStore(OauthCognito()),
            CreditCardMemoPayment(),
            StockFireStore(OauthCognito()),
            CartFireStore(OauthCognito()),
            PaymentFireStore(OauthCognito()),
            PaymentMemoValidate()
    )

    var orderService = OrderService(
            OrderFireStore(OauthCognito()),
            OrderMemoValidate()
    )

    var cartService = CartService(
            StockFireStore(OauthCognito()),
            CartFireStore(OauthCognito()),
            CartValidate()
    )


    var stockService = StockService(StockFireStore(OauthCognito()), StockMemoValidate())

    var productService = ProductService(ProductFireStore(OauthCognito()), ProductMemoValidate())

    var userService = UserService(UserFireStore(OauthCognito()), UserMemoryValidate())
}