package onedaycat.com.foodfantasyservicelib.contract.repository

import onedaycat.com.foodfantasyservicelib.entity.Cart
import onedaycat.com.foodfantasyservicelib.entity.ProductQTY

interface CartRepo {
    suspend fun upsert(productQTY: ProductQTY)
    suspend fun getAll(): Cart
    suspend fun delete(userId: String)
}