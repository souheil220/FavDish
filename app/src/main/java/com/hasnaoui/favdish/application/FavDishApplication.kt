package com.hasnaoui.favdish.application

import android.app.Application
import com.hasnaoui.favdish.model.databes.FavDishRepository
import com.hasnaoui.favdish.model.databes.FavDishRoomDatabase

class FavDishApplication:Application() {
    private val database by lazy { FavDishRoomDatabase.getDatabase(this) }
    val repository by lazy { FavDishRepository(database.favDishDao()) }
}