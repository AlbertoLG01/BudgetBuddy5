package com.example.budgetbuddy5.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.budgetbuddy5.dataClasses.Expense
import com.example.budgetbuddy5.dataClasses.Income
import kotlinx.coroutines.flow.Flow
import androidx.room.Database
import androidx.room.Delete
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Dao
interface IncomeDao {
    @Query("SELECT * FROM Incomes")
    suspend fun getAllIncomes(): MutableList<Income>

    @Insert
    suspend fun insertIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM Expenses")
    suspend fun getAllExpenses(): MutableList<Expense>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}

@Database(entities = [Income::class, Expense::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
}

