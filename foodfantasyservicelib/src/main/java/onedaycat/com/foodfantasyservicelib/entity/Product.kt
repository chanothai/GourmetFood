package onedaycat.com.foodfantasyservicelib.entity

data class Product(
        var id: String = "",
        var name:String = "",
        var price: Int = 0,
        var desc: String = "",
        var image: String = "",
        var createDate: String = "",
        var updateDate: String = "")

//ProductQTY Product and qty
data class ProductQTY(
        var productId: String = "",
        var productName: String = "",
        var price: Int = 0,
        var qty: Int = 0)

fun newProductQTY(productId: String, productName: String, price: Int, qty: Int): ProductQTY {
    return ProductQTY(
            productId = productId,
            productName = productName,
            price = price,
            qty = qty)
}




