package com.example.budgetbuddy5

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.budgetbuddy5.ui.main.SectionsPagerAdapter
import com.example.budgetbuddy5.databinding.ActivityTabbedBinding

class TabbedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabbedBinding

    private val incomeList = mutableListOf<Income?>()
    private val expenseList = mutableListOf<Expense?>()
    private var ingresosIniciados = false
    private var gastosIniciados = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabbedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab


        // Agregar un listener para detectar cambios de página en el ViewPager
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


    fun getIncomeList(): MutableList<Income?> {
        return incomeList
    }

    fun getExpenseList(): MutableList<Expense?> {
        return expenseList
    }

    fun addIncome(income: Income) {
        incomeList.add(income)
    }

    fun addExpense(expense: Expense) {
        expenseList.add(expense)
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