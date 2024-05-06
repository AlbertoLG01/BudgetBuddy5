package com.example.budgetbuddy5

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import com.example.budgetbuddy5.dataClasses.Expense
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class ExpenseFragment : Fragment() {

    private lateinit var tabbedActivity: TabbedActivity
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var expenseList: MutableList<Expense>

    private lateinit var ascendingButton: ImageButton
    private lateinit var descendingButton: ImageButton
    private lateinit var orderSpinner: Spinner
    private var esAscendente : Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        tabbedActivity = requireActivity() as TabbedActivity

        // Inicializar RecyclerView para los gastos

        tabbedActivity.lifecycleScope.launch {
            //val gasto1 = Expense("Alquiler Enero", "10.0", "PAGO", "hola", LocalDate.of(2023, 12, 4))
            //tabbedActivity.addExpense(gasto1)
            expenseList = tabbedActivity.getExpenseList()
            expenseAdapter = ExpenseAdapter(expenseList, tabbedActivity)
            expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
            expenseRecyclerView.adapter = expenseAdapter
            expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            //Filtros
            ascendingButton = view.findViewById(R.id.btnExAscending)
            descendingButton = view.findViewById(R.id.btnExDescending)
            orderSpinner = view.findViewById(R.id.spinnerExOrder)

            configurarListenersBotones()
            configurarSpinner()
        }

        //Recuperación de los datos de AddExpenseActivity
        val expenseIntent = requireActivity().intent
        if (expenseIntent.hasExtra("expenseTitle") && expenseIntent.hasExtra("expenseAmount") && expenseIntent.hasExtra("expenseCategory") && expenseIntent.hasExtra("expenseNote")) {
            val title = expenseIntent.getStringExtra("expenseTitle")
            val amount = expenseIntent.getStringExtra("expenseAmount")
            val category = expenseIntent.getStringExtra("expenseCategory")
            val note = expenseIntent.getStringExtra("expenseNote")
            val dateAux = expenseIntent.getStringExtra("expenseDate")
            val date = LocalDate.parse(dateAux)

            val expense = Expense(title!!, amount!!, category!!, note!!, date)

            tabbedActivity.lifecycleScope.launch {
                tabbedActivity.addExpense(expense)

                //Despues de recuperar los datos, viajo al tab correspondiente (indice 1 para gastos)
                val tabLayout = tabbedActivity.findViewById<TabLayout>(R.id.tabs)
                tabLayout?.getTabAt(1)?.select()

                val expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
                expenseRecyclerView.adapter?.notifyItemInserted(expenseList.size - 1) // Notificar al adaptador sobre el nuevo ingreso
                expenseRecyclerView.scrollToPosition(expenseList.size - 1)
            }

        }

        return view
    }

    private fun configurarListenersBotones(){

        ascendingButton.setOnClickListener {

            if(esAscendente != true) {
                // Cambiar el estado de los botones
                esAscendente = true

                // Cambiar el fondo de los botones
                ascendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.darker_gray
                    )
                )
                descendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )

                // Ordenar la lista según el valor del spinner
                sortListBySpinnerSelection()
            }
            else{
                esAscendente = null
                ascendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            }
        }

        descendingButton.setOnClickListener {

            if(esAscendente!=false) {
                // Cambiar el estado de los botones
                esAscendente = false

                // Cambiar el fondo de los botones
                descendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.darker_gray
                    )
                )
                ascendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )

                // Ordenar la lista según el valor del spinner
                sortListBySpinnerSelection()
            }
            else{
                esAscendente=null
                descendingButton.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            }
        }
    }

    private fun sortListBySpinnerSelection() {
        // Obtener el ítem seleccionado del spinner
        val selectedItem = orderSpinner.selectedItem as String

        // Ordenar la lista según el ítem seleccionado y el estado de los botones
        when (selectedItem) {
            "A-Z" -> {
                if(esAscendente!=null) {
                    if (esAscendente as Boolean) {
                        expenseList.sortBy { it.title }
                    } else {
                        expenseList.sortByDescending { it.title }
                    }
                }
            }
            "€" -> {
                if(esAscendente!=null) {
                    if (esAscendente as Boolean) {
                        expenseList.sortBy { it.amount.toFloat() }
                    } else {
                        expenseList.sortByDescending { it.amount.toFloat() }
                    }
                }
            }
            "Fecha" -> {
                if(esAscendente!=null) {
                    if (esAscendente as Boolean) {
                        expenseList.sortBy { it.date }
                    } else {
                        expenseList.sortByDescending { it.date }
                    }
                }
            }
        }

        // Notificar al adaptador de que los datos han cambiado
        tabbedActivity.lifecycleScope.launch {
            expenseAdapter.notifyDataSetChanged()
            expenseRecyclerView.scrollToPosition(expenseList.size - 1)
        }
    }

    private fun configurarSpinner() {
        // Crear un adaptador para el Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.order_options,  // Define el array de opciones en strings.xml
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Establecer el adaptador en el Spinner
        orderSpinner.adapter = spinnerAdapter

        // Configurar el evento de selección del Spinner
        orderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sortListBySpinnerSelection()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se hace nada si no se selecciona ningún elemento
            }
        }
    }

}

class ExpenseAdapter(private val itemList: MutableList<Expense>, private val tabbedActivity: TabbedActivity) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)

        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.deleteExButton)

        // Configurar el clic del botón de eliminación
        deleteButton.setOnClickListener {
            val context = holder.itemView.context
            showDeleteConfirmationDialog(context) {
                // Si el usuario confirma la eliminación
                val position = holder.adapterPosition
                tabbedActivity.lifecycleScope.launch {
                    tabbedActivity.deleteExpense(currentItem)
                    itemList.removeAt(position)
                    super.notifyItemRemoved(position)
                }
            }
        }


    }

    private fun showDeleteConfirmationDialog(context: Context, onDeleteConfirmed: () -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirmar eliminación")
        alertDialogBuilder.setMessage("¿Estás seguro de que deseas eliminar este gasto?")
        alertDialogBuilder.setPositiveButton("Sí") { dialog, _ ->
            onDeleteConfirmed.invoke() // Llamar a la función de eliminación si el usuario confirma
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun getItemCount() = itemList.size

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Expense?) {
            itemView.findViewById<TextView>(R.id.titleItemExTextView).text = item!!.title
            itemView.findViewById<TextView>(R.id.amountItemExTextView).text = "${item.amount} €"
            itemView.findViewById<TextView>(R.id.dateItemExTextView).text = String.format("%02d/%02d/%d", item.date.dayOfMonth, item.date.monthValue, item.date.year)

        }
    }
}