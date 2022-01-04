package com.hasnaoui.favdish.model.databes

import androidx.room.*
import com.hasnaoui.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavoriteDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishList(): Flow<List<FavDish>>

    @Update
    suspend fun updateFavoriteDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favorite_dish = 1 ORDER BY ID")
    fun getFavDishList(): Flow<List<FavDish>>

    @Delete
    suspend fun deleteFavoriteDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType ORDER BY ID")
    fun getFilteredDishList(filterType:String): Flow<List<FavDish>>
}