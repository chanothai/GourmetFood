package onedaycat.com.foodfantasyservicelib.service

import onedaycat.com.foodfantasyservicelib.entity.Order
import onedaycat.com.foodfantasyservicelib.contract.repository.OrderRepo
import onedaycat.com.foodfantasyservicelib.input.GetOrderInput
import onedaycat.com.foodfantasyservicelib.validate.OrderValidate

class OrderService(private val orderRepo: OrderRepo,
                   private val orderValidate: OrderValidate) {

    fun getOrder(input: GetOrderInput): Order? {
        orderValidate.inputGetOrder(input)

        return orderRepo.get(input.id)
    }

    fun getOrders(input: GetOrderInput): ArrayList<Order> {
        return orderRepo.getAll(input.id)
    }
}