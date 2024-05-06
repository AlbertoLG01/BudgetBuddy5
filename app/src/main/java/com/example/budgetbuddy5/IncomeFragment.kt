import android.content.Context
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy5.dataClasses.Income
import com.example.budgetbuddy5.R
import com.example.budgetbuddy5.TabbedActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.time.LocalDate

class IncomeFragment : Fragment() {

    private lateinit var tabbedActivity: TabbedActivity
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeRecyclerView: RecyclerView
    private lateinit var incomeList: MutableList<Income>

    private lateinit var ascendingButton: ImageButton
    private lateinit var descendingButton: ImageButton
    private lateinit var orderSpinner: Spinner
    private var esAscendente : Boolean? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income, container, false)

        tabbedActivity = requireActivity() as TabbedActivity

        // Inicializar RecyclerView para los ingresos
        tabbedActivity.lifecycleScope.launch {
            incomeList = tabbedActivity.getIncomeList()
            incomeAdapter = IncomeAdapter(incomeList, tabbedActivity)
            incomeRecyclerView = view.findViewById<RecyclerView>(R.id.incomeRecyclerView)
            incomeRecyclerView.adapter = incomeAdapter
            incomeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

            //Filtros
            ascendingButton = view.findViewById(R.id.btnInAscending)
            descendingButton = view.findViewById(R.id.btnInDescending)
            orderSpinner = view.findViewById(R.id.spinnerInOrder)

            configurarListenersBotones()
            configurarSpinner()

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

            tabbedActivity.lifecycleScope.launch {
                tabbedActivity.addIncome(income)

                //Despues de recuperar los datos, viajo al tab correspondiente (indice 0 para ingresos)
                val tabLayout = tabbedActivity.findViewById<TabLayout>(R.id.tabs)
                tabLayout?.getTabAt(0)?.select()

                val incomeRecyclerView = view.findViewById<RecyclerView>(R.id.incomeRecyclerView)
                incomeRecyclerView.adapter?.notifyItemInserted(incomeList.size - 1) // Notificar al adaptador sobre el nuevo ingreso
                incomeRecyclerView.scrollToPosition(incomeList.size - 1)
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
                        incomeList.sortBy { it.title }
                    } else {
                        incomeList.sortByDescending { it.title }
                    }
                }
            }
            "€" -> {
                if(esAscendente!=null) {
                    if (esAscendente as Boolean) {
                        incomeList.sortBy { it.amount }
                    } else {
                        incomeList.sortByDescending { it.amount }
                    }
                }
            }
            "Fecha" -> {
                if(esAscendente!=null) {
                    if (esAscendente as Boolean) {
                        incomeList.sortBy { it.date }
                    } else {
                        incomeList.sortByDescending { it.date }
                    }
                }
            }
        }

        // Notificar al adaptador de que los datos han cambiado
        tabbedActivity.lifecycleScope.launch {
            incomeAdapter.notifyDataSetChanged()
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


    private fun inicializarIncomeRecycler(view: View) {

        //HACER UN GET A LA BASE DE DATOS

//        // Previsualización ingresos
//        val ingreso1 = Income("Nomina Enero", "30.0", "SALARIO", "hola", LocalDate.of(2023, 10, 4))
//        tabbedActivity.addIncome(ingreso1)
//        val ingreso2 = Income("Bizum Mamá", "10.0", "REGALOS", "hola", LocalDate.of(2023, 9, 4))
//        tabbedActivity.addIncome(ingreso2)
//        val ingreso3 = Income("Devolución Hacienda", "10.0", "INVERSIONES", "hola", LocalDate.of(2023, 10, 5))
//        tabbedActivity.addIncome(ingreso3)
//        val ingreso4 = Income("Devolución prestamo Rafa", "10.0", "BIZUM", "hola", LocalDate.of(2022, 10, 4))
//        tabbedActivity.addIncome(ingreso4)

        incomeList.sortBy { it?.date }

        incomeRecyclerView.scrollToPosition(incomeList.size - 1)
    }
}

class IncomeAdapter(private val itemList: MutableList<Income>, private val tabbedActivity: TabbedActivity) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)

        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.deleteInButton)

        // Configurar el clic del botón de eliminación
        deleteButton.setOnClickListener {
            val context = holder.itemView.context
            showDeleteConfirmationDialog(context) {
                // Si el usuario confirma la eliminación
                val position = holder.adapterPosition
                tabbedActivity.lifecycleScope.launch {
                    tabbedActivity.deleteIncome(currentItem)
                    itemList.removeAt(position)
                    super.notifyItemRemoved(position)
                }
            }
        }


    }

    private fun showDeleteConfirmationDialog(context: Context, onDeleteConfirmed: () -> Unit) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Confirmar eliminación")
        alertDialogBuilder.setMessage("¿Estás seguro de que deseas eliminar este ingreso?")
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

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Income?) {
            itemView.findViewById<TextView>(R.id.titleItemInTextView).text = item!!.title
            itemView.findViewById<TextView>(R.id.amountItemInTextView).text = "${item.amount} €"
            itemView.findViewById<TextView>(R.id.dateItemInTextView).text = String.format("%02d/%02d/%d", item.date.dayOfMonth, item.date.monthValue, item.date.year)
        }
    }
}


