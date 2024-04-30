package com.example.budgetbuddy5

import java.io.Serializable
import java.time.LocalDate

data class Income(
    val title: String,
    val amount: String, //Debería ser numérico pero eso lo controlo con el editText
    val category: String,
    val note: String,
    val date: LocalDate
) : Serializable

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
