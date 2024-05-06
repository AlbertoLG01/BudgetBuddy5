package com.example.budgetbuddy5

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.budgetbuddy5.dataClasses.Expense
import com.example.budgetbuddy5.dataClasses.Income
import com.example.budgetbuddy5.ui.main.SectionsPagerAdapter
import com.example.budgetbuddy5.databinding.ActivityTabbedBinding
import com.example.budgetbuddy5.daos.AppDatabase
import com.example.budgetbuddy5.daos.DatabaseBuilder
import com.example.budgetbuddy5.daos.ExpenseDao
import com.example.budgetbuddy5.daos.IncomeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TabbedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabbedBinding

    private var ingresosIniciados = false
    private var gastosIniciados = false
    private lateinit var db: AppDatabase
    private lateinit var incomeDao: IncomeDao
    private lateinit var expenseDao: ExpenseDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Base de datos
        db = DatabaseBuilder.getAppDatabase(this)
        //db = Room.databaseBuilder(this, AppDatabase::class.java, "Base de Datos de BudgetBuddy").build()
        incomeDao = db.incomeDao()
        expenseDao = db.expenseDao()

        binding = ActivityTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab


        // Agregar un listener para detectar cambios de página en el ViewPager y poder ocultar los botones
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            //Da error si no lo dejo
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // Verificar si la página seleccionada es la pestaña de estadísticas
                if (position == 2) {
                    // Ocultar el FloatingActionButton
                    fab.hide()
                } else {
                    // Mostrar el FloatingActionButton en las otras pestañas
                    fab.show()
                }
            }

            //Da error si no lo dejo
            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        //Listener del boton de añadir
        fab.setOnClickListener {
            val currentPage = viewPager.currentItem

            if (currentPage == 0) {
                // Si estamos en la página 0, abrir AddIncomeActivity
                startActivity(Intent(this, AddIncomeActivity::class.java))
            } else if (currentPage == 1) {
                // Si estamos en la página 1, abrir AddExpenseActivity
                startActivity(Intent(this, AddExpenseActivity::class.java))
            }
        }

    }


    suspend fun getIncomeList(): MutableList<Income> {
        return withContext(Dispatchers.IO) {
            incomeDao.getAllIncomes()
        }
    }

    suspend fun getExpenseList(): MutableList<Expense> {
        return withContext(Dispatchers.IO) {
            expenseDao.getAllExpenses()
        }
    }

    suspend fun addIncome(income: Income) {
        CoroutineScope(Dispatchers.IO).launch {
            incomeDao.insertIncome(income)
        }
    }

    suspend fun deleteIncome(income: Income) {
        CoroutineScope(Dispatchers.IO).launch {
            incomeDao.deleteIncome(income)
        }
    }

    suspend fun addExpense(expense: Expense) {
        CoroutineScope(Dispatchers.IO).launch {
            expenseDao.insertExpense(expense)
        }
    }

    suspend fun deleteExpense(expense: Expense) {
        CoroutineScope(Dispatchers.IO).launch {
            expenseDao.deleteExpense(expense)
        }
    }

    fun setIngresosIniciados(valor: Boolean){
        ingresosIniciados = valor
    }

    fun getIngresosIniciados(): Boolean{
        return ingresosIniciados
    }

    fun setGastosIniciados(valor: Boolean){
        gastosIniciados = valor
    }

    fun getGastosIniciados(): Boolean{
        return gastosIniciados
    }
}