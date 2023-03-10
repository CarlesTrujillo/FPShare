package cat.copernic.fpshare.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.adapters.ForoAdapter
import cat.copernic.fpshare.modelo.Foro
import cat.copernic.fpshare.databinding.FragmentListaForosBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Clase de la pantalla ListForo
 *
 * @author FPShare
 */
class ListaForos : Fragment(), ForoAdapter.OnItemClickListener {
    private var _binding: FragmentListaForosBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView : RecyclerView
    private var bd = FirebaseFirestore.getInstance()

    /**
     * Con esta función mostraremos el diseño de la pantalla ,mediante un View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaForosBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    /**
     * En esta función iniciamos  los diferentes elementos de la pantalla y creamos los listener de los eventos de los
     * elementos  de la vista
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //inicimos el item de recycleview
        recyclerView = binding.recyclerView

        lifecycleScope.launch(Dispatchers.Main) {
            async{

                //llamamos a la función
                llamarecycleview()
            }
        }
        //si pulsamos el boton flotante
        binding.fab.setOnClickListener {
            //cambiamos la pantalla a la pantalla a la de creación de foro
            val action =
                ListaForosDirections.actionListaForosToCreacionForo("1")
            view.findNavController().navigate(action)
        }

    }

    /**
     * Con est función destruimos la vista del fragemnt y limpiamos recursos para que el sistema funcione correctamente
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Con esta función recogemos los foros de la base de datos y los mostrsos mediante un recycleview
     */
    private suspend fun llamarecycleview() {

        //creamos un arrayList de tipo Foro
        val foroList = ArrayList<Foro>()

        //recogemos los foros de la base de datos
        val foros = bd.collection("Foros").get().await()
            for (document in foros){
                val wallitem = document.toObject(Foro::class.java)
                foroList.add(wallitem)
            }

            //ordenamos la lista por el id del foro
            foroList.sortWith(compareBy({ it.id }))

           //establecemos la vista de los items del recycleview en vertical
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

           //y asignamos el Foro adapter al adapter del recycleview, al Foroadapter cual le pasamos el arraylistv de
        // foros para crear los items que se mostraran en el rewcycleview
            recyclerView.adapter = ForoAdapter(foroList,this)

    }

    /**
     * Con esta función si un item de recycleview es clickado cambia de pantalla
     *
     * @param id
     */
    override fun onItemClick(id: String) {
        //al pulsar un item del recycleview cambiaremos de pantalla a la pantalla FPHilo
        val action =
            ListaForosDirections.actionListaForosToFPHilo(id, "1")
        view?.findNavController()?.navigate(action)
    }
}