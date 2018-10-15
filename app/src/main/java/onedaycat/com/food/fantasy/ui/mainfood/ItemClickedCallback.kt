package onedaycat.com.food.fantasy.mainfood

import onedaycat.com.food.fantasy.ui.mainfood.FoodModel

interface ItemClickedCallback {
   fun onClicked(foodModel: FoodModel)
}