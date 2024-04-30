package com.example.budgetbuddy5

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import androidx.core.graphics.ColorUtils
import com.google.android.material.snackbar.Snackbar


class StatisticsFragment : Fragment() {

    private var showTotalValues = true

    private val categoryIcons = mapOf(
        IncomeCategory.SALARIO.name to R.drawable.ic_salario,
        IncomeCategory.TRABAJO.name to R.drawable.ic_trabajo,
        IncomeCategory.INVERSIONES.name to R.drawable.ic_inversiones,
        IncomeCategory.REGALOS.name to R.drawable.ic_regalos,
        IncomeCategory.BIZUM.name to R.drawable.ic_bizum,
        IncomeCategory.OTRO.name to R.drawable.ic_otro,
        ExpenseCategory.PAGO.name to R.drawable.ic_pago,
        ExpenseCategory.DEUDA.name to R.drawable.ic_deuda
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        val pieGraph = view.findViewById<PieGraph>(R.id.pieGraph)
        val changeGraphButton = view.findViewById<Button>(R.id.changeGraphButton)
        val legendLayout = view.findViewById<GridLayout>(R.id.legendLayout)

        val tabbedActivity = requireActivity() as TabbedActivity

        // Mostrar el gráfico inicial con los valores totales de ingresos y gastos
        val incomeList = tabbedActivity.getIncomeList()
        val expenseList = tabbedActivity.getExpenseList()

        mostrarValoresTotales(pieGraph, incomeList, expenseList, container, legendLayout)


        // Configurar el listener del botón para cambiar el gráfico
        changeGraphButton.setOnClickListener {
            if (showTotalValues) {
                mostrarPorCategoriasOTitulos(pieGraph, incomeList, expenseList, container, legendLayout)
            } else {
                mostrarValoresTotales(pieGraph, incomeList, expenseList, container, legendLayout)
            }
            showTotalValues = !showTotalValues
        }

        return view
    }

    private fun mostrarValoresTotales(pieGraph: PieGraph, incomeList: MutableList<Income?>, expenseList: MutableList<Expense?>, container: ViewGroup?, legendLayout: GridLayout) {
        pieGraph.removeSlices()

        val softGreen = ContextCompat.getColor(requireContext(), R.color.soft_green)
        val softRed = ContextCompat.getColor(requireContext(), R.color.soft_red)

        // Crear las rebanadas para el gráfico
        val sliceIngresos = PieSlice()
        sliceIngresos.title = "Ingresos"
        sliceIngresos.color = softGreen
        sliceIngresos.value = calcularTotalIngresos(incomeList)
        pieGraph.addSlice(sliceIngresos)

        val sliceGastos = PieSlice()
        sliceGastos.title = "Gastos"
        sliceGastos.color = softRed
        sliceGastos.value = calcularTotalGastos(expenseList)
        pieGraph.addSlice(sliceGastos)

        addLegend(pieGraph, container, legendLayout)
        pieGraph.visibility = View.VISIBLE
    }

    private fun mostrarPorCategoriasOTitulos(pieGraph: PieGraph, incomeList: MutableList<Income?>, expenseList: MutableList<Expense?>, container: ViewGroup?, legendLayout: GridLayout) {
        pieGraph.removeSlices()


        // Crear un mapa para almacenar el total de ingresos por categoría
        val incomeByCategory = HashMap<String, Float>()
        for (income in incomeList) {
            val category = income?.category
            val amount = income?.amount?.toFloat()
            incomeByCategory[category!!] = incomeByCategory.getOrDefault(category, 0f) + amount!!
        }

        // Crear un mapa para almacenar el total de gastos por categoría
        val expenseByCategory = HashMap<String, Float>()
        for (expense in expenseList) {
            val category = expense?.category
            val amount = expense?.amount?.toFloat()
            expenseByCategory[category!!] = expenseByCategory.getOrDefault(category, 0f) + amount!!
        }

        // Obtener los colores base
        val softGreenBase = ContextCompat.getColor(requireContext(), R.color.soft_green)
        val softRedBase = ContextCompat.getColor(requireContext(), R.color.soft_red)

        // Agregar las rebanadas al gráfico para los ingresos
        var indexIngresos = 0
        for ((category, amount) in incomeByCategory) {
            val slice = PieSlice()
            slice.title = category
            slice.color = modificarTonalidad(softGreenBase, indexIngresos)
            slice.value = amount
            pieGraph.addSlice(slice)
            indexIngresos++
        }

        // Agregar las rebanadas al gráfico para los gastos
        var indexGastos = 0
        for ((category, amount) in expenseByCategory) {
            val slice = PieSlice()
            slice.title = category
            slice.color = modificarTonalidad(softRedBase, indexGastos)
            slice.value = amount
            pieGraph.addSlice(slice)
            indexGastos++
        }

        addLegend(pieGraph, container, legendLayout)
        pieGraph.visibility = View.VISIBLE
    }

    private fun modificarTonalidad(colorBase: Int, index: Int): Int {
        val alpha = (255 - index * 25).coerceIn(0, 255) // Calcular el valor de opacidad
        return ColorUtils.setAlphaComponent(colorBase, alpha)
    }

    // Funciones para calcular los valores totales de ingresos y gastos
    private fun calcularTotalIngresos(incomeList: MutableList<Income?>): Float {
        var totalIngresos = 0f

        for (income in incomeList) {
            totalIngresos += income!!.amount.toFloat()
        }

        return totalIngresos
    }

    private fun calcularTotalGastos(expenseList: MutableList<Expense?>): Float {
        var totalGastos = 0f

        for (expense in expenseList) {
            totalGastos += expense!!.amount.toFloat()
        }

        return totalGastos
    }

    private fun addLegend(pieGraph: PieGraph, container: ViewGroup?, legendLayout: GridLayout) {

        legendLayout.removeAllViews() // Limpiar la vista anterior

        for (slice in pieGraph.slices) {
            // Crear una vista personalizada para mostrar el color y la etiqueta
            val legendItem: View = LayoutInflater.from(context).inflate(R.layout.legend_item, container, false)

            // Obtener referencias a los elementos de la vista personalizada
            val imageView = legendItem.findViewById<ImageView>(R.id.colorView)
            val labelTextView = legendItem.findViewById<TextView>(R.id.labelTextView)

            // Establecer el color y la etiqueta correspondientes a este segmento
            imageView.setBackgroundColor(slice.color)
            labelTextView.setText(slice.title)

            // Obtener el icono correspondiente a la categoría del slice
            val categoryIcon = categoryIcons[slice.title]
            if (categoryIcon != null) {
                // Si se encuentra el icono, establecerlo en el ImageView
                imageView.setImageResource(categoryIcon)
            }


            // Agregar la vista personalizada al diseño de la leyenda
            legendLayout.addView(legendItem)
        }
    }
}