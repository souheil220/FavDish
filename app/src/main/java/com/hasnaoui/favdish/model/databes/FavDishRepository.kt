package com.hasnaoui.favdish.model.databes

import androidx.annotation.WorkerThread
import com.hasnaoui.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insertFavoriteDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish) {
        favDishDao.updateFavoriteDishDetails(favDish)
    }

    val favDishesList: Flow<List<FavDish>> = favDishDao.getFavDishList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish) {
        favDishDao.deleteFavoriteDishDetails(favDish)
    }

    fun filteredDishesList(filterType: String): Flow<List<FavDish>> =
        favDishDao.getFilteredDishList(filterType)


}