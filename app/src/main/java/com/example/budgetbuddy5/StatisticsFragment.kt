package com.example.budgetbuddy5

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.echo.holographlibrary.PieGraph
import com.echo.holographlibrary.PieSlice
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import com.example.budgetbuddy5.dataClasses.Expense
import com.example.budgetbuddy5.dataClasses.ExpenseCategory
import com.example.budgetbuddy5.dataClasses.Income
import com.example.budgetbuddy5.dataClasses.IncomeCategory
import kotlinx.coroutines.launch
import ir.mahozad.android.PieChart


class StatisticsFragment : Fragment() {

    private var mostrarPorCategorias = false
    private lateinit var incomeList: MutableList<Income>
    private lateinit var expenseList: MutableList<Expense>
    private lateinit var pieChartTotal: PieChart
    private lateinit var pieChartIncome: PieChart
    private lateinit var pieChartExpense: PieChart
    private lateinit var tituloGrafico: TextView
    private lateinit var changeGraphButton: Button
    private lateinit var tituloIngresos: TextView
    private lateinit var tituloGastos: TextView

    private var incomeValor = 0f
    private var expenseValor = 0f


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

    private val categoryColors = mapOf(
        IncomeCategory.SALARIO.name to R.color.salario_color,
        IncomeCategory.TRABAJO.name to R.color.trabajo_color,
        IncomeCategory.INVERSIONES.name to R.color.inversiones_color,
        IncomeCategory.REGALOS.name to R.color.regalos_color,
        IncomeCategory.BIZUM.name to R.color.bizum_color,
        IncomeCategory.OTRO.name to R.color.otro_color,
        ExpenseCategory.PAGO.name to R.color.pago_color,
        ExpenseCategory.DEUDA.name to R.color.deuda_color
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        pieChartTotal = view.findViewById(R.id.pieChartTotal)
        pieChartIncome = view.findViewById(R.id.pieChartIngresos)
        pieChartExpense = view.findViewById(R.id.pieChartGastos)
        tituloGrafico = view.findViewById(R.id.tituloGrafico)
        changeGraphButton = view.findViewById(R.id.changeGraphButton)
        tituloIngresos = view.findViewById(R.id.tituloIngresos)
        tituloGastos = view.findViewById(R.id.tituloGastos)

        val tabbedActivity = requireActivity() as TabbedActivity

        // Mostrar el gráfico inicial con los valores totales de ingresos y gastos
        tabbedActivity.lifecycleScope.launch {
            incomeList = tabbedActivity.getIncomeList()
            expenseList = tabbedActivity.getExpenseList()

            //mostrarValoresTotales(pieGraph, incomeList, expenseList, container, legendLayout)
            mostrarGraficoTotal()


            // Configurar el listener del botón para cambiar el gráfico
            changeGraphButton.setOnClickListener {
                mostrarPorCategorias = !mostrarPorCategorias
                if (mostrarPorCategorias) {
                    tituloGrafico.text = "Gráfico Por Categorías"
                    mostrarGraficoIngresosPorCategoria()
                    mostrarGraficoGastosPorCategorias()
                    pieChartTotal.visibility = GONE
                    changeGraphButton.text = "Mostrar Balance Total"
                } else {
                    mostrarGraficoTotal()
                    tituloIngresos.visibility = GONE
                    tituloGastos.visibility = GONE
                }
            }

        }

        return view

    }
    private fun mostrarGraficoTotal() {

        tituloGrafico.text = "Gráfico Balance Total"
        changeGraphButton.text = "Balance por Categorías"

        incomeValor = calcularTotalIngresos(incomeList)
        expenseValor = calcularTotalGastos(expenseList)
        val totalValor = incomeValor + expenseValor

        val fraccionIngresos = incomeValor/totalValor
        val fraccionGastos = expenseValor/totalValor

        pieChartTotal.apply {
            slices = listOf(
                PieChart.Slice(
                    fraction = fraccionIngresos,
                    color = ContextCompat.getColor(requireContext(), R.color.soft_green),
                    label = "Ingresos",
                    labelSize = 16f,
                    labelColor = Color.BLACK,
                    outsideLabelMargin = 15f
                ),
                PieChart.Slice(
                    fraction = fraccionGastos,
                    color = ContextCompat.getColor(requireContext(), R.color.soft_red),
                    label = "Gastos",
                    labelSize = 16f,
                    labelColor = Color.BLACK,
                    outsideLabelMargin = 15f
                )
            )
            labelType = PieChart.LabelType.OUTSIDE
            holeRatio = 0.70f
        }

        pieChartTotal.visibility = VISIBLE
        pieChartIncome.visibility = GONE
        pieChartExpense.visibility = GONE
    }

    // Función para calcular el total de ingresos o gastos por categoría
    private fun calcularTotalPorCategoria(list: List<Any>): Map<String, Float> {
        val totalByCategory = mutableMapOf<String, Float>()
        for (item in list) {
            val category = if (item is Income) item.category else (item as Expense).category
            val amount = if (item is Income) item.amount.toFloat() else (item as Expense).amount.toFloat()
            totalByCategory[category] = totalByCategory.getOrDefault(category, 0f) + amount
        }
        return totalByCategory
    }

    // Función para obtener el color asociado a una categoría
    private fun getColorForCategory(category: String): Int {
        // Aquí puedes implementar la lógica para asignar colores a cada categoría
        // Por ejemplo, podrías usar un mapa para asignar un color específico a cada categoría
        // Y devolver el color correspondiente para la categoría dada
        return ContextCompat.getColor(requireContext(), categoryColors[category]!!)
    }

    private fun mostrarGraficoIngresosPorCategoria() {
        val incomeByCategory = calcularTotalPorCategoria(incomeList)

        tituloIngresos.visibility = VISIBLE

        val slices = incomeByCategory.map { (category, total) ->
            PieChart.Slice(
                fraction = total/incomeValor,
                color = getColorForCategory(category),
                label = category,
                labelSize = 16f,
                labelColor = Color.BLACK,
                outsideLabelMargin = 6f
            )
        }
        pieChartIncome.slices = slices
        pieChartIncome.labelType = PieChart.LabelType.OUTSIDE
        pieChartIncome.holeRatio = 0.7f
        pieChartIncome.visibility = VISIBLE
    }

    private fun mostrarGraficoGastosPorCategorias() {
        val expenseByCategory = calcularTotalPorCategoria(expenseList)

        tituloGastos.visibility = VISIBLE

        val slices = expenseByCategory.map { (category, total) ->
            PieChart.Slice(
                fraction = total/expenseValor,
                color = getColorForCategory(category),
                label = category,
                labelSize = 16f,
                labelColor = Color.BLACK,
                outsideLabelMargin = 6f
            )
        }
        pieChartExpense.slices = slices
        pieChartExpense.labelType = PieChart.LabelType.OUTSIDE
        pieChartExpense.holeRatio = 0.7f
        pieChartExpense.visibility = VISIBLE
    }

//    private fun mostrarPorCategoriasOTitulos(container: ViewGroup?) {
//        pieGraph.removeSlices()
//
//
//        // Crear un mapa para almacenar el total de ingresos por categoría
//        val incomeByCategory = HashMap<String, Float>()
//        for (income in incomeList) {
//            val category = income.category
//            val amount = income.amount.toFloat()
//            incomeByCategory[category] = incomeByCategory.getOrDefault(category, 0f) + amount
//        }
//
//        // Crear un mapa para almacenar el total de gastos por categoría
//        val expenseByCategory = HashMap<String, Float>()
//        for (expense in expenseList) {
//            val category = expense.category
//            val amount = expense.amount.toFloat()
//            expenseByCategory[category] = expenseByCategory.getOrDefault(category, 0f) + amount
//        }
//
//        // Obtener los colores base
//        val softGreenBase = ContextCompat.getColor(requireContext(), R.color.soft_green)
//        val softRedBase = ContextCompat.getColor(requireContext(), R.color.soft_red)
//
//        // Agregar las rebanadas al gráfico para los ingresos
//        var indexIngresos = 0
//        for ((category, amount) in incomeByCategory) {
//            val slice = PieSlice()
//            slice.title = category
//            slice.color = modificarTonalidad(softGreenBase, indexIngresos)
//            slice.value = amount
//            pieGraph.addSlice(slice)
//            indexIngresos++
//        }
//
//        // Agregar las rebanadas al gráfico para los gastos
//        var indexGastos = 0
//        for ((category, amount) in expenseByCategory) {
//            val slice = PieSlice()
//            slice.title = category
//            slice.color = modificarTonalidad(softRedBase, indexGastos)
//            slice.value = amount
//            pieGraph.addSlice(slice)
//            indexGastos++
//        }
//
//        addLegend(pieGraph, container, legendLayout)
//        pieGraph.visibility = View.VISIBLE
//    }
//
//    private fun modificarTonalidad(colorBase: Int, index: Int): Int {
//        val alpha = (255 - index * 25).coerceIn(0, 255) // Calcular el valor de opacidad
//        return ColorUtils.setAlphaComponent(colorBase, alpha)
//    }

    // Funciones para calcular los valores totales de ingresos y gastos
    private fun calcularTotalIngresos(incomeList: MutableList<Income>): Float {
        var totalIngresos = 0f

        for (income in incomeList) {
            totalIngresos += income.amount.toFloat()
        }

        return totalIngresos
    }

    private fun calcularTotalGastos(expenseList: MutableList<Expense>): Float {
        var totalGastos = 0f

        for (expense in expenseList) {
            totalGastos += expense.amount.toFloat()
        }

        return totalGastos
    }



//    private fun addLegend(pieGraph: PieGraph, container: ViewGroup?, legendLayout: GridLayout) {
//
//        legendLayout.removeAllViews() // Limpiar la vista anterior
//
//        for (slice in pieGraph.slices) {
//            // Crear una vista personalizada para mostrar el color y la etiqueta
//            val legendItem: View = LayoutInflater.from(context).inflate(R.layout.legend_item, container, false)
//
//            // Obtener referencias a los elementos de la vista personalizada
//            val imageView = legendItem.findViewById<ImageView>(R.id.colorView)
//            val labelTextView = legendItem.findViewById<TextView>(R.id.labelTextView)
//
//            // Establecer el color y la etiqueta correspondientes a este segmento
//            imageView.setBackgroundColor(slice.color)
//            labelTextView.setText(slice.title)
//
//            // Obtener el icono correspondiente a la categoría del slice
//            val categoryIcon = categoryIcons[slice.title]
//            if (categoryIcon != null) {
//                // Si se encuentra el icono, establecerlo en el ImageView
//                imageView.setImageResource(categoryIcon)
//            }
//
//
//            // Agregar la vista personalizada al diseño de la leyenda
//            legendLayout.addView(legendItem)
//        }
//
//        legendLayout.visibility = VISIBLE
//    }
}