package com.hasnaoui.favdish.model.databes

import androidx.annotation.WorkerThread
import com.hasnaoui.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao:FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish:FavDish){
        favDishDao.insertFavoriteDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishList()
}