import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy5.AddIncomeActivity
import com.example.budgetbuddy5.Income
import com.example.budgetbuddy5.R
import com.example.budgetbuddy5.TabbedActivity
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate

class IncomeFragment : Fragment() {

    private lateinit var tabbedActivity: TabbedActivity
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income, container, false)

        tabbedActivity = requireActivity() as TabbedActivity

        // Inicializar RecyclerView para los ingresos
        incomeAdapter = IncomeAdapter(tabbedActivity.getIncomeList())
        incomeRecyclerView = view.findViewById<RecyclerView>(R.id.incomeRecyclerView)
        incomeRecyclerView.adapter = incomeAdapter
        incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

//        // Observa los cambios en la lista de ingresos
//        viewModel.incomeList.observe(viewLifecycleOwner) { incomes ->
//            // Actualiza el RecyclerView cuando cambia la lista de ingresos
//            val incomeRecyclerView = view.findViewById<RecyclerView>(R.id.incomeRecyclerView)
//            val incomeAdapter = IncomeAdapter(incomes)
//            incomeRecyclerView.adapter = incomeAdapter
//            incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        }

        // Verifica si los ingresos ya han sido inicializados
        if(!tabbedActivity.getIngresosIniciados()) {
            // Inicializa los ingresos predefinidos
            inicializarIncomeRecycler(view)
            // Marca los ingresos como inicializados
            tabbedActivity.setIngresosIniciados(true)
        }

        //Recuperación de los datos de AddIncomeActivity
        val incomeIntent = requireActivity().intent
        if (incomeIntent.hasExtra("incomeTitle") && incomeIntent.hasExtra("incomeAmount") && incomeIntent.hasExtra("incomeCategory") && incomeIntent.hasExtra("incomeNote")) {
            val title = incomeIntent.getStringExtra("incomeTitle")
            val amount = incomeIntent.getStringExtra("incomeAmount")
            val category = incomeIntent.getStringExtra("incomeCategory")
            val note = incomeIntent.getStringExtra("incomeNote")
            val dateAux = incomeIntent.getStringExtra("incomeDate")
            val date = LocalDate.parse(dateAux)

            val income = Income(title!!, amount!!, category!!, note!!, date)

            tabbedActivity.addIncome(income)

            //Despues de recuperar los datos, viajo al tab correspondiente (indice 0 para ingresos)
            val tabLayout = tabbedActivity.findViewById<TabLayout>(R.id.tabs)
            tabLayout?.getTabAt(0)?.select()

            val incomeRecyclerView = view.findViewById<RecyclerView>(R.id.incomeRecyclerView)
            incomeRecyclerView.adapter?.notifyItemInserted(tabbedActivity.getIncomeList().size - 1) // Notificar al adaptador sobre el nuevo ingreso
            incomeRecyclerView.scrollToPosition(tabbedActivity.getIncomeList().size - 1)
        }

        return view
    }


    private fun inicializarIncomeRecycler(view: View) {

        //HACER UN GET A LA BASE DE DATOS

        // Previsualización ingresos
        val ingreso1 = Income("Nomina Enero", "30.0", "SALARIO", "hola", LocalDate.of(2023, 10, 4))
        tabbedActivity.addIncome(ingreso1)
        val ingreso2 = Income("Bizum Mamá", "10.0", "REGALOS", "hola", LocalDate.of(2023, 9, 4))
        tabbedActivity.addIncome(ingreso2)
        val ingreso3 = Income("Devolución Hacienda", "10.0", "INVERSIONES", "hola", LocalDate.of(2023, 10, 5))
        tabbedActivity.addIncome(ingreso3)
        val ingreso4 = Income("Devolución prestamo Rafa", "10.0", "BIZUM", "hola", LocalDate.of(2022, 10, 4))
        tabbedActivity.addIncome(ingreso4)

        tabbedActivity.getIncomeList().sortBy { it?.date }

        incomeRecyclerView.scrollToPosition(tabbedActivity.getIncomeList().size - 1)
    }
}

class IncomeAdapter(private val itemList: MutableList<Income?>) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Income?) {
            itemView.findViewById<TextView>(R.id.titleItemInTextView).text = item!!.title
            itemView.findViewById<TextView>(R.id.amountItemInTextView).text = "${item.amount} €"
            itemView.findViewById<TextView>(R.id.dateItemInTextView).text = "${item.date.dayOfMonth}/${item.date.monthValue}/${item.date.year}"
        }
    }
}


