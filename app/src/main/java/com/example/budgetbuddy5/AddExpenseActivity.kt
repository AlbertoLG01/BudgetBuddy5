package com.example.budgetbuddy5

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbuddy5.dataClasses.ExpenseCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate.now

class AddExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expense)

        //Spinner
        val categories = ExpenseCategory.getAllCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        categorySpinner.adapter = adapter


        //Boton de guardado
        val saveButton = findViewById<FloatingActionButton>(R.id.saveButton);

        saveButton.setOnClickListener {
            val title = findViewById<EditText>(R.id.titleEditText).text.toString()
            val amount = findViewById<EditText>(R.id.amountEditText).text.toString()
            val category = categorySpinner.selectedItem.toString()
            val note = findViewById<EditText>(R.id.noteEditText).text.toString().orEmpty()
            val date = now().toString()

            if (title.isNotEmpty() && amount.isNotEmpty()) { //Podria comprobar tambien la categoria pero el propio diseño del spinner hace que no pueda estar vacío
                val intent = Intent(this, TabbedActivity::class.java)
                intent.putExtra("expenseTitle", title)
                intent.putExtra("expenseAmount", amount)
                intent.putExtra("expenseCategory", category)
                intent.putExtra("expenseNote", note)
                intent.putExtra("expenseDate", date)

                setResult(RESULT_OK, intent)
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish() // Finaliza la actividad actual
            } else {
                Toast.makeText(this, "Por favor, complete el título y la cantidad", Toast.LENGTH_SHORT).show()
            }
        }

    }
}