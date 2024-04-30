package com.example.budgetbuddy5

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import com.example.budgetbuddy5.TabbedActivity
import com.google.android.material.tabs.TabLayout

class ExpenseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        val tabbedActivity = requireActivity() as TabbedActivity

        if(!tabbedActivity.getGastosIniciados()) {
            // Inicializa los ingresos predefinidos
            inicializarExpenseRecycler(view)
            tabbedActivity.setGastosIniciados(true)
        }


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

            tabbedActivity.addExpense(expense)

            //Despues de recuperar los datos, viajo al tab correspondiente (indice 1 para gastos)
            val tabLayout = tabbedActivity.findViewById<TabLayout>(R.id.tabs)
            tabLayout?.getTabAt(1)?.select()

            val expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
            expenseRecyclerView.adapter?.notifyItemInserted(tabbedActivity.getExpenseList().size - 1) // Notificar al adaptador sobre el nuevo ingreso
            expenseRecyclerView.scrollToPosition(tabbedActivity.getExpenseList().size - 1)
        }

        return view
    }

    private fun inicializarExpenseRecycler(view: View) {
        val tabbedActivity = requireActivity() as TabbedActivity

        //HACER UN GET A LA BASE DE DATOS

        // Previsualización gastos
        val gasto1 = Expense("Alquiler Enero", "10.0", "PAGO", "hola", LocalDate.of(2023, 12, 4))
        tabbedActivity.addExpense(gasto1)
        val gasto2 = Expense("Deuda María", "10.0", "BIZUM", "hola", LocalDate.of(2021, 1, 7))
        tabbedActivity.addExpense(gasto2)
        val gasto3 = Expense("Tabaco", "10.0", "INVERSIONES", "hola", LocalDate.of(2024, 4, 29))
        tabbedActivity.addExpense(gasto3)
        val gasto4 = Expense("Cena KFC", "10.0", "PAGO", "hola", LocalDate.of(2022, 11, 5))
        tabbedActivity.addExpense(gasto4)

        tabbedActivity.getExpenseList().sortBy { it?.date }

        // Inicializar RecyclerView para los gastos
        val expenseRecyclerView = view.findViewById<RecyclerView>(R.id.expenseRecyclerView)
        val expenseAdapter = ExpenseAdapter(tabbedActivity.getExpenseList())
        expenseRecyclerView.adapter = expenseAdapter
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        expenseRecyclerView.scrollToPosition(tabbedActivity.getExpenseList().size - 1)
    }

}

class ExpenseAdapter(private val itemList: MutableList<Expense?>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

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