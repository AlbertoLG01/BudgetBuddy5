package com.example.budgetbuddy5.dataClasses

import java.time.LocalDate
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Incomes")
data class Income(
    @PrimaryKey val title: String,
    val amount: String, //Debería ser numérico pero eso lo controlo con el editText
    val category: String,
    val note: String,
    val date: LocalDate
)

enum class IncomeCategory {
    SALARIO,
    TRABAJO,
    INVERSIONES,
    REGALOS,
    BIZUM,
    OTRO;

    companion object {
        fun getAllCategories(): List<String> {
            return values().map { it.name }
        }
    }
}

