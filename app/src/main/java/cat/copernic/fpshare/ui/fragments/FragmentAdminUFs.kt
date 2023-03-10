package cat.copernic.fpshare.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.copernic.fpshare.R
import cat.copernic.fpshare.adapters.UfAdminAdapter
import cat.copernic.fpshare.databinding.FragmentAdminUFsBinding
import cat.copernic.fpshare.modelo.Uf
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Fragment de pantalla de Administración de UFs
 *
 * @author FPShare
 */
class FragmentAdminUFs : Fragment(), UfAdminAdapter.OnItemClickListener {

    // Binding
    private var _binding: FragmentAdminUFsBinding? = null
    private val binding get() = _binding!!

    // Firebase
    private val bd = FirebaseFirestore.getInstance()

    private lateinit var ufList: Deferred<MutableList<Uf>>
    private lateinit var adapterU: UfAdminAdapter

    // Botones
    private lateinit var botonAddUF: Button
    private lateinit var botonEditModulo: Button
    private lateinit var botonDeleteModulo: Button

    private lateinit var recyclerViewUF: RecyclerView

    // Args
    private val args: FragmentAdminUFsArgs by navArgs()

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
        _binding = FragmentAdminUFsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * En esta función iniciamos  los diferentes elementos de la pantalla y creamos los listener de los eventos de los
     * elementos  de la vista
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inicializadoresButton()
        inicializadoresRW()
        listeners()

        // Corrutina para cargar las UFs
        lifecycleScope.launch(Dispatchers.Main) {
            ufList = async { crearUF() }
        }
    }

    /**
     * Con esta función destruimos la vista del fragemnt y limpiamos recursos para que el
     * sistema funcione correctamente
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inicializadoresButton() {
        // inicializar botones de UF
        botonAddUF = binding.buttonAddUF
        botonEditModulo = binding.btnEditModule
        botonDeleteModulo = binding.btnDeleteModule
    }

    private fun inicializadoresRW() {
        recyclerViewUF = binding.recyclerViewUFs
    }

    private fun listeners() {
        /**
         * Botón para enviar al usuario a la pantalla CrearUF para que pueda crear una
         * nueva UF en la base de datos
         */
        botonAddUF.setOnClickListener {
            val action =
                FragmentAdminUFsDirections.actionFragmentAdminUFsToCrearUF(
                    args.idCiclo,
                    args.idModulo
                )
            view?.findNavController()?.navigate(action)
        }
        botonEditModulo.setOnClickListener {
            val action =
                FragmentAdminUFsDirections.actionFragmentAdminUFsToFragmentAdminEditModule(
                    args.idModulo,
                    args.idCiclo
                )
            view?.findNavController()?.navigate(action)
        }
        botonDeleteModulo.setOnClickListener {
            borrarModulo()
            val action =
                FragmentAdminUFsDirections.actionFragmentAdminUFsToFragmentAdminModulos(args.idCiclo)
            view?.findNavController()?.navigate(action)
        }
    }

    /**
     * Función para la lectura de UFs dentro de un ciclo y un modulo especificado en anteriores
     * fragments
     *
     * @return ufList
     */
    private suspend fun crearUF(): MutableList<Uf> {
        val ufList = mutableListOf<Uf>()
        val idCiclo = args.idCiclo
        val idModulo = args.idModulo

        val ufs = bd.collection("Ciclos").document(idCiclo).collection("Modulos")
            .document(idModulo).collection("UFs")
            .get().await()
        for (document in ufs) {
            val idUf = document.id
            val nombreUf = document["nombre"].toString()
            val uf = Uf(
                idUf,
                nombreUf
            )
            ufList.add(uf)
        }
        adapterU = UfAdminAdapter(ufList, this)
        binding.recyclerViewUFs.adapter = adapterU
        binding.recyclerViewUFs.layoutManager = LinearLayoutManager(requireContext())

        return ufList
    }

    /**
     * Función para borrar el modulo en el que nos encontramos
     */
    private fun borrarModulo() {
        bd.collection("Ciclos").document(args.idCiclo).collection("Modulos").document(args.idModulo)
            .delete().addOnSuccessListener {
                Toast.makeText(
                    context,
                    getString(R.string.moduloBorradoCorrecto),
                    Toast.LENGTH_LONG
                ).show()
            }.addOnFailureListener {
                Toast.makeText(context, getString(R.string.moduloBorradoError), Toast.LENGTH_LONG)
                    .show()
            }
    }

    /**
     * OnItemClick que lleva las IDs de idCiclo, idModulo y idUF hacia la pantalla de Publicaciones
     * para mostrar las publicaciones que se encuentran dentro de la UF especificada
     *
     * @param id
     */
    override fun onItemClick(id: String) {
        val view = binding.root
        val action = FragmentAdminUFsDirections.actionFragmentAdminUFsToFragmentAdminPosts(
            args.idCiclo,
            args.idModulo,
            id
        )
        view.findNavController().navigate(action)
    }
}