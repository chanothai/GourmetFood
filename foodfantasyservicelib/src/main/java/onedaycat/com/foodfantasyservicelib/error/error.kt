package onedaycat.com.foodfantasyservicelib.error


class Errors {
    companion object {
        //Error View
        val UnableIntentActivity = InternalError(2222, "Unable intent because not found destination of class")
        val BundleNotFound = BadRequestException(2221, "Bundle Not Found")
        //Error interactor
        var UserNotFound = NotFoundException(1001, "User Not Found")
        var ProductNotFound = NotFoundException(1002, "Product Not Found")
        var CartNotFound = NotFoundException(1222, "Cart Not Found")
        var ProductStockNotFound = NotFoundException(111, "Product Stock Not Found")
        var InvalidInput = InvalidInputException(1002, "Input some field invalid")
        var InvalidInputProduct = InvalidInputException(1006, "Product price shouldn't be less than 0 and name shouldn't be empty or blank")
        var InvalidInputLimitPaging = InvalidInputException(1007, "Limit shouldn't be less than 1")
        var InvalidInputProductStock = InvalidInputException(222, "Product qty shouldn't be less than 0 and ID shouldn't be empty or blank")
        var EmailExist = BadRequestException(1003, "User email already exist")
        var ProductNotMatched = BadRequestException(1005, "Products didn't match")
        var ProductOutOfStock = BadRequestException(199, "Product out of stock")
        var UnableCreateUser = InternalError(1004, "Unable create user")
        var UnableCreateProduct = InternalError(1999, "Unable create product")
        var UnableGetUser = InternalError(1005, "Unable get user")
        var UnableSavePayment = InternalError(1323, "Unable save payment")
        var UnableChargeCreditCard = InternalError(2233, "Unable to charge credit card")
        var UnableGetProduct = InternalError(1010, "Unable get product")
        var UnableRemoveProduct = InternalError(1011, "Unable remove product")
        var UnableGetCart = InternalError(111, "Unable get cart")
        var UnableGetProductStock = InternalError(111, "Unable get product stock")
        var UnableSaveCart = InternalError(111,"Unable save cart")
        var UnableGetOrder = InternalError(233, "Unable get order")
        var UnableSaveProductStock = InternalError(222, "Unable save product stock")
        var EmptyProductInOrder = BadRequestException(123, "Order does not have product")
        var InvalidOrderTotalPrice = BadRequestException(123, "Order total price less than 1")
        var OrderAndTxNotMatched = BadRequestException(241, "Order id and Transaction id are not matched")
        var InvalidTxAmount = BadRequestException(1233, "Amount in transaction less than 1")
        var TxStatusNotCharged = BadRequestException(123, "Transaction is not chang")
        var OrderStatusNotPending = BadRequestException(123, "Cannot Paid: Order is not pending")
        var OrderStatusNotPaid = BadRequestException(324, "Cannot Refund: Order is not paid")
        var NotOrderOwner = BadRequestException(5436, "You are not the owner of order")
        var UnKnownError = onedaycat.com.foodfantasyservicelib.error.UnKnownError(123, "")

        var TokenNotFound = NotFoundException(9999, "Token Not Found")
        var TokenExpired = InternalError(8888, "Token expired")
    }
}

