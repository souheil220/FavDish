package com.hasnaoui.favdish.model.databes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hasnaoui.favdish.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {
    @Insert
    suspend fun insertFavoriteDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishList(): Flow<List<FavDish>>
}