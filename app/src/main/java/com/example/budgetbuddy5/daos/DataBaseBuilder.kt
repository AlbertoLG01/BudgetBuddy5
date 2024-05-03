package com.example.budgetbuddy5.daos

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    private var INSTANCE: AppDatabase? = null

    fun getAppDatabase(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "Base de Datos de BudgetBuddy"
                ).build()
            }
        }
        return INSTANCE!!
    }
}
