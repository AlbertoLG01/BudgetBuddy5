package com.example.budgetbuddy5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            expenseAdapter = ExpenseAdapter(expenseList)
            expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
            expenseRecyclerView.adapter = expenseAdapter
            expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

//        if(!tabbedActivity.getGastosIniciados()) {
//            // Inicializa los ingresos predefinidos
//            inicializarExpenseRecycler(view)
//            tabbedActivity.setGastosIniciados(true)
//        }


//        // Observa los cambios en la lista de gastos
//        viewModel.expenseList.observe(viewLifecycleOwner) { expenses ->
//            // Actualiza el RecyclerView cuando cambia la lista de gastos
//            val expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
//            val expenseAdapter = ExpenseAdapter(expenses)
//            expenseRecyclerView.adapter = expenseAdapter
//            expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        }

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
            }

            //Despues de recuperar los datos, viajo al tab correspondiente (indice 1 para gastos)
            val tabLayout = tabbedActivity.findViewById<TabLayout>(R.id.tabs)
            tabLayout?.getTabAt(1)?.select()

            val expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
            expenseRecyclerView.adapter?.notifyItemInserted(expenseList.size - 1) // Notificar al adaptador sobre el nuevo ingreso
            expenseRecyclerView.scrollToPosition(expenseList.size - 1)
        }

        return view
    }

    private fun inicializarExpenseRecycler(view: View) {

        //HACER UN GET A LA BASE DE DATOS

//        // Previsualización gastos
//        val gasto1 = Expense("Alquiler Enero", "10.0", "PAGO", "hola", LocalDate.of(2023, 12, 4))
//        tabbedActivity.addExpense(gasto1)
//        val gasto2 = Expense("Deuda María", "10.0", "BIZUM", "hola", LocalDate.of(2021, 1, 7))
//        tabbedActivity.addExpense(gasto2)
//        val gasto3 = Expense("Tabaco", "10.0", "INVERSIONES", "hola", LocalDate.of(2024, 4, 29))
//        tabbedActivity.addExpense(gasto3)
//        val gasto4 = Expense("Cena KFC", "10.0", "PAGO", "hola", LocalDate.of(2022, 11, 5))
//        tabbedActivity.addExpense(gasto4)

        expenseList.sortBy { it?.date }


        expenseRecyclerView.scrollToPosition(expenseList.size - 1)
    }

}

class ExpenseAdapter(private val itemList: MutableList<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Expense?) {
            itemView.findViewById<TextView>(R.id.titleItemExTextView).text = item!!.title
            itemView.findViewById<TextView>(R.id.amountItemExTextView).text = "${item.amount} €"
            itemView.findViewById<TextView>(R.id.dateItemExTextView).text = "${item.date.dayOfMonth}/${item.date.monthValue}/${item.date.year}"
        }
    }
}